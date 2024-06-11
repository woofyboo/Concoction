package net.mcreator.concoction.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.core.particles.SimpleParticleType;

import net.mcreator.concoction.init.ConcoctionModParticleTypes;

public class MintEffectParticlesProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (Math.random() < 0.04) {
			if (Math.random() < 0.5) {
				world.addParticle((SimpleParticleType) (ConcoctionModParticleTypes.MINT_LEAF_PARTICLE_VARIANT_1.get()), (x + 0.5), (y + Mth.nextDouble(RandomSource.create(), 0.5, 1.5)), (z + 0.5), (Mth.nextDouble(RandomSource.create(), -0.5, 0.5)),
						(Mth.nextDouble(RandomSource.create(), -0.5, 0.5)), (Mth.nextDouble(RandomSource.create(), -0.5, 0.5)));
			} else {
				world.addParticle((SimpleParticleType) (ConcoctionModParticleTypes.MINT_LEAF_PARTICLE_VARIANT_2.get()), (x + 0.5), (y + Mth.nextDouble(RandomSource.create(), 0.5, 1.5)), (z + 0.5), (Mth.nextDouble(RandomSource.create(), -0.5, 0.5)),
						(Mth.nextDouble(RandomSource.create(), -0.5, 0.5)), (Mth.nextDouble(RandomSource.create(), -0.5, 0.5)));
			}
		}
	}
}
