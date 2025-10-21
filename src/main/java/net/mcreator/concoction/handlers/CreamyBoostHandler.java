package net.mcreator.concoction.handlers;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.Holder;

import java.util.*;

/**
 * CREAMY: разовый буст уровней других эффектов.
 * - При появлении CREAMY — повышает уровень всех уже висящих эффектов (кроме самого CREAMY) на (amp+1).
 * - Пока CREAMY активен, любой НОВЫЙ эффект сразу повышается ровно один раз.
 * - Один и тот же тип эффекта бустится только один раз (сбрасывается при снятии этого эффекта).
 * - Сам CREAMY никогда не бустится.
 */
@EventBusSubscriber
public class CreamyBoostHandler {

    // защита от рекурсии, когда мы сами снимаем/добавляем эффекты внутри обработчика
    private static final ThreadLocal<Boolean> REENTRY = ThreadLocal.withInitial(() -> false);

    // Для каждого игрока/моба: какие типы эффектов уже были "усилены" при текущем наличии CREAMY
    // Ключ — UUID сущности; значение — множество строк-ключей эффектов (minecraft:regeneration и т.п.)
    private static final Map<UUID, Set<String>> BOOSTED = new HashMap<>();

    private static Set<String> getBoostedSet(LivingEntity e) {
        return BOOSTED.computeIfAbsent(e.getUUID(), u -> new HashSet<>());
    }
    private static void setBoostedSet(LivingEntity e, Set<String> s) {
        BOOSTED.put(e.getUUID(), s);
    }

    /** вернуть строковый ключ эффекта (minecraft:xxx), либо null, если не получилось */
    private static String keyOf(Holder<MobEffect> holder) {
        return holder.unwrapKey().map(k -> k.location().toString()).orElse(null);
    }

    private static boolean wasBoosted(LivingEntity e, Holder<MobEffect> holder) {
        String id = keyOf(holder);
        if (id == null) return true; // на всякий случай — считаем "уже бустили", чтобы не рисковать
        return getBoostedSet(e).contains(id);
    }

    private static void markBoosted(LivingEntity e, Holder<MobEffect> holder) {
        String id = keyOf(holder);
        if (id == null) return;
        Set<String> set = getBoostedSet(e);
        set.add(id);
        setBoostedSet(e, set);
    }

    private static void unmarkBoosted(LivingEntity e, Holder<MobEffect> holder) {
        String id = keyOf(holder);
        if (id == null) return;
        Set<String> set = getBoostedSet(e);
        if (set.remove(id)) setBoostedSet(e, set);
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        // как только КОНКРЕТНЫЙ тип эффекта снят — позволяем в будущем его снова бустить
        LivingEntity e = event.getEntity();
        if (e == null) return;
        unmarkBoosted(e, event.getEffect());
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        if (Boolean.TRUE.equals(REENTRY.get())) return;

        LivingEntity entity = event.getEntity();
        MobEffectInstance added = event.getEffectInstance();
        if (entity == null || added == null) return;

        Holder<MobEffect> addedHolder = added.getEffect();

        // === 1) Обработка, когда добавили CREAMY ===
        if (addedHolder.is(ConcoctionModMobEffects.CREAMY)) {
            // НИКОГДА не бустим сам CREAMY
            // Повышаем уровни ВСЕХ текущих эффектов (кроме CREAMY), которые ещё не бустили
            int bonus = added.getAmplifier() + 1;

            // копия активных, т.к. будем снимать/добавлять
            List<MobEffectInstance> snapshot = new ArrayList<>(entity.getActiveEffects());

            for (MobEffectInstance inst : snapshot) {
                if (inst == added) continue;                 // не трогаем только что добавленный CREAMY
                Holder<MobEffect> h = inst.getEffect();
                if (h.is(ConcoctionModMobEffects.CREAMY)) continue;  // и вообще никогда не трогаем CREAMY
                if (wasBoosted(entity, h)) continue;         // уже бустили этот тип — пропускаем

                // создаём усиленную копию
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

                markBoosted(entity, h);
            }
            return;
        }

        // === 2) Добавили ЛЮБОЙ другой эффект, пока CREAMY активен ===
        if (entity.hasEffect(ConcoctionModMobEffects.CREAMY)) {
            // Узнаём бонус от текущего CREAMY
            MobEffectInstance creamy = entity.getEffect(ConcoctionModMobEffects.CREAMY);
            if (creamy == null) return;

            // на всякий: не трогаем эффекты, если почему-то добавили CREAMY (хотя выше мы уже вышли)
            if (addedHolder.is(ConcoctionModMobEffects.CREAMY)) return;

            // Бустим только если этот тип ещё не бустили ранее (в текущем "сеансе")
            if (!wasBoosted(entity, addedHolder)) {
                int bonus = creamy.getAmplifier() + 1;

                MobEffectInstance boosted = new MobEffectInstance(
                        addedHolder,
                        added.getDuration(),
                        added.getAmplifier() + bonus,
                        added.isAmbient(),
                        added.isVisible(),
                        added.showIcon()
                );

                REENTRY.set(true);
                try {
                    entity.removeEffect(addedHolder);
                    entity.addEffect(boosted);
                } finally {
                    REENTRY.set(false);
                }

                markBoosted(entity, addedHolder);
            }
        }
    }
}
