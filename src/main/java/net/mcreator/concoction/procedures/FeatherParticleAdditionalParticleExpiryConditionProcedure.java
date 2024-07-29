package net.mcreator.concoction.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

public class FeatherParticleAdditionalParticleExpiryConditionProcedure {
	public static boolean execute(LevelAccessor world, double x, double y, double z) {
		boolean condition = false;
		condition = false;
		if (world.getBlockState(BlockPos.containing(x, y - 0.05, z)).canOcclude()) {
			condition = true;
		}
		return condition;
	}
}
