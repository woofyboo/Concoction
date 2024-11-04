
package net.mcreator.concoction.block;

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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.SpecialPlantable;

import net.mcreator.concoction.procedures.SunflowerOnTickUpdateProcedure;
import net.minecraft.world.level.block.CropBlock;

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
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource random) {
        if (!pLevel.isAreaLoaded(pPos, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (pLevel.getRawBrightness(pPos, 0) >= 9 && (pState.getValue(HALF) == DoubleBlockHalf.LOWER)) {
            int nextAge = this.getAge(pState) + (pLevel.getBlockState(pPos.above(1)).is(this) ? this.getAge(pLevel.getBlockState(pPos.above(1))) : 0);
            if (nextAge < this.getMaxAge()) {
                float f = getGrowthSpeed(pState, pLevel, pPos);
                if (net.neoforged.neoforge.common.CommonHooks.canCropGrow(pLevel, pPos, pState, random.nextInt((int)(25.0F / f) + 1) == 0)) {
        			if (nextAge < FIRST_STAGE_MAX_AGE) 
						pLevel.setBlock(pPos, this.getState(pState, nextAge+1, DoubleBlockHalf.LOWER), 2);
	        		else if (nextAge >= FIRST_STAGE_MAX_AGE && (pLevel.getBlockState(pPos.above(1)).is(Blocks.AIR) || 
        			(pLevel.getBlockState(pPos.above(1)).is(this)))) {
				        pLevel.setBlock(pPos.above(1), this.getState(pState, nextAge+1, DoubleBlockHalf.UPPER), 2);
				        pLevel.setBlock(pPos, this.getState(pState, nextAge+1, DoubleBlockHalf.LOWER), 2);
        			}
                    net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost(pLevel, pPos, pState);
                }
            }
        }
        SunflowerOnTickUpdateProcedure.execute(pLevel, pPos.getX(), pPos.getY(), pPos.getZ());
    }
    
	@Override
    public void growCrops(Level pLevel, BlockPos pPos, BlockState pState) {
    	int nextAge = this.getAge(pState) + this.getBonemealAgeIncrease(pLevel);
    	int maxAge = this.getMaxAge();
        if (pState.getValue(HALF) == DoubleBlockHalf.LOWER) {
        	nextAge += pLevel.getBlockState(pPos.above(1)).is(this) ? this.getAge(pLevel.getBlockState(pPos.above(1))) : 0;
			if (nextAge > maxAge) nextAge = maxAge;
			if (nextAge < FIRST_STAGE_MAX_AGE) 
				pLevel.setBlock(pPos, this.getState(pState, nextAge, DoubleBlockHalf.LOWER), 2);
	        else if (nextAge >= FIRST_STAGE_MAX_AGE && (pLevel.getBlockState(pPos.above(1)).is(Blocks.AIR) || 
        	(pLevel.getBlockState(pPos.above(1)).is(this)))) {
		        pLevel.setBlock(pPos.above(1), this.getState(pState, nextAge, DoubleBlockHalf.UPPER), 2);
		        pLevel.setBlock(pPos, this.getState(pState, nextAge, DoubleBlockHalf.LOWER), 2);
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

    public BlockState getState(BlockState pState, int age, DoubleBlockHalf half) {
        return this.defaultBlockState().setValue(this.getAgeProperty(), Integer.valueOf(age)).
        		setValue(FACING, pState.getValue(FACING)).setValue(HALF, half);
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
        return super.canSurvive(pState, pLevel, pPos) || (pLevel.getBlockState(pPos.below(1)).is(this) &&
                pLevel.getBlockState(pPos.below(1)).getValue(AGE) >= FIRST_STAGE_MAX_AGE);
    }

    @Override
    public boolean mayPlaceOn(BlockState pState, BlockGetter p_52303_, BlockPos p_52304_) {
        return pState.is(BlockTags.DIRT) || pState.getBlock() instanceof net.minecraft.world.level.block.FarmBlock;
    }

	@Override
	public void destroy(LevelAccessor level, BlockPos pPos, BlockState pState) {
	    Level pLevel = (Level)level;
	    
	    	if (!pLevel.isClientSide) {
		        if (pState.getValue(HALF) == DoubleBlockHalf.UPPER) {
		           BlockState blockBelow = pLevel.getBlockState(pPos.below(1));
	
		           if (blockBelow.getBlock() == pState.getBlock() && blockBelow.getValue(HALF) == DoubleBlockHalf.LOWER) {
		              pLevel.setBlock(pPos.below(1), Blocks.AIR.defaultBlockState(), 2);
		           }
		        }
	    		}
    }
    
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE, FACING, HALF);
//		super.createBlockStateDefinition(builder);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(this.getAgeProperty(), Integer.valueOf(0)).
				setValue(FACING, FacingProperty.EAST).setValue(HALF, DoubleBlockHalf.LOWER);
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
