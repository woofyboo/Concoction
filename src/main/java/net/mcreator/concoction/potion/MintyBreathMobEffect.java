
package net.mcreator.concoction.potion;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import net.mcreator.concoction.procedures.MintEffectParticlesProcedure;

public class MintyBreathMobEffect extends MobEffect {
	public MintyBreathMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -6684724);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		MintEffectParticlesProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ());
	}
}
