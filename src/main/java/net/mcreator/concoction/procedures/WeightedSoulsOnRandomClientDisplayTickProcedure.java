package net.mcreator.concoction.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

public class WeightedSoulsOnRandomClientDisplayTickProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world.isEmptyBlock(BlockPos.containing(x, y + 1, z))) {
			if (Math.random() < 0.015) {
				world.addParticle(ParticleTypes.SOUL, (x + 0.5), (y + 0.9), (z + 0.5), 0, (Mth.nextDouble(RandomSource.create(), 0.3, 0.45)), 0);
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("particle.soul_escape")), SoundSource.BLOCKS, 1, (float) Math.random());
					} else {
						_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("particle.soul_escape")), SoundSource.BLOCKS, 1, (float) Math.random(), false);
					}
				}
				if (Math.random() < 0.5) {
					world.addParticle(ParticleTypes.SOUL, (x + 0.5), (y + 0.9), (z + 0.5), (Mth.nextDouble(RandomSource.create(), -0.1, 0.1)), (Mth.nextDouble(RandomSource.create(), 0.3, 0.45)), (Mth.nextDouble(RandomSource.create(), -0.1, 0.1)));
				}
				if (Math.random() < 0.5) {
					world.addParticle(ParticleTypes.SOUL, (x + 0.5), (y + 0.9), (z + 0.5), (Mth.nextDouble(RandomSource.create(), -0.1, 0.1)), (Mth.nextDouble(RandomSource.create(), 0.3, 0.45)), (Mth.nextDouble(RandomSource.create(), -0.1, 0.1)));
				}
				if (Math.random() < 0.8) {
					world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, (x + 0.5), (y + 0.9), (z + 0.5), (Mth.nextDouble(RandomSource.create(), -0.1, 0.1)), (Mth.nextDouble(RandomSource.create(), 0.3, 0.45)),
							(Mth.nextDouble(RandomSource.create(), -0.1, 0.1)));
				}
			}
		}
	}
}
