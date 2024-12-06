
package net.mcreator.concoction.block;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModItems;

import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.RandomSource;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.CropBlock;
import net.neoforged.bus.api.ICancellableEvent;

public class SunflowerBlock extends CropBlock {
	public static final EnumProperty<FacingProperty> FACING = EnumProperty.create("facing", FacingProperty.class);
	public static final int FIRST_STAGE_MAX_AGE = 2;
    public static final int SECOND_STAGE_MAX_AGE = 3;
    public static final int MAX_AGE = FIRST_STAGE_MAX_AGE + SECOND_STAGE_MAX_AGE;
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;


    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(6.0D, 0.0D, 6.0D, 10.0D, 8.0D, 10.0D),
            Block.box(4.0D, 0.0D, 4.0D, 12.0D, 14.0D, 12.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};
  
	public SunflowerBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.GRASS).sound(SoundType.GRASS).strength(0f, 10f).noCollission().noOcclusion().randomTicks().pushReaction(PushReaction.DESTROY).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), Integer.valueOf(0)).setValue(FACING, FacingProperty.EAST).setValue(HALF, DoubleBlockHalf.LOWER));
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
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_BY_AGE[this.getAge(pState)];
    }
    
    @Override
    public boolean isRandomlyTicking(BlockState p_52288_) {
        return true;
    }
    
    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource random) {
		if (!pLevel.isAreaLoaded(pPos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
		this.rotate(pLevel, pPos, pState);
        if (pLevel.getRawBrightness(pPos, 0) >= 9 && (pState.getValue(HALF) == DoubleBlockHalf.LOWER)) {
            int nextAge = this.getAge(pState) + 1;
            if (nextAge <= this.getMaxAge()) {
                float f = getGrowthSpeed(pState, pLevel, pPos);
                if (net.neoforged.neoforge.common.CommonHooks.canCropGrow(pLevel, pPos, pState, random.nextInt((int)(25.0F / f) + 1) == 0)) {
        			if (nextAge < FIRST_STAGE_MAX_AGE) 
						pLevel.setBlock(pPos, this.getState(pState, nextAge, DoubleBlockHalf.LOWER, pState.getValue(FACING)), 2);
	        		else if (nextAge >= FIRST_STAGE_MAX_AGE && (pLevel.getBlockState(pPos.above(1)).is(Blocks.AIR) || 
        			(pLevel.getBlockState(pPos.above(1)).is(this)))) {
				        pLevel.setBlock(pPos.above(1), this.getState(pState, nextAge, DoubleBlockHalf.UPPER, pState.getValue(FACING)), 2);
				        pLevel.setBlock(pPos, this.getState(pState, nextAge, DoubleBlockHalf.LOWER, pState.getValue(FACING)), 2);
        			}
                    net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost(pLevel, pPos, pState);
                }
            }
        }
    }
	public FacingProperty getRotateFacing(LevelAccessor world, BlockState pState) {
		int dayTime = Math.floorMod(world.dayTime(),24000);
		if (dayTime >= 0 && dayTime < 3000) return FacingProperty.EAST;
		else if (dayTime >= 3000 && dayTime < 6000) return FacingProperty.EASTISH;
		else if (dayTime >= 6000 && dayTime < 9000) return FacingProperty.WESTISH;
		else if (dayTime >= 9000) return FacingProperty.WEST;
		else return pState.getValue(FACING);
	}
	public void rotate(LevelAccessor world, BlockPos pPos, BlockState pState) {
//		ConcoctionMod.LOGGER.debug(String.format("rotate sunflower at %d %d %d (%d)", pPos.getX(), pPos.getY(), pPos.getZ(), world.dayTime())); 
		if (world.canSeeSkyFromBelowWater(pPos.above(3)) && world instanceof ServerLevel sLevel &&
				pState.getValue(HALF) == DoubleBlockHalf.LOWER && !sLevel.isRainingAt(pPos)) {
			FacingProperty toRotate = getRotateFacing(world, pState);
			world.setBlock(pPos, pState.setValue(FACING, toRotate), Block.UPDATE_ALL);
			if (world.getBlockState(pPos.above(1)).is(this))
				world.setBlock(pPos.above(1), this.getState(pState, pState.getValue(AGE), DoubleBlockHalf.UPPER, toRotate), Block.UPDATE_ALL);

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
				pLevel.setBlock(pPos, this.getState(pState, nextAge, DoubleBlockHalf.LOWER, pState.getValue(FACING)), 2);
	        else if (nextAge >= FIRST_STAGE_MAX_AGE && (pLevel.getBlockState(pPos.above(1)).is(Blocks.AIR) || 
        	(pLevel.getBlockState(pPos.above(1)).is(this)))) {
		        pLevel.setBlock(pPos.above(1), this.getState(pState, nextAge, DoubleBlockHalf.UPPER, pState.getValue(FACING)), 2);
		        pLevel.setBlock(pPos, this.getState(pState, nextAge, DoubleBlockHalf.LOWER, pState.getValue(FACING)), 2);
        	}
        }
    
//    	else {
//    		nextAge += pLevel.getBlockState(pPos.below(1)).is(this) : this.getAge(pLevel.getBlockState(pPos.below(1))) ? 0;
//    		if (nextAge > maxAge) nextAge = maxAge;
//    		if (nextAge >= FIRST_STAGE_MAX_AGE && pLevel.getBlockState(pPos.above(1)).is(Blocks.AIR)) {
//		        pLevel.setBlock(pPos.above(1), this.getState(pState, nextAge, DoubleBlockHalf.UPPER), 2);
//		        pLevel.setBlock(pPos, this.getState(pState, nextAge, DoubleBlockHalf.LOWER), 2);
//    		}
//    	}
    }

    public BlockState getState(BlockState pState, int age, DoubleBlockHalf half, FacingProperty facing) {
        return this.defaultBlockState().setValue(this.getAgeProperty(), Integer.valueOf(age)).
        		setValue(FACING, pState.getValue(FACING)).setValue(HALF, half).setValue(FACING, facing);
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
	        	return super.canSurvive(pState, pLevel, pPos) && pLevel.getBlockState(pPos.above(1)).is(this);	
	        else
	        	return super.canSurvive(pState, pLevel, pPos);
    	} 
    	else {
	        return super.canSurvive(pState, pLevel, pPos) || (pLevel.getBlockState(pPos.below(1)).is(this) &&
                pLevel.getBlockState(pPos.below(1)).getValue(AGE) >= FIRST_STAGE_MAX_AGE &&
                pLevel.getBlockState(pPos.below(1)).getValue(HALF) != pState.getValue(HALF));
    	}

    }

    @Override
    public boolean mayPlaceOn(BlockState pState, BlockGetter p_52303_, BlockPos p_52304_) {
        return ((pState.is(BlockTags.DIRT) || pState.getBlock() instanceof net.minecraft.world.level.block.FarmBlock) &&
        	!(pState.getBlock() instanceof SunflowerBlock));
    }

    @Override
    public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player player) {
        if (!pLevel.isClientSide) {
            if (player.isCreative()) breakBlock(false, pLevel, pPos, pState, player);
            else breakBlock(true, pLevel, pPos, pState, player);
        }
        return super.playerWillDestroy(pLevel, pPos, pState, player);
    }

//	@Override
//	public void destroy(LevelAccessor level, BlockPos pPos, BlockState pState) {
//    	if (level instanceof Level pLevel && !pLevel.isClientSide) {
//    		breakBlock(true, pLevel, pPos, pState, null);
//    	}
//	}

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
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE, FACING, HALF);
//		super.createBlockStateDefinition(builder);
	}

	@Override
	protected void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block p_60512_, BlockPos neighbor, boolean p_60514_) {
		BlockState neighborState = pLevel.getBlockState(neighbor);
		if (!pLevel.isClientSide() && pState.is(this) && pState.getValue(HALF) == DoubleBlockHalf.LOWER &&
				neighborState.is(this) && !pState.getValue(FACING).equals(neighborState.getValue(FACING))) {
			if (pState.getValue(HALF) == DoubleBlockHalf.LOWER) {
				ConcoctionMod.queueServerWork(RandomSource.create().nextInt(20), () -> {
				if (pLevel.getBlockState(pPos.above(1)).is(this))
					pLevel.setBlock(pPos, pState.setValue(FACING, neighborState.getValue(FACING)), Block.UPDATE_ALL);
				if (pLevel.getBlockState(pPos.above(1)).is(this))
					pLevel.setBlock(pPos.above(1), this.getState(pState, pState.getValue(AGE),
						DoubleBlockHalf.UPPER, getRotateFacing(pLevel, neighborState)), Block.UPDATE_ALL);
				});
			}
		}
		super.neighborChanged(pState, pLevel, pPos, p_60512_, neighbor, p_60514_);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FacingProperty toRotate = getRotateFacing(context.getLevel(), this.defaultBlockState());
		return super.getStateForPlacement(context).setValue(this.getAgeProperty(), Integer.valueOf(0)).
				setValue(FACING, toRotate).setValue(HALF, DoubleBlockHalf.LOWER);
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 100;
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
        return ConcoctionModItems.SUNFLOWER_SEEDS.get();
    }
    
	public enum FacingProperty implements StringRepresentable {
		EAST("east"), EASTISH("eastish"), WESTISH("westish"), WEST("west");

		private final String name;

		private FacingProperty(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
	}
}
