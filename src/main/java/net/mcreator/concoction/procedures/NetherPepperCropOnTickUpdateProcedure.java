package net.mcreator.concoction.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;

public class NetherPepperCropOnTickUpdateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.SOUL, (x + 0.5), (y + 0.8), (z + 0.5), 16, 1, 0.2, 1, 0.8);
	}
}
