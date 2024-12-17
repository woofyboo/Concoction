package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.SpecialPlantable;

import javax.annotation.Nullable;

// Класс растения, наследующий от CropBlock
public class CropRiceBlock extends CropBlock implements SimpleWaterloggedBlock {
	// Максимальный возраст растения
	public static final int FIRST_STAGE_MAX_AGE = 3;
	public static final int SECOND_STAGE_MAX_AGE = 2;
	public static final int MAX_AGE = FIRST_STAGE_MAX_AGE + SECOND_STAGE_MAX_AGE;
	// Свойство возраста растения
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public CropRiceBlock() {
		// Установка свойств блока
		super(BlockBehaviour.Properties.of()
				.mapColor(MapColor.PLANT)
				.sound(SoundType.GRASS)
				.instabreak()
				.noCollission()
				.noOcclusion()
				.randomTicks()
				.pushReaction(PushReaction.DESTROY)
				.isRedstoneConductor((bs, br, bp) -> false));
		// Регистрация состояния по умолчанию
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
	}
	@Override
	protected BlockState updateShape(BlockState p_56285_, Direction p_56286_, BlockState p_56287_, LevelAccessor p_56288_, BlockPos p_56289_, BlockPos p_56290_) {
		if (p_56285_.getValue(WATERLOGGED)) {
			p_56288_.scheduleTick(p_56289_, Fluids.WATER, Fluids.WATER.getTickDelay(p_56288_));
		}

		return super.updateShape(p_56285_, p_56286_, p_56287_, p_56288_, p_56289_, p_56290_);
	}

	@Override
	protected FluidState getFluidState(BlockState p_56299_) {
		return p_56299_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_56299_);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		// Пропускает ли блок свет вниз
		return true;
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		// Количество блокируемого света
		return 0;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		// Визуальная форма блока
		return Shapes.empty();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		// Форма блока в зависимости от возраста
		return switch (state.getValue(AGE)) {
			default -> Block.box(1, 0, 1, 15, 15, 15);

			case 0 -> Block.box(1, 0, 1, 15, 5, 15);
			case 1 -> Block.box(1, 0, 1, 15, 10, 15);
			case 2 -> Block.box(1, 0, 1, 15, 15, 15);
			case 3 -> Block.box(1, 0, 1, 15, 16, 15);
			case 4 -> Block.box(1, 0, 1, 15, 16, 15);
			case 5 -> Block.box(1, 0, 1, 15, 16, 15);
		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		// Добавление свойства возраста в состояние блока
		builder.add(AGE, HALF, WATERLOGGED);
	}

	@Override
	public boolean isRandomlyTicking(BlockState pState) {
		return pState.getValue(AGE) != this.getMaxAge();
	}

	@Override
	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource random) {
		if (!pLevel.isAreaLoaded(pPos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light

		if (pLevel.getRawBrightness(pPos, 0) >= 9 && (pState.getValue(HALF) == DoubleBlockHalf.LOWER) &&
				(pLevel.getBlockState(pPos.above(1)).is(Blocks.AIR) ||
					(pLevel.getBlockState(pPos.above(1)).is(this) && pLevel.getBlockState(pPos.above(1)).getValue(WATERLOGGED) == false))) {
			int nextAge = this.getAge(pState) + 1;
			if (nextAge <= this.getMaxAge()) {
				float f = getGrowthSpeed(pState, pLevel, pPos);
				if (net.neoforged.neoforge.common.CommonHooks.canCropGrow(pLevel, pPos, pState, random.nextInt((int)(25.0F / f) + 1) == 0)) {
					if (nextAge < FIRST_STAGE_MAX_AGE)
						pLevel.setBlock(pPos, this.getState(pState, nextAge, DoubleBlockHalf.LOWER, pState.getValue(WATERLOGGED)), 2);
					else if (nextAge >= FIRST_STAGE_MAX_AGE && (pLevel.getBlockState(pPos.above(1)).is(Blocks.AIR) ||
							(pLevel.getBlockState(pPos.above(1)).is(this)))) {
						pLevel.setBlock(pPos.above(1), this.getState(pState, nextAge, DoubleBlockHalf.UPPER, false), 2);
						pLevel.setBlock(pPos, this.getState(pState, nextAge, DoubleBlockHalf.LOWER, pState.getValue(WATERLOGGED)), 2);
					}
					net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost(pLevel, pPos, pState);
				}
			}
		}
	}

	@Override
	public void growCrops(Level pLevel, BlockPos pPos, BlockState pState) {
		int nextAge = this.getAge(pState) + this.getBonemealAgeIncrease(pLevel);
		int maxAge = this.getMaxAge();
		if (pState.getValue(HALF) == DoubleBlockHalf.LOWER) {
//        	nextAge += pLevel.getBlockState(pPos.above(1)).is(this) ? this.getAge(pLevel.getBlockState(pPos.above(1))) : 0;
			if (nextAge > maxAge) nextAge = maxAge;
			if (nextAge < FIRST_STAGE_MAX_AGE)
				pLevel.setBlock(pPos, this.getState(pState, nextAge, DoubleBlockHalf.LOWER, pState.getValue(WATERLOGGED)), 2);
			else if (nextAge >= FIRST_STAGE_MAX_AGE && (pLevel.getBlockState(pPos.above(1)).is(Blocks.AIR) ||
					(pLevel.getBlockState(pPos.above(1)).is(this)))) {
				pLevel.setBlock(pPos.above(1), this.getState(pState, nextAge, DoubleBlockHalf.UPPER, false), 2);
				pLevel.setBlock(pPos, this.getState(pState, nextAge, DoubleBlockHalf.LOWER, pState.getValue(WATERLOGGED)), 2);
			}
		}
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		// Возвращает горючесть блока
		return 100;
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
		// Предмет, получаемый при копировании блока на колёсико
		return new ItemStack(
            ConcoctionModItems.RICE.get()
            );
	}

	public BlockState getState(BlockState pState, int age, DoubleBlockHalf half, boolean waterlogged) {
		return this.defaultBlockState().setValue(this.getAgeProperty(), Integer.valueOf(age)).
				setValue(WATERLOGGED, waterlogged).setValue(HALF, half);
	}

	@Override
	public boolean isValidBonemealTarget(LevelReader pLevel, BlockPos pPos, BlockState pState) {
		if (pState.getValue(HALF) == DoubleBlockHalf.LOWER)
			return pState.getValue(AGE) != this.getMaxAge();
		else
			return false;
	}

	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		if (pState.getValue(HALF) == DoubleBlockHalf.LOWER) {
			if (pState.getValue(AGE) >= FIRST_STAGE_MAX_AGE)
				return super.canSurvive(pState, pLevel, pPos) && pLevel.getBlockState(pPos.above(1)).is(this) &&
						pState.getValue(WATERLOGGED) == true;
			else
				return super.canSurvive(pState, pLevel, pPos);
		}
		else {
			return super.canSurvive(pState, pLevel, pPos) || (pLevel.getBlockState(pPos.below(1)).is(this) &&
					pState.getValue(WATERLOGGED) == false &&
					pLevel.getBlockState(pPos.below(1)).getValue(AGE) >= FIRST_STAGE_MAX_AGE &&
					pLevel.getBlockState(pPos.below(1)).getValue(HALF) != pState.getValue(HALF));
		}

	}

	@Override
	public boolean mayPlaceOn(BlockState pState, BlockGetter blockGetter, BlockPos pPos) {
		return ((pState.is(BlockTags.DIRT) || pState.is(BlockTags.SAND) || pState.is(Blocks.GRAVEL) || pState.is(Blocks.CLAY) || pState.is(Blocks.MUD)) &&
				blockGetter.getBlockState(pPos.above()).getFluidState().is(Fluids.WATER) && !(pState.getBlock() instanceof CropRiceBlock));
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
		DoubleBlockHalf doubleblockhalf = pState.getValue(HALF);
		if (doubleblockhalf == DoubleBlockHalf.UPPER) {
			BlockPos blockpos = pPos.below();
			BlockState blockstate = pLevel.getBlockState(blockpos);
			if (blockstate.is(pState.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
				BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
				pLevel.setBlock(blockpos, blockstate1, 35);
				pLevel.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
				if (dropItems) dropResources(blockstate, pLevel, blockpos, null, player, player.getMainHandItem());
			}
		}
		else if (dropItems) dropResources(pState, pLevel, pPos, null, player, player.getMainHandItem());
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
	    if (context.getLevel().getBlockState(context.getClickedPos()).getFluidState().is(Fluids.WATER)) {
	        return this.defaultBlockState().setValue(this.getAgeProperty(), Integer.valueOf(0)).
					setValue(WATERLOGGED, true).setValue(HALF, DoubleBlockHalf.LOWER);
		} else {
		return super.getStateForPlacement(context).setValue(this.getAgeProperty(), Integer.valueOf(0)).
				setValue(WATERLOGGED, false).setValue(HALF, DoubleBlockHalf.LOWER);
		}
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		// Скорость распространения огня
		return 25;
	}

	@Override
	public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
		// Тип пути для мобов
		return PathType.OPEN;
	}

	@Override
	public int getMaxAge() {
		// Возвращает максимальный возраст растения
		return MAX_AGE; // не менять
	}

	@Override
	protected ItemLike getBaseSeedId() {
		// Возвращает семена для посадки растения
		return ConcoctionModItems.RICE.get();
	}

	@Override
	public IntegerProperty getAgeProperty() {
		// Возвращает свойство возраста растения
		return AGE; // не менять
	}
}
