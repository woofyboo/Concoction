package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class ConcoctionCakeBlock extends Block {
	protected static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 6);

	private final int nutrition;
	private final float saturation;

	protected ConcoctionCakeBlock(BlockBehaviour.Properties properties, int nutrition, float saturation) {
		super(properties);
		this.nutrition = nutrition;
		this.saturation = saturation;
		this.registerDefaultState(this.stateDefinition.any().setValue(BITES, 0));
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
		return switch (state.getValue(BITES)) {
			default -> box(1, 0, 1, 15, 8, 15);
			case 0 -> box(1, 0, 1, 15, 8, 15);
			case 1 -> box(3, 0, 1, 15, 8, 15);
			case 2 -> box(5, 0, 1, 15, 8, 15);
			case 3 -> box(7, 0, 1, 15, 8, 15);
			case 4 -> box(9, 0, 1, 15, 8, 15);
			case 5 -> box(11, 0, 1, 15, 8, 15);
			case 6 -> box(13, 0, 1, 15, 8, 15);
		};
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (direction == Direction.DOWN && neighborState.isAir()) {
			return Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BITES);
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (!canEat(level, player)) {
			return super.useWithoutItem(state, level, pos, player, hit);
		}
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}

		eatSlice(level, pos, state, player, InteractionHand.MAIN_HAND);
		return InteractionResult.SUCCESS;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!canEat(level, player)) {
			return super.useItemOn(itemStack, state, level, pos, player, hand, hit);
		}
		if (level.isClientSide) {
			return ItemInteractionResult.SUCCESS;
		}

		eatSlice(level, pos, state, player, hand);
		return ItemInteractionResult.SUCCESS;
	}

	private boolean canEat(Level level, Player player) {
		if (player.isShiftKeyDown()) {
			return false;
		}

		boolean canAlwaysEat = level.getGameRules().getBoolean(ConcoctionModGameRules.CAN_ALWAYS_EAT);
		return player.getFoodData().getFoodLevel() < 20 || canAlwaysEat || player.getAbilities().instabuild;
	}

	private void eatSlice(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand) {
		int bites = state.getValue(BITES);
		if (bites < 6) {
			level.setBlock(pos, state.setValue(BITES, bites + 1), 3);
		} else {
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		}

		player.swing(hand, true);
		player.getFoodData().eat(nutrition, saturation);
		onEaten(level, pos, player);
	}

	protected void onEaten(Level level, BlockPos pos, Player player) {
	}
}
