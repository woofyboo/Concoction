package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Objects;

import static net.mcreator.concoction.init.ConcoctionModBlocks.CROP_REAPPER;

public class CropSpicyPepperBlock extends CropBlock {
	public static final int MAX_AGE = 5;
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

	public CropSpicyPepperBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS).instabreak().noCollission().noOcclusion().randomTicks().pushReaction(PushReaction.DESTROY)
				.isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
	}

	@Override
	public boolean mayPlaceOn(BlockState state, BlockGetter world, BlockPos pos) {
		return state.getBlock() instanceof FarmBlock || state.getBlock() instanceof SoullandBlock;
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		if (!level.isAreaLoaded(pos, 1)) {
			return;
		}

		if (level.getBlockState(pos.below()).getBlock() instanceof SoullandBlock) {
			int currentAge = this.getAge(state);
			Block netherBlock = CROP_REAPPER.get();

			if (netherBlock instanceof CropBlock netherCrop) {
				int maxNetherAge = netherCrop.getMaxAge();
				int clampedAge = Mth.clamp(currentAge, 0, maxNetherAge);
				level.setBlock(pos, netherCrop.getStateForAge(clampedAge), 2);
			} else {
				level.setBlock(pos, netherBlock.defaultBlockState(), 2);
			}

			Utils.playSoulMutationEffect(level, pos);
			return;
		}

		super.randomTick(state, level, pos, random);
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!player.isShiftKeyDown() && state.getBlock() == ConcoctionModBlocks.CROP_SPICY_PEPPER.get() && state.getValue(AGE) == MAX_AGE) {
			player.swing(InteractionHand.MAIN_HAND, true);
			if (!level.isClientSide()) {
				level.playSound(null, pos, Objects.requireNonNull(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.sweet_berry_bush.pick_berries"))), SoundSource.BLOCKS, 1.0F, 1.0F);
			} else {
				level.playLocalSound(pos, Objects.requireNonNull(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("block.sweet_berry_bush.pick_berries"))), SoundSource.BLOCKS, 1.0F, 1.0F, false);
			}

			if (level instanceof ServerLevel serverLevel) {
				level.setBlock(pos, state.setValue(AGE, 2), 3);
				ItemEntity drop = new ItemEntity(serverLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ConcoctionModItems.SPICY_PEPPER.get(), 1));
				drop.setPickUpDelay(10);
				serverLevel.addFreshEntity(drop);

				if (Math.random() < 0.3) {
					ItemEntity extra = new ItemEntity(serverLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(ConcoctionModItems.SPICY_PEPPER.get()));
					extra.setPickUpDelay(10);
					serverLevel.addFreshEntity(extra);
				}

				return ItemInteractionResult.SUCCESS;
			}
		}

		return super.useItemOn(itemStack, state, level, pos, player, hand, hit);
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
		return switch (state.getValue(AGE)) {
			default -> Block.box(1, 0, 1, 15, 15, 15);
			case 0 -> Block.box(4, 0, 4, 12, 8, 12);
			case 1 -> Block.box(2, 0, 2, 14, 12, 14);
			case 2 -> Block.box(1, 0, 1, 15, 15, 15);
			case 3 -> Block.box(1, 0, 1, 15, 15, 15);
			case 4 -> Block.box(1, 0, 1, 15, 15, 15);
			case 5 -> Block.box(1, 0, 1, 15, 15, 15);
		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 100;
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		return new ItemStack(ConcoctionModItems.SPICY_PEPPER_SEEDS.get());
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 25;
	}

	@Override
	public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
		return PathType.OPEN;
	}

	@Override
	public int getMaxAge() {
		return MAX_AGE;
	}

	@Override
	protected ItemLike getBaseSeedId() {
		return ConcoctionModItems.SPICY_PEPPER_SEEDS.get();
	}

	@Override
	public IntegerProperty getAgeProperty() {
		return AGE;
	}
}
