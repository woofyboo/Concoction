package net.mcreator.concoction.procedures;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

import net.mcreator.concoction.init.ConcoctionModBlocks;

public class SoulIceBlockAddedProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if ((world instanceof Level _lvl ? _lvl.dimension() : (world instanceof WorldGenLevel _wgl ? _wgl.getLevel().dimension() : Level.OVERWORLD)) == Level.NETHER) {
			world.setBlock(BlockPos.containing(x, y, z), ConcoctionModBlocks.WEIGHTED_SOULS.get().defaultBlockState(), 3);
		}
	}
}
