
package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.SpecialPlantable;

public class CropMintBlock extends CropBlock {
	public static final int MAX_AGE = 3;
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);
//	public static final TagKey<Block> beeGrowables = BlockTags.create(new ResourceLocation("minecraft", "bee_growables‌"));
//	public static final TagKey<Block> swordEfficient = BlockTags.create(new ResourceLocation("minecraft", "sword_efficient‌‌"));

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D),
            Block.box(3.0D, 0.0D, 3.0D, 13.0D, 10.0D, 13.0D),
            Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D),
            Block.box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D)};


	public CropMintBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS).strength(0f, 1f).noCollission().noOcclusion().randomTicks().pushReaction(PushReaction.DESTROY).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
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
    
//	@Override
//	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
//		return switch (state.getValue(AGE)) {
//			default -> box(4, 0, 4, 12, 8, 12);
//			case 0 -> box(4, 0, 4, 12, 8, 12);
//			case 1 -> box(3, 0, 3, 13, 10, 13);
//			case 2 -> box(1, 0, 1, 15, 15, 15);
//			case 3 -> box(1, 0, 1, 15, 15, 15);
//
//		};
//	}

	@Override
    public void growCrops(Level pLevel, BlockPos pPos, BlockState pState) {
        int nextAge = this.getAge(pState) + this.getBonemealAgeIncrease(pLevel);
        int maxAge = this.getMaxAge();
        if(nextAge > maxAge) {
            nextAge = maxAge;
        }
		 
        if(this.getAge(pState) == (maxAge-1) && pLevel.getBlockState(pPos.above(1)).is(Blocks.AIR)) {
            pLevel.setBlock(pPos, ConcoctionModBlocks.MINT.get().defaultBlockState(), 2);
            pLevel.setBlock(pPos.above(1), (new Object() {
			public BlockState with(BlockState _bs, String _property, String _newValue) {
				Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty(_property);
				return _prop instanceof EnumProperty _ep && _ep.getValue(_newValue).isPresent() ? _bs.setValue(_ep, (Enum) _ep.getValue(_newValue).get()) : _bs;
				}
			}.with(ConcoctionModBlocks.MINT.get().defaultBlockState(), "half", "upper")), 2);
        } else {
            pLevel.setBlock(pPos, this.getStateForAge(nextAge - 1), 2);
        }
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
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 25;
	}

	@Override
	public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
		return PathType.OPEN;
	}

//	@Override
//	public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
//		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
//		return tileEntity instanceof MenuProvider menuProvider ? menuProvider : null;
//	}

	@Override
	public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
		super.triggerEvent(state, world, pos, eventID, eventParam);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		return blockEntity == null ? false : blockEntity.triggerEvent(eventID, eventParam);
	}

	@Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ConcoctionModItems.MINT_SEEDS.get();
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

}
