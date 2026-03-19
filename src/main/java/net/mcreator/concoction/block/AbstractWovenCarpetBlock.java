package net.mcreator.concoction.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public abstract class AbstractWovenCarpetBlock extends HorizontalDirectionalBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;

	protected AbstractWovenCarpetBlock() {
		this(createProperties());
	}

	protected AbstractWovenCarpetBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, BedPart.FOOT));
	}

	protected static BlockBehaviour.Properties createProperties() {
		return BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).sound(SoundType.WOOL).strength(0.1f).noOcclusion().pushReaction(PushReaction.DESTROY)
				.isRedstoneConductor((bs, br, bp) -> false);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return level.getBlockState(pos.below()).isSolid();
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
		return box(0, 0, 0, 16, 1, 16);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, PART);
	}

	private static Direction getNeighbourDirection(BedPart part, Direction facing) {
		return part == BedPart.FOOT ? facing : facing.getOpposite();
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (!this.canSurvive(state, level, pos)) {
			return Blocks.AIR.defaultBlockState();
		}
		if (direction == getNeighbourDirection(state.getValue(PART), state.getValue(FACING))) {
			return neighborState.is(this) && neighborState.getValue(PART) != state.getValue(PART)
					? state
					: Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		if (!level.isClientSide && player.isCreative()) {
			BedPart part = state.getValue(PART);
			if (part == BedPart.FOOT) {
				BlockPos otherPartPos = pos.relative(getNeighbourDirection(part, state.getValue(FACING)));
				BlockState otherPartState = level.getBlockState(otherPartPos);
				if (otherPartState.is(this) && otherPartState.getValue(PART) == BedPart.HEAD) {
					level.setBlock(otherPartPos, Blocks.AIR.defaultBlockState(), 35);
					level.levelEvent(player, 2001, otherPartPos, Block.getId(otherPartState));
				}
			}
		}

		return super.playerWillDestroy(level, pos, state, player);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction direction = context.getHorizontalDirection();
		BlockPos pos = context.getClickedPos();
		BlockPos otherPartPos = pos.relative(direction);
		Level level = context.getLevel();
		return level.getBlockState(otherPartPos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(otherPartPos) && this.canSurvive(level.getBlockState(otherPartPos), level, otherPartPos)
				? this.defaultBlockState().setValue(FACING, direction)
				: null;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (!level.isClientSide) {
			BlockPos otherPartPos = pos.relative(state.getValue(FACING));
			level.setBlock(otherPartPos, state.setValue(PART, BedPart.HEAD), 3);
			level.blockUpdated(pos, Blocks.AIR);
			state.updateNeighbourShapes(level, pos, 3);
		}
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
