package net.mcreator.concoction.potion;

import net.mcreator.concoction.handlers.WeepingStateHandler;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class WeepingMobEffect extends MobEffect {

    public WeepingMobEffect() {
        // ярко-красный цвет и нейтральная категория
        super(MobEffectCategory.NEUTRAL, 0xFF0000);
    }

    /**
     * Максимальное хп под эффектом WEEPING.
     * База: 8 HP (4 сердца), каждый уровень минус 2 HP.
     * Минимум — 2 HP (1 сердце).
     */
    public static float getWeepingHpCap(LivingEntity entity) {
        MobEffectInstance inst = entity.getEffect(ConcoctionModMobEffects.WEEPING);
        if (inst == null) {
            return entity.getMaxHealth();
        }

        int amp = inst.getAmplifier(); // 0 = I, 1 = II, ...
        float cap = 8.0F - amp * 2.0F; // 8, 6, 4, 2...
        cap = Mth.clamp(cap, 2.0F, entity.getMaxHealth());
        return cap;
    }

    private static void clampHealthToCap(LivingEntity entity) {
        float cap = getWeepingHpCap(entity);
        if (entity.getHealth() > cap) {
            entity.setHealth(cap);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // пусть тикает каждый тик, чтобы сразу подрезать хп до капа
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            WeepingStateHandler.ensureWeepingState(entity);
            clampHealthToCap(entity);
        }
        // true = эффект что-то сделал, можно, например, обновлять рендер эффектов
        return true;
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            WeepingStateHandler.ensureWeepingState(entity);
            clampHealthToCap(entity);
        }
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            WeepingStateHandler.ensureWeepingState(entity);
            clampHealthToCap(entity);
        }
    }
}
