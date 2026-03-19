package net.mcreator.concoction.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class CarrotCakeBlock extends ConcoctionCakeBlock {
	public CarrotCakeBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.WOOL).strength(0.5f).noOcclusion().pushReaction(PushReaction.DESTROY)
				.isRedstoneConductor((bs, br, bp) -> false), 4, 1.2F);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockState belowState = level.getBlockState(pos.below());
		return belowState.isSolid() || belowState.getBlock() instanceof CarrotCakeBlock || belowState.getBlock() instanceof CakeBlock;
	}
}
