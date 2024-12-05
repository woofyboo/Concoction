
package net.mcreator.concoction.block;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Mob;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.concoction.init.ConcoctionModItems;

import javax.annotation.Nullable;

public class CropCornBlock extends CropBlock {
	public static final int FIRST_STAGE_MAX_AGE = 1;
	public static final int SECOND_STAGE_MAX_AGE = 1;
	public static final int THIRD_STAGE_MAX_AGE = 3;
	public static final int MAX_AGE = FIRST_STAGE_MAX_AGE + SECOND_STAGE_MAX_AGE + THIRD_STAGE_MAX_AGE;
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);
	public static final EnumProperty<PartProperty> PART = EnumProperty.create("part", PartProperty.class);

	public CropCornBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.NONE).sound(SoundType.GRASS).instabreak().noCollission().noOcclusion().randomTicks().pushReaction(PushReaction.DESTROY).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), 0).setValue(PART, PartProperty.BOTTOM));
	}

	@Override
	public boolean isRandomlyTicking(BlockState p_52288_) {
		return !this.isMaxAge(p_52288_);
	}

	public void changeAge(BlockState pState, ServerLevel pLevel, BlockPos pPos, int nextAge) {
		if (nextAge <= FIRST_STAGE_MAX_AGE)
			pLevel.setBlock(pPos, this.getState(pState, nextAge, PartProperty.BOTTOM), 2);

		else if (nextAge == FIRST_STAGE_MAX_AGE+SECOND_STAGE_MAX_AGE && pLevel.getBlockState(pPos.above()).is(Blocks.AIR)) {
			pLevel.setBlock(pPos, this.getState(pState, nextAge, PartProperty.BOTTOM), 2);
			pLevel.setBlock(pPos.above(), this.getState(pState, nextAge, PartProperty.MIDDLE), 2);
		}

		else if (nextAge > FIRST_STAGE_MAX_AGE+SECOND_STAGE_MAX_AGE && (pLevel.getBlockState(pPos.above()).is(Blocks.AIR) || pLevel.getBlockState(pPos.above()).is(this))
				&& (pLevel.getBlockState(pPos.above(2)).is(Blocks.AIR) || pLevel.getBlockState(pPos.above(2)).is(this))) {
			pLevel.setBlock(pPos, this.getState(pState, nextAge, PartProperty.BOTTOM), 2);
			pLevel.setBlock(pPos.above(1), this.getState(pState, nextAge, PartProperty.MIDDLE), 2);
			pLevel.setBlock(pPos.above(2), this.getState(pState, nextAge, PartProperty.TOP), 2);
		}
	}

	@Override
	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource random) {
		if (!pLevel.isAreaLoaded(pPos, 1))
			return; // Forge: prevent loading unloaded chunks when checking neighbor's light

		if (pLevel.getRawBrightness(pPos, 0) >= 9 && (pState.getValue(PART) == PartProperty.BOTTOM)) {
			int nextAge = this.getAge(pState) + 1;
			if (nextAge <= this.getMaxAge()) {
				float f = getGrowthSpeed(pState, pLevel, pPos);
				if (net.neoforged.neoforge.common.CommonHooks.canCropGrow(pLevel, pPos, pState, random.nextInt((int) (25.0F / f) + 1) == 0)) {
					changeAge(pState, pLevel, pPos, nextAge);
					net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost(pLevel, pPos, pState);
				}
			}
		}
	}

	public BlockState getState(BlockState pState, int age, PartProperty part) {
		return pState.setValue(this.getAgeProperty(), Integer.valueOf(age)).setValue(PART, part);
	}

	@Override
	public void growCrops(Level pLevel, BlockPos pPos, BlockState pState) {
		int nextAge = this.getAge(pState) + this.getBonemealAgeIncrease(pLevel);
		int maxAge = this.getMaxAge();
		if (pState.getValue(PART) == PartProperty.BOTTOM) {
//        	nextAge += pLevel.getBlockState(pPos.above(1)).is(this) ? this.getAge(pLevel.getBlockState(pPos.above(1))) : 0;
			if (nextAge > maxAge) nextAge = maxAge;
			changeAge(pState, (ServerLevel) pLevel, pPos, nextAge);
		}
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState) {
		if (pState.getValue(PART) == PartProperty.BOTTOM)
			return pState.getValue(AGE) != this.getMaxAge();
		else
			return false;
	}

	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		if (pState.getValue(PART) == PartProperty.BOTTOM) {
			if (pState.getValue(AGE) <= FIRST_STAGE_MAX_AGE)
				return super.canSurvive(pState, pLevel, pPos);
			else if (pState.getValue(AGE) == SECOND_STAGE_MAX_AGE+FIRST_STAGE_MAX_AGE)
				return super.canSurvive(pState, pLevel, pPos) && isValidCorn(pLevel, pPos.above(1), pState.getValue(AGE), PartProperty.MIDDLE);
			else if (pState.getValue(AGE) > SECOND_STAGE_MAX_AGE+FIRST_STAGE_MAX_AGE)
				return super.canSurvive(pState, pLevel, pPos) && isValidCorn(pLevel, pPos.above(1), pState.getValue(AGE), PartProperty.MIDDLE);
			else return false;
		}
		else if (pState.getValue(PART) == PartProperty.MIDDLE) {
			if (pState.getValue(AGE) <= FIRST_STAGE_MAX_AGE)
				return false;
			else if (pState.getValue(AGE) == SECOND_STAGE_MAX_AGE+FIRST_STAGE_MAX_AGE)
				return super.canSurvive(pState, pLevel, pPos) || isValidCorn(pLevel, pPos.below(1), pState.getValue(AGE), PartProperty.BOTTOM);
			else if (pState.getValue(AGE) > SECOND_STAGE_MAX_AGE+FIRST_STAGE_MAX_AGE)
				return super.canSurvive(pState, pLevel, pPos) || (isValidCorn(pLevel, pPos.below(1), pState.getValue(AGE), PartProperty.BOTTOM)
						&& isValidCorn(pLevel, pPos.above(1), pState.getValue(AGE), PartProperty.TOP));
			else return false;
		}
		else if (pState.getValue(PART) == PartProperty.TOP) {
			if (pState.getValue(AGE) <= SECOND_STAGE_MAX_AGE+FIRST_STAGE_MAX_AGE)
				return false;
			else if (pState.getValue(AGE) > SECOND_STAGE_MAX_AGE+FIRST_STAGE_MAX_AGE)
				return super.canSurvive(pState, pLevel, pPos) || (isValidCorn(pLevel, pPos.below(1), pState.getValue(AGE), PartProperty.MIDDLE));
			else return false;
		}
		else return canSurvive(pState, pLevel, pPos);
	}
	public boolean isValidCorn(LevelReader pLevel, BlockPos pPos, int needAge, PartProperty needPart) {
		BlockState pState = pLevel.getBlockState(pPos);
		return pState.is(this) && pState.getValue(PART) == needPart; // && pState.getValue(AGE) == needAge

	}
	@Override
	public boolean mayPlaceOn(BlockState pState, BlockGetter p_52303_, BlockPos p_52304_) {
		return ((pState.is(BlockTags.DIRT) || pState.getBlock() instanceof net.minecraft.world.level.block.FarmBlock) &&
				!(pState.getBlock() instanceof CropCornBlock));
	}

	@Override
	public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player player) {
		if (!pLevel.isClientSide) {
			if (player.isCreative()) breakBlock(false, pLevel, pPos, pState, player);
			else breakBlock(true, pLevel, pPos, pState, player);
		}
		return super.playerWillDestroy(pLevel, pPos, pState, player);
	}

	@Override
	public void playerDestroy(Level p_52865_, Player p_52866_, BlockPos p_52867_, BlockState p_52868_, @Nullable BlockEntity p_52869_, ItemStack p_52870_) {
		super.playerDestroy(p_52865_, p_52866_, p_52867_, Blocks.AIR.defaultBlockState(), p_52869_, p_52870_);
	}

	protected static void breakBlock(boolean dropItems, Level pLevel, BlockPos pPos, BlockState pState, Player player) {
		PartProperty blockPart = pState.getValue(PART);
		if (blockPart == PartProperty.MIDDLE) {
			BlockPos blockpos = pPos.below();
			BlockState blockstate = pLevel.getBlockState(blockpos);
			if (blockstate.is(pState.getBlock()) && blockstate.getValue(PART) == PartProperty.BOTTOM) {
				BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
				pLevel.setBlock(blockpos, blockstate1, 35);
				pLevel.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
				if (dropItems) dropResources(blockstate, pLevel, blockpos, null, player, player.getMainHandItem());
			}
		}
		else if (blockPart == PartProperty.TOP) {
			BlockState blockstate1 = pLevel.getBlockState(pPos.below(1));
			BlockState blockstate2 = pLevel.getBlockState(pPos.below(2));
			if (blockstate1.is(pState.getBlock()) && blockstate1.getValue(PART) == PartProperty.MIDDLE &&
					blockstate2.is(pState.getBlock()) && blockstate2.getValue(PART) == PartProperty.BOTTOM)
			{
				BlockState replaceBlockState1 = blockstate1.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
				BlockState replaceBlockState2 = blockstate2.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
				pLevel.setBlock(pPos.below(1), replaceBlockState1, 35);
				pLevel.setBlock(pPos.below(2), replaceBlockState2, 35);
				pLevel.levelEvent(player, 2001, pPos.below(2), Block.getId(blockstate2));
				if (dropItems) dropResources(blockstate2, pLevel, pPos.below(2), null, player, player.getMainHandItem());
			}
		}
		else if (dropItems) dropResources(pState, pLevel, pPos, null, player, player.getMainHandItem());
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

	private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
			Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D),
			Block.box(2.0D, 0.0D, 2.0D, 12.0D, 14.0D, 12.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
			Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)
	};

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return SHAPE_BY_AGE[this.getAge(pState)];
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
//		super.createBlockStateDefinition(builder);
		builder.add(AGE, PART);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(this.getAgeProperty(), 0).setValue(PART, PartProperty.BOTTOM);
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 20;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		return new ItemStack(ConcoctionModItems.CORN_SEEDS.get());
	}

	@Override
	public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
		return PathType.OPEN;
	}

	public int getMaxAge() {
		return MAX_AGE;
	}

	@Override
	public IntegerProperty getAgeProperty() {
		return AGE;
	}

	@Override
	public ItemLike getBaseSeedId() {
		return ConcoctionModItems.CORN_SEEDS.get();
	}

	public enum PartProperty implements StringRepresentable {
		BOTTOM("bottom"), MIDDLE("middle"), TOP("top");

		private final String name;

		private PartProperty(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
}
