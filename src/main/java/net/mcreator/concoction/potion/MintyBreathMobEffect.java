
package net.mcreator.concoction.potion;

import net.mcreator.concoction.init.ConcoctionModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import net.minecraft.world.level.Level;

public class MintyBreathMobEffect extends MobEffect {
	public MintyBreathMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -6684724);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		Level world = entity.level();
		double x = entity.position().x();
		double y = entity.position().y();
		double z = entity.position().z();
		if (Math.random() < 0.04) {
			if (Math.random() < 0.5) {
				world.addParticle((SimpleParticleType) (ConcoctionModParticleTypes.MINT_LEAF_PARTICLE_VARIANT_1.get()), (x + 0.5), (y + Mth.nextDouble(RandomSource.create(), 0.5, 1.5)), (z + 0.5), (Mth.nextDouble(RandomSource.create(), -0.5, 0.5)),
						(Mth.nextDouble(RandomSource.create(), -0.5, 0.5)), (Mth.nextDouble(RandomSource.create(), -0.5, 0.5)));
			} else {
				world.addParticle((SimpleParticleType) (ConcoctionModParticleTypes.MINT_LEAF_PARTICLE_VARIANT_2.get()), (x + 0.5), (y + Mth.nextDouble(RandomSource.create(), 0.5, 1.5)), (z + 0.5), (Mth.nextDouble(RandomSource.create(), -0.5, 0.5)),
						(Mth.nextDouble(RandomSource.create(), -0.5, 0.5)), (Mth.nextDouble(RandomSource.create(), -0.5, 0.5)));
			}
		}
		return super.applyEffectTick(entity, amplifier);
	}
}
