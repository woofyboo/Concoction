
package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import com.mojang.serialization.MapCodec;

import static net.mcreator.concoction.utils.Utils.*;

public class RiceBlockBlock extends FallingBlock {
	public static final MapCodec<RiceBlockBlock> CODEC = simpleCodec(properties -> new RiceBlockBlock());
	public MapCodec<RiceBlockBlock> codec() {
		return CODEC;
	}

	public RiceBlockBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).sound(SoundType.SAND).strength(0.5f));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 5;
	}

	@Override
	public void onLand(Level p_52068_, BlockPos p_52069_, BlockState p_52070_, BlockState p_52071_, FallingBlockEntity p_52072_) {
		if (shouldSolidify(p_52068_, p_52069_, p_52070_, p_52071_.getFluidState()) || touchesLiquid(p_52068_, p_52069_, p_52070_)) {
			p_52068_.setBlock(p_52069_, ConcoctionModBlocks.SOAKED_RICE_BLOCK.get().defaultBlockState(), 128);
		}

	}

	@Override
	protected BlockState updateShape(BlockState p_52074_, Direction p_52075_, BlockState p_52076_, LevelAccessor p_52077_, BlockPos p_52078_, BlockPos p_52079_) {
		return touchesLiquid(p_52077_, p_52078_, p_52074_) ? ConcoctionModBlocks.SOAKED_RICE_BLOCK.get().defaultBlockState() : super.updateShape(p_52074_, p_52075_, p_52076_, p_52077_, p_52078_, p_52079_);
	}


	@Override
	protected void onPlace(BlockState p_56811_, Level p_56812_, BlockPos p_56813_, BlockState p_56814_, boolean p_56815_) {
		if (!p_56814_.is(p_56811_.getBlock())) {
			tryAbsorbWater(p_56812_, p_56813_, this);
		}
		super.onPlace(p_56811_, p_56812_, p_56813_, p_56814_, p_56815_);
	}

	@Override
	protected void neighborChanged(BlockState p_56801_, Level p_56802_, BlockPos p_56803_, Block p_56804_, BlockPos p_56805_, boolean p_56806_) {
		tryAbsorbWater(p_56802_, p_56803_, this);
		super.neighborChanged(p_56801_, p_56802_, p_56803_, p_56804_, p_56805_, p_56806_);
	}
	
}
