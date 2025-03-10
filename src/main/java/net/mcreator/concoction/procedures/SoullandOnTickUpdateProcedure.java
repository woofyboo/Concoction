package net.mcreator.concoction.procedures;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.BlockPos;

import net.mcreator.concoction.init.ConcoctionModBlocks;

public class SoullandOnTickUpdateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, BlockState blockstate) {
		boolean found = false;
		double sx = 0;
		double sz = 0;
		sx = -3;
		found = false;
		for (int index0 = 0; index0 < 7; index0++) {
			sz = -3;
			for (int index1 = 0; index1 < 7; index1++) {
				if ((world.getBlockState(BlockPos.containing(x + sx, y, z + sz))).getBlock() == ConcoctionModBlocks.WEIGHTED_SOULS.get()) {
					found = true;
				}
				sz = sz + 1;
			}
			sx = sx + 1;
		}
		if (!(blockstate.getBlock().getStateDefinition().getProperty("soulcharged") instanceof BooleanProperty _getbp3 && blockstate.getValue(_getbp3))) {
			if (found == true) {
				{
					BlockPos _pos = BlockPos.containing(x, y, z);
					BlockState _bs = world.getBlockState(_pos);
					if (_bs.getBlock().getStateDefinition().getProperty("soulcharged") instanceof BooleanProperty _booleanProp)
						world.setBlock(_pos, _bs.setValue(_booleanProp, true), 3);
				}
			} else if (found == false) {
				world.setBlock(BlockPos.containing(x, y, z), Blocks.SOUL_SOIL.defaultBlockState(), 3);
			}
		} else {
			if (found == false) {
				{
					BlockPos _pos = BlockPos.containing(x, y, z);
					BlockState _bs = world.getBlockState(_pos);
					if (_bs.getBlock().getStateDefinition().getProperty("soulcharged") instanceof BooleanProperty _booleanProp)
						world.setBlock(_pos, _bs.setValue(_booleanProp, false), 3);
				}
			}
		}
	}
}
