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

public class NetherPepperCropOnTickUpdateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		world.addParticle(ParticleTypes.SOUL, (x + Mth.nextDouble(RandomSource.create(), 0.2, 0.8)), (y + Mth.nextDouble(RandomSource.create(), 0.2, 0.8)), (z + Mth.nextDouble(RandomSource.create(), 0.2, 0.8)),
				(Mth.nextDouble(RandomSource.create(), -0.13, 0.13)), (Mth.nextDouble(RandomSource.create(), 0.13, 0.2)), (Mth.nextDouble(RandomSource.create(), -0.13, 0.13)));
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("particle.soul_escape")), SoundSource.BLOCKS, 1, (float) Math.random());
			} else {
				_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("particle.soul_escape")), SoundSource.BLOCKS, 1, (float) Math.random(), false);
			}
		}
	}
}
