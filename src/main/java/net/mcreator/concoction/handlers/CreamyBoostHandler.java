package net.mcreator.concoction.handlers;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.*;

/**
 * CREAMY: разовый буст уровней других эффектов.
 * - При добавлении CREAMY бустит текущие эффекты.
 * - Пока CREAMY активен, на каждом тике бустит любой новый эффект, который ещё не был усилен.
 * - Один и тот же тип эффекта бустится не более одного раза (память держится до исчезновения самого усиленного эффекта).
 * - Сам CREAMY никогда не бустится и не повышает сам себя.
 * - Память об усиленных эффектах сохраняется в NBT игрока, переживает перезаход и клоны игрока.
 */
@EventBusSubscriber
public class CreamyBoostHandler {

    /** защита от рекурсии, когда мы сами remove/add эффекты */
    private static final ThreadLocal<Boolean> REENTRY = ThreadLocal.withInitial(() -> false);

    /** кэш: для каждого игрока — набор id эффектов, уже усиленных текущим “жизненным циклом” этих эффектов */
    private static final Map<UUID, Set<String>> BOOSTED = new HashMap<>();

    /** ключ в PersistentData игрока */
    private static final String NBT_KEY = "concoction_creamy_boosted";

    // -------------------- утилиты ключей/наборов --------------------

    /** строковый ключ эффекта (minecraft:xxx) либо null */
    private static String keyOf(Holder<MobEffect> holder) {
        return holder.unwrapKey().map(k -> k.location().toString()).orElse(null);
    }

    /** получить кэш-набор (RAM); если его нет — подгрузить из NBT */
    private static Set<String> getBoostedSet(LivingEntity e) {
        return BOOSTED.computeIfAbsent(e.getUUID(), u -> new HashSet<>(readPersisted(e)));
    }

    /** записать кэш в карту + синкнуть в NBT */
    private static void setBoostedSet(LivingEntity e, Set<String> set) {
        BOOSTED.put(e.getUUID(), set);
        writePersisted(e, set);
    }

    private static boolean wasBoosted(LivingEntity e, Holder<MobEffect> holder) {
        String id = keyOf(holder);
        if (id == null) return true; // перестраховка
        return getBoostedSet(e).contains(id);
    }

    private static void markBoosted(LivingEntity e, Holder<MobEffect> holder) {
        String id = keyOf(holder);
        if (id == null) return;
        Set<String> set = getBoostedSet(e);
        if (set.add(id)) setBoostedSet(e, set);
    }

    private static void unmarkBoosted(LivingEntity e, Holder<MobEffect> holder) {
        String id = keyOf(holder);
        if (id == null) return;
        Set<String> set = getBoostedSet(e);
        if (set.remove(id)) setBoostedSet(e, set);
    }

    // -------------------- Persisted storage (NBT) --------------------

    private static Set<String> readPersisted(LivingEntity e) {
        CompoundTag tag = e.getPersistentData();
        Set<String> out = new HashSet<>();
        if (tag != null && tag.contains(NBT_KEY, Tag.TAG_LIST)) {
            ListTag list = tag.getList(NBT_KEY, Tag.TAG_STRING);
            for (Tag t : list) {
                if (t instanceof StringTag st) out.add(st.getAsString());
            }
        }
        return out;
    }

    private static void writePersisted(LivingEntity e, Set<String> set) {
        CompoundTag tag = e.getPersistentData();
        ListTag list = new ListTag();
        for (String s : set) list.add(StringTag.valueOf(s));
        tag.put(NBT_KEY, list);
    }

    // -------------------- события жизни игрока --------------------

    /** При логине — подтянуть persisted набор в кэш (на случай холодного старта) */
    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player p = event.getEntity();
        if (p == null || p.level().isClientSide()) return;
        BOOSTED.put(p.getUUID(), readPersisted(p));
    }

    /** При клоне (смерть/измерение) — перенести NBT, обновить кэш */
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        Player oldP = event.getOriginal();
        Player newP = event.getEntity();
        if (newP == null || newP.level().isClientSide()) return;
        if (oldP == null) return;

        CompoundTag oldTag = oldP.getPersistentData();
        if (oldTag != null && oldTag.contains(NBT_KEY, Tag.TAG_LIST)) {
            newP.getPersistentData().put(NBT_KEY, oldTag.getList(NBT_KEY, Tag.TAG_STRING).copy());
        }
        BOOSTED.put(newP.getUUID(), readPersisted(newP));
    }

    // -------------------- реакции на эффекты --------------------

    /** Снятие эффектов: если снят НЕ CREAMY — снимаем «усиленный» флаг для этого типа */
    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        LivingEntity e = event.getEntity();
        if (e == null || e.level().isClientSide()) return;

        Holder<MobEffect> removed = event.getEffect();
        if (!removed.is(ConcoctionModMobEffects.CREAMY)) {
            // эффект закончился — можно будет вновь усилить при новом появлении
            unmarkBoosted(e, removed);
        }
        // снятие CREAMY здесь память НЕ очищает — так и задумано
    }

    /** Истёкший эффект: зеркалим логику Remove (не все моды триггерят Remove одинаково) */
    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        LivingEntity e = event.getEntity();
        if (e == null || e.level().isClientSide()) return;

        MobEffectInstance inst = event.getEffectInstance();
        if (inst == null) return;
        Holder<MobEffect> expired = inst.getEffect();

        if (!expired.is(ConcoctionModMobEffects.CREAMY)) {
            unmarkBoosted(e, expired);
        }
    }

    /** Добавление CREAMY — разово бустим все текущие эффекты */
    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        if (Boolean.TRUE.equals(REENTRY.get())) return;

        LivingEntity entity = event.getEntity();
        MobEffectInstance added = event.getEffectInstance();
        if (entity == null || added == null || entity.level().isClientSide()) return;

        Holder<MobEffect> addedHolder = added.getEffect();
        if (!addedHolder.is(ConcoctionModMobEffects.CREAMY)) return;

        int bonus = added.getAmplifier() + 1;

        // защитим сам CREAMY
        markBoosted(entity, ConcoctionModMobEffects.CREAMY);

        // моментально бустим всё, что уже висит
        List<MobEffectInstance> snapshot = new ArrayList<>(entity.getActiveEffects());
        for (MobEffectInstance inst : snapshot) {
            if (inst == added) continue;
            Holder<MobEffect> h = inst.getEffect();
            if (h.is(ConcoctionModMobEffects.CREAMY)) continue;
            if (wasBoosted(entity, h)) continue;

            boostOnce(entity, inst, bonus);
            markBoosted(entity, h);
        }
    }

    /** Пока CREAMY активен — бустим любые новые эффекты, появившиеся ПОСЛЕ него */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player == null || player.level().isClientSide()) return;

        // синхронизируем кэш с NBT иногда (на случай внешних правок)
        BOOSTED.computeIfAbsent(player.getUUID(), u -> new HashSet<>(readPersisted(player)));

        MobEffectInstance creamy = player.getEffect(ConcoctionModMobEffects.CREAMY);
        if (creamy == null) return;

        int bonus = creamy.getAmplifier() + 1;

        // сам CREAMY никогда не бустим
        markBoosted(player, ConcoctionModMobEffects.CREAMY);

        // проверяем активные эффекты — бустим те, которых ещё нет в «усиленных»
        List<MobEffectInstance> snapshot = new ArrayList<>(player.getActiveEffects());
        for (MobEffectInstance inst : snapshot) {
            Holder<MobEffect> h = inst.getEffect();
            if (h.is(ConcoctionModMobEffects.CREAMY)) continue;
            if (wasBoosted(player, h)) continue;

            boostOnce(player, inst, bonus);
            markBoosted(player, h);
        }
    }

    // -------------------- буст одного инстанса --------------------

    /** Выполнить разовое повышение уровня конкретного инстанса эффекта */
    private static void boostOnce(LivingEntity entity, MobEffectInstance inst, int bonus) {
        if (Boolean.TRUE.equals(REENTRY.get())) return;

        Holder<MobEffect> h = inst.getEffect();

        MobEffectInstance upgraded = new MobEffectInstance(
                h,
                inst.getDuration(),
                inst.getAmplifier() + bonus,
                inst.isAmbient(),
                inst.isVisible(),
                inst.showIcon()
        );

        REENTRY.set(true);
        try {
            entity.removeEffect(h);
            entity.addEffect(upgraded);
        } finally {
            REENTRY.set(false);
        }
    }
}
