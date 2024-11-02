
package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.Mob;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.concoction.procedures.SunflowerOnTickUpdateProcedure;

public class SunflowerBlock extends CropBlock {
	public static final int FIRST_STAGE_MAX_AGE = 4;
    public static final int SECOND_STAGE_MAX_AGE = 4;
	public static final int MAX_AGE = FIRST_STAGE_MAX_AGE + SECOND_STAGE_MAX_AGE;
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

	public static final EnumProperty<HalfProperty> HALF = EnumProperty.create("half", HalfProperty.class);
	public static final EnumProperty<FacingProperty> FACING = EnumProperty.create("facing", FacingProperty.class);

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)};

	public SunflowerBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.GRASS).sound(SoundType.GRASS).strength(0f, 10f).noCollission().noOcclusion().randomTicks().pushReaction(PushReaction.DESTROY).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), Integer.valueOf(0)).setValue(HALF, HalfProperty.UPPER).setValue(FACING, FacingProperty.EAST));
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
	
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!pLevel.isAreaLoaded(pPos, 1)) return;
        if (pLevel.getRawBrightness(pPos, 0) >= 9) {
            int currentAge = this.getAge(pState);
            if (currentAge < this.getMaxAge()) {
                float growthSpeed = getGrowthSpeed(pState, pLevel, pPos);
                if (net.neoforged.neoforge.common.CommonHooks.canCropGrow(pLevel, pPos, pState, pRandom.nextInt((int)(25.0F / growthSpeed) + 1) == 0)) {
                    if(currentAge == FIRST_STAGE_MAX_AGE) {
                        if(pLevel.getBlockState(pPos.above(1)).is(Blocks.AIR)) {
                            pLevel.setBlock(pPos.above(1), this.getStateForAge(currentAge + 1), 2);
                        }
                    } else {
                        pLevel.setBlock(pPos, this.getStateForAge(currentAge + 1), 2);
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
        if(nextAge > maxAge) {
            nextAge = maxAge;
        }

        if(nextAge >= FIRST_STAGE_MAX_AGE && pLevel.getBlockState(pPos.above(1)).is(Blocks.AIR)) {
            pLevel.setBlock(pPos.above(1), this.getStateForAge(nextAge), 2);
        } else {
            pLevel.setBlock(pPos, this.getStateForAge(nextAge), 2);
        }

        // if(this.getAge(pState) == (maxAge-1) && pLevel.getBlockState(pPos.above(1)).is(Blocks.AIR)) {
        //     pLevel.setBlock(pPos, ConcoctionModBlocks.SUNFLOWER.get().defaultBlockState(), 2);
        //     pLevel.setBlock(pPos.above(1), (new Object() {
		// 	public BlockState with(BlockState _bs, String _property, String _newValue) {
		// 		Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty(_property);
		// 		return _prop instanceof EnumProperty _ep && _ep.getValue(_newValue).isPresent() ? _bs.setValue(_ep, (Enum) _ep.getValue(_newValue).get()) : _bs;
		// 		}
		// 	}.with(ConcoctionModBlocks.MINT.get().defaultBlockState(), "half", "upper")), 2);
        // } else {
        //     pLevel.setBlock(pPos, this.getStateForAge(nextAge - 1), 2);
        // }
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return super.canSurvive(pState, pLevel, pPos) || (pLevel.getBlockState(pPos.below(1)).is(this) &&
                pLevel.getBlockState(pPos.below(1)).getValue(AGE) == 7);
    }
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		// super.createBlockStateDefinition(builder);
		builder.add(AGE, HALF, FACING);
	}

	// @Override
	// public BlockState getStateForPlacement(BlockPlaceContext context) {
	// 	return super.getStateForPlacement(context).setValue(HALF, HalfProperty.UPPER).setValue(FACING, FacingProperty.EAST);
	// }

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 100;
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
        return ConcoctionModItems.SUNFLOWER.get();
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

	public enum HalfProperty implements StringRepresentable {
		UPPER("upper"), BOTTOM("bottom");

		private final String name;

		private HalfProperty(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return this.name;
		}
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
