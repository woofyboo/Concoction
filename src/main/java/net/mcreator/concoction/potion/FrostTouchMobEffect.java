
package net.mcreator.concoction.potion;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import net.mcreator.concoction.procedures.FrostTouchActiveTickConditionProcedure;

public class FrostTouchMobEffect extends MobEffect {
	public FrostTouchMobEffect() {
		super(MobEffectCategory.NEUTRAL, -6710785);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		FrostTouchActiveTickConditionProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
		return super.applyEffectTick(entity, amplifier);
	}
}
