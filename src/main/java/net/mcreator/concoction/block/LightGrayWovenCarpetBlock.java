
package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.Mob;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

public class LightGrayWovenCarpetBlock extends HorizontalDirectionalBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;

	public LightGrayWovenCarpetBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.WOOL).strength(0.1f).noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, BedPart.FOOT));
	}

	@Override
	protected boolean canSurvive(BlockState p_51209_, LevelReader p_51210_, BlockPos p_51211_) {
		return p_51210_.getBlockState(p_51211_.below()).isSolid();
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return true;
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 0;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			default -> box(0, 0, 0, 16, 1, 16);
			case NORTH -> box(0, 0, 0, 16, 1, 16);
			case EAST -> box(0, 0, 0, 16, 1, 16);
			case WEST -> box(0, 0, 0, 16, 1, 16);
		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, PART);
	}

	private static Direction getNeighbourDirection(BedPart p_49534_, Direction p_49535_) {
		return p_49534_ == BedPart.FOOT ? p_49535_ : p_49535_.getOpposite();
	}
	@Override
	protected BlockState updateShape(BlockState p_49525_, Direction p_49526_, BlockState p_49527_, LevelAccessor p_49528_, BlockPos p_49529_, BlockPos p_49530_) {
		if (!this.canSurvive(p_49525_, p_49528_, p_49529_)) return Blocks.AIR.defaultBlockState();
		if (p_49526_ == getNeighbourDirection(p_49525_.getValue(PART), p_49525_.getValue(FACING))) {
			return p_49527_.is(this) && p_49527_.getValue(PART) != p_49525_.getValue(PART)
					? p_49525_
					: Blocks.AIR.defaultBlockState();
		} else {
			return super.updateShape(p_49525_, p_49526_, p_49527_, p_49528_, p_49529_, p_49530_);
		}
	}
	@Override
	public BlockState playerWillDestroy(Level p_49505_, BlockPos p_49506_, BlockState p_49507_, Player p_49508_) {
		if (!p_49505_.isClientSide && p_49508_.isCreative()) {
			BedPart bedpart = p_49507_.getValue(PART);
			if (bedpart == BedPart.FOOT) {
				BlockPos blockpos = p_49506_.relative(getNeighbourDirection(bedpart, p_49507_.getValue(FACING)));
				BlockState blockstate = p_49505_.getBlockState(blockpos);
				if (blockstate.is(this) && blockstate.getValue(PART) == BedPart.HEAD) {
					p_49505_.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
					p_49505_.levelEvent(p_49508_, 2001, blockpos, Block.getId(blockstate));
				}
			}
		}

		return super.playerWillDestroy(p_49505_, p_49506_, p_49507_, p_49508_);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext p_49479_) {
		Direction direction = p_49479_.getHorizontalDirection();
		BlockPos blockpos = p_49479_.getClickedPos();
		BlockPos blockpos1 = blockpos.relative(direction);
		Level level = p_49479_.getLevel();
		return level.getBlockState(blockpos1).canBeReplaced(p_49479_) && level.getWorldBorder().isWithinBounds(blockpos1) && this.canSurvive(level.getBlockState(blockpos1), level, blockpos1)
				? this.defaultBlockState().setValue(FACING, direction)
				: null;
	}

	@Override
	public void setPlacedBy(Level p_49499_, BlockPos p_49500_, BlockState p_49501_, @Nullable LivingEntity p_49502_, ItemStack p_49503_) {
		super.setPlacedBy(p_49499_, p_49500_, p_49501_, p_49502_, p_49503_);
		if (!p_49499_.isClientSide) {
			BlockPos blockpos = p_49500_.relative(p_49501_.getValue(FACING));
			p_49499_.setBlock(blockpos, p_49501_.setValue(PART, BedPart.HEAD), 3);
			p_49499_.blockUpdated(p_49500_, Blocks.AIR);
			p_49501_.updateNeighbourShapes(p_49499_, p_49500_, 3);
		}
	}

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return null;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
		return PathType.OPEN;
	}
}
