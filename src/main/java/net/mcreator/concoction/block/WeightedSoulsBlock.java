package net.mcreator.concoction.block;

import org.checkerframework.checker.units.qual.s;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.pathfinder.PathComputationType;

import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;


import net.mcreator.concoction.procedures.WeightedSoulsOnRandomClientDisplayTickProcedure;
import net.mcreator.concoction.procedures.WeightedSoulsMobplayerCollidesBlockProcedure;
import net.mcreator.concoction.procedures.WeightedSoulsBlockAddedProcedure;
import net.mcreator.concoction.init.ConcoctionModFluids;

public class WeightedSoulsBlock extends LiquidBlock {
	public WeightedSoulsBlock() {
		super(ConcoctionModFluids.WEIGHTED_SOULS.get(), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(100f).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).lightLevel(s -> 6).noCollission()
				.noLootTable().liquid().pushReaction(PushReaction.DESTROY).sound(SoundType.EMPTY).replaceable());
	}

	@Override
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);
		WeightedSoulsBlockAddedProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
	}
	
	@Override
	public boolean isPathfindable(BlockState p_53267_, PathComputationType p_53270_) {
		return false;
	}


	@Override
	public void entityInside(BlockState blockstate, Level world, BlockPos pos, Entity entity) {
		super.entityInside(blockstate, world, pos, entity);
		WeightedSoulsMobplayerCollidesBlockProcedure.execute(world, entity);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState blockstate, Level world, BlockPos pos, RandomSource random) {
		super.animateTick(blockstate, world, pos, random);
		WeightedSoulsOnRandomClientDisplayTickProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
	}

	@Override
public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
    super.neighborChanged(blockstate, world, pos, neighborBlock, neighborPos, isMoving);

    // Check if lava is above the block
    BlockPos blockAbove = pos.above();
    if (world.getBlockState(blockAbove).getBlock() == Blocks.LAVA) {
        // Replace the current block with blackstone
        world.setBlock(pos, Blocks.BLACKSTONE.defaultBlockState(), 3);
        
        // Play sound at the block position
        world.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.7F, 1.0F);
    }
}

}
