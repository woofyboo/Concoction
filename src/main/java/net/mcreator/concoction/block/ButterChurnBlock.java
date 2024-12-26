
package net.mcreator.concoction.block;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.block.entity.ButterChurnEntity;
import net.mcreator.concoction.block.entity.CookingCauldronEntity;
import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.mcreator.concoction.init.ConcoctionModSounds;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ButterChurnBlock extends Block implements EntityBlock {
	public static final DirectionProperty FACING = DirectionalBlock.FACING;
	public static final BooleanProperty FULL = BooleanProperty.create("full");

	public ButterChurnBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOD).strength(2.5f).randomTicks().pushReaction(PushReaction.IGNORE));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FULL, false));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new ButterChurnEntity(pPos, pState);
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, FULL);
	}

	@Override
	public ItemInteractionResult useItemOn(
			ItemStack pItem, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult p_316140_) {
		if (pLevel.isClientSide) {
			return ItemInteractionResult.SUCCESS;
		} else {
			BlockEntity blockentity = pLevel.getBlockEntity(pPos);

			if (blockentity instanceof ButterChurnEntity butterChurn && pItem.getItem().equals(Items.STICK) &&
					!butterChurn.hasCraftedResult() && butterChurn.hasRecipe()) {
				pLevel.playSound(null, pPos, ConcoctionModSounds.BUTTER_CHURN_SPIN.get(),
						SoundSource.BLOCKS, 1.0F, (float)Math.random());
				if (Math.random() < 0.2) {
					butterChurn.craftItem();
//                            LayeredCauldronBlock.lowerFillLevel(pState, pLevel, pPos);
					pLevel.playSound(null, pPos, ConcoctionModSounds.BUTTER_THICKENS.get(),
							SoundSource.BLOCKS, 1.0F, (float)Math.random());
					butterChurn.craftItem();
					pLevel.setBlockAndUpdate(pPos, pState.setValue(FULL, true));
					butterChurn.setChanged();
					return ItemInteractionResult.SUCCESS;
				}
				return ItemInteractionResult.SUCCESS;
			}

			else if (blockentity instanceof ButterChurnEntity butterChurn && butterChurn.hasCraftedResult()) {
				Map<String, String> result = butterChurn.getCraftResult();
				Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(result.get("id")));
				switch (result.get("interactionType")) {
					case "hand":
						if (pItem.getItem().equals(Items.AIR) || (pItem.getItem().equals(item) && pItem.getCount() < pItem.getMaxStackSize())) {
							if (!pPlayer.addItem(new ItemStack(item)))
								pPlayer.drop(new ItemStack(item), false);
							pLevel.playSound(null, pPos, SoundEvents.ITEM_PICKUP,
									SoundSource.BLOCKS, 1.0F, (float)Math.random());
							result = this.decreesItemCountFromResult(result);
							if (result.get("count").isEmpty()) {
								pLevel.setBlockAndUpdate(pPos, pState.setValue(FULL, false));
								butterChurn.setChanged();
							}
							butterChurn.setCraftResult(result);
						}
						break;

					case "bottle":
						if (pItem.getItem().equals(Items.GLASS_BOTTLE)) {
							if (!pPlayer.addItem(new ItemStack(item)))
								pPlayer.drop(new ItemStack(item), false);
							if (!pPlayer.isCreative()) pItem.shrink(1);
//							LayeredCauldronBlock.lowerFillLevel(pState, pLevel, pPos);
							pLevel.playSound(null, pPos, SoundEvents.BOTTLE_FILL,
									SoundSource.BLOCKS, 1.0F, (float)Math.random());
							result = this.decreesItemCountFromResult(result);
							if (result.get("count").isEmpty()) {
								pLevel.setBlockAndUpdate(pPos, pState.setValue(FULL, false));
								butterChurn.setChanged();
							}
							butterChurn.setCraftResult(result);
						}
						break;

					default:
						ConcoctionMod.LOGGER.warn("Unknown interaction type: {}", butterChurn.getCraftResult().get("interactionType"));
						break;
				}
//                if (cauldron.getCraftResult().get("id").isEmpty()) pLevel.setBlockAndUpdate(pPos, pState.setValue(LIT, false));
				return ItemInteractionResult.CONSUME;
			}

			if (blockentity instanceof ButterChurnEntity butter) {
				if (pItem.getItem().equals(Items.AIR)) {
//                    if (pPlayer.isShiftKeyDown()) pPlayer.addItem(cauldron.takeItemOnClick(true));
					if (pPlayer.addItem(butter.takeItemOnClick(true)))
						pLevel.playSound(null, pPos, SoundEvents.ITEM_PICKUP,
							SoundSource.BLOCKS, 1.0F, (float)Math.random());
				} else {
//                    if (pPlayer.isShiftKeyDown()) cauldron.addItemOnClick(pItem, pItem.getCount(), pPlayer.isCreative());
					if (butter.addItemOnClick(pItem, 1, pPlayer.isCreative()))
						pLevel.playSound(null, pPos, SoundEvents.DECORATED_POT_INSERT,
							SoundSource.BLOCKS, 1.0F, (float)Math.random());
					else pLevel.playSound(null, pPos, ConcoctionModSounds.BARREL_OVERFILLED.get(),
							SoundSource.BLOCKS, 1.0F, (float)Math.random());
				}
			}

			return ItemInteractionResult.CONSUME;
		}
	}

	public Map<String, String>  decreesItemCountFromResult(Map<String, String> result) {
		int new_count = Integer.parseInt(result.get("count"))-1;
		if (new_count <= 0) {
			return Map.ofEntries(
					Map.entry("id",""),
					Map.entry("count",""),
					Map.entry("interactionType","")
			);

		} else {
			return Map.ofEntries(
					Map.entry("id",result.get("id")),
					Map.entry("count",String.valueOf(new_count)),
					Map.entry("interactionType",result.get("interactionType"))
			);
		}
	}

	@Override
	public void onRemove(BlockState p_54085_, Level p_54086_, BlockPos p_54087_, BlockState p_54088_, boolean p_54089_) {
		Containers.dropContentsOnDestroy(p_54085_, p_54088_, p_54086_, p_54087_);
		super.onRemove(p_54085_, p_54086_, p_54087_, p_54088_, p_54089_);
	}

	@Nullable // Due to generics, an unchecked cast is necessary here.
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type) {
		return null;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState p_54055_) {
		return false;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(FULL, false);
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 100;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 60;
	}
}
