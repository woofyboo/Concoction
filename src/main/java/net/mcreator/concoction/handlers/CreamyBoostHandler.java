package net.mcreator.concoction.handlers;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

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
 * - "Память" об усиленных типах хранится в NBT и живёт до снятия САМИХ эффектов (переживает выход/вход).
 * - Сам CREAMY не бустится.
 *
 * Исправления гонок:
 * - во время replace (remove/add) выставляем отметку ДО модификации;
 * - onEffectRemoved игнорирует события, инициированные нами (REENTRY=true).
 */
@EventBusSubscriber
public class CreamyBoostHandler {

    /** защита от рекурсии и внутренних remove/add */
    private static final ThreadLocal<Boolean> REENTRY = ThreadLocal.withInitial(() -> false);

    // ==== Работа с NBT-памятью бустов ====

    private static final String MOD_ROOT = "concoction";
    private static final String BOOSTED_KEY = "creamyBoosted"; // ListTag<String> с id эффектов (e.g. "minecraft:regeneration")

    private static CompoundTag getOrCreateModRoot(LivingEntity e) {
        CompoundTag root = e.getPersistentData();
        CompoundTag mod = root.getCompound(MOD_ROOT);
        if (!root.contains(MOD_ROOT, Tag.TAG_COMPOUND)) {
            root.put(MOD_ROOT, mod);
        }
        return mod;
    }

    private static Set<String> loadBoosted(LivingEntity e) {
        CompoundTag mod = getOrCreateModRoot(e);
        Set<String> out = new HashSet<>();
        if (mod.contains(BOOSTED_KEY, Tag.TAG_LIST)) {
            ListTag list = mod.getList(BOOSTED_KEY, Tag.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                out.add(list.getString(i));
            }
        }
        return out;
    }

    private static void saveBoosted(LivingEntity e, Set<String> set) {
        CompoundTag root = e.getPersistentData();
        CompoundTag mod = getOrCreateModRoot(e);

        ListTag list = new ListTag();
        for (String id : set) {
            list.add(StringTag.valueOf(id));
        }
        mod.put(BOOSTED_KEY, list);
        root.put(MOD_ROOT, mod);
    }

    /** строковый ключ эффекта (minecraft:xxx) либо null */
    private static String keyOf(Holder<MobEffect> holder) {
        return holder.unwrapKey().map(k -> k.location().toString()).orElse(null);
    }

    private static boolean wasBoosted(LivingEntity e, Holder<MobEffect> holder) {
        String id = keyOf(holder);
        if (id == null) return true; // перестрахуемся
        return loadBoosted(e).contains(id);
    }

    private static void markBoosted(LivingEntity e, Holder<MobEffect> holder) {
        String id = keyOf(holder);
        if (id == null) return;
        Set<String> set = loadBoosted(e);
        if (set.add(id)) {
            saveBoosted(e, set);
        }
    }

    private static void unmarkBoosted(LivingEntity e, Holder<MobEffect> holder) {
        String id = keyOf(holder);
        if (id == null) return;
        Set<String> set = loadBoosted(e);
        if (set.remove(id)) {
            saveBoosted(e, set);
        }
    }

    // ==== События ====

    /** Снятие эффектов: чистим отметки ТОЛЬКО снятого эффекта; CREAMY не очищает память.
     *  Если снятие вызвали мы сами (REENTRY=true) — пропускаем, чтобы не потерять отметку посреди replace. */
    @SubscribeEvent
public static void onEffectAdded(MobEffectEvent.Added event) {
    // ✅ Никакой логики на клиенте
    if (event.getEntity() == null || event.getEntity().level().isClientSide()) return;

    if (Boolean.TRUE.equals(REENTRY.get())) return;

    LivingEntity entity = event.getEntity();
    MobEffectInstance added = event.getEffectInstance();
    if (added == null) return;

    Holder<MobEffect> addedHolder = added.getEffect();
    if (addedHolder.is(ConcoctionModMobEffects.CREAMY)) {
        int bonus = added.getAmplifier() + 1;

        // Сам creamy не бустим
        markBoosted(entity, ConcoctionModMobEffects.CREAMY);

        // Бустим только текущие эффекты на сервере
        List<MobEffectInstance> snapshot = new ArrayList<>(entity.getActiveEffects());
        for (MobEffectInstance inst : snapshot) {
            if (inst == added) continue;
            Holder<MobEffect> h = inst.getEffect();
            if (h.is(ConcoctionModMobEffects.CREAMY)) continue;
            if (wasBoosted(entity, h)) continue;

            markBoosted(entity, h);
            boostOnce(entity, inst, bonus);
        }
    }
}

@SubscribeEvent
public static void onEffectRemoved(MobEffectEvent.Remove event) {
    // ✅ Никакой логики на клиенте
    if (event.getEntity() == null || event.getEntity().level().isClientSide()) return;

    if (Boolean.TRUE.equals(REENTRY.get())) return;

    LivingEntity e = event.getEntity();
    Holder<MobEffect> removed = event.getEffect();

    if (removed.is(ConcoctionModMobEffects.CREAMY)) {
        return; // снятие creamy память не трогает
    }

    unmarkBoosted(e, removed);
}


    /** Периодическая проверка: пока CREAMY активен — бустим любые НОВЫЕ эффекты один раз. */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player == null || player.level().isClientSide) return;

        if (!player.hasEffect(ConcoctionModMobEffects.CREAMY)) return;
        MobEffectInstance creamy = player.getEffect(ConcoctionModMobEffects.CREAMY);
        if (creamy == null) return;

        int bonus = creamy.getAmplifier() + 1;

        // Сам CREAMY не должен буститься
        markBoosted(player, ConcoctionModMobEffects.CREAMY);

        // Просматриваем все активные эффекты — если ещё не бустили, бустим
        List<MobEffectInstance> snapshot = new ArrayList<>(player.getActiveEffects());
        for (MobEffectInstance inst : snapshot) {
            Holder<MobEffect> h = inst.getEffect();
            if (h.is(ConcoctionModMobEffects.CREAMY)) continue;
            if (wasBoosted(player, h)) continue;

            // Сначала помечаем, потом повышаем
            markBoosted(player, h);
            boostOnce(player, inst, bonus);
        }
    }

    // ==== Утилита буста ====

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
            entity.removeEffect(h);   // это вызовет onEffectRemoved, но он пропустит из-за REENTRY=true
            entity.addEffect(upgraded); // onEffectAdded тоже пропустит из-за REENTRY=true
        } finally {
            REENTRY.set(false);
        }
    }
}
