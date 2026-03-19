package net.mcreator.concoction.block;

import net.mcreator.concoction.block.entity.AbstractKitchenCabinetBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public abstract class AbstractKitchenCabinetBlock extends Block implements EntityBlock {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

	protected AbstractKitchenCabinetBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, OPEN);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(OPEN, false);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 5;
	}

	@Override
	public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player entity, BlockHitResult hit) {
		super.useWithoutItem(state, world, pos, entity, hit);
		if (entity instanceof ServerPlayer player) {
			MenuProvider menuProvider = this.getMenuProvider(state, world, pos);
			if (menuProvider != null) {
				player.openMenu(menuProvider, pos);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity instanceof MenuProvider menuProvider ? menuProvider : null;
	}

	@Override
	public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
		super.triggerEvent(state, world, pos, eventID, eventParam);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof AbstractKitchenCabinetBlockEntity cabinetBlockEntity) {
				Containers.dropContents(world, pos, cabinetBlockEntity);
				world.updateNeighbourForOutputSignal(pos, this);
			}
			super.onRemove(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (state.getValue(OPEN)) {
			level.setBlock(pos, state.setValue(OPEN, false), 3);
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity instanceof AbstractKitchenCabinetBlockEntity cabinetBlockEntity
				? AbstractContainerMenu.getRedstoneSignalFromContainer(cabinetBlockEntity)
				: 0;
	}

	public SoundEvent getOpenSound() {
		return SoundEvents.WOODEN_TRAPDOOR_OPEN;
	}

	public SoundEvent getCloseSound() {
		return SoundEvents.WOODEN_TRAPDOOR_CLOSE;
	}
}
