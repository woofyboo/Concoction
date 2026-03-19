
package net.mcreator.concoction.block;

import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;

public class TomatoBlock extends Block {
	public TomatoBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).sound(SoundType.GRASS).strength(0.2f, 10f));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}
}
