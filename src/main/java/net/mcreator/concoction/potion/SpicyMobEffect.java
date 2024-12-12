
package net.mcreator.concoction.potion;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import net.mcreator.concoction.procedures.SpicyOnEffectActiveTickProcedure;

public class SpicyMobEffect extends MobEffect {
	public SpicyMobEffect() {
		super(MobEffectCategory.NEUTRAL, -46336);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		SpicyOnEffectActiveTickProcedure.execute(entity.level(), entity);
		return super.applyEffectTick(entity, amplifier);
	}
}
