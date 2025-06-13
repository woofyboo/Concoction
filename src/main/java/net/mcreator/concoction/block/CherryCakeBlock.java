package net.mcreator.concoction.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CherryCakeBlock extends Block {
	public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 6);

	public CherryCakeBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.WOOL).strength(0.5f).noOcclusion().pushReaction(PushReaction.DESTROY).isRedstoneConductor((bs, br, bp) -> false));
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
	protected BlockState updateShape(BlockState p_51213_, Direction p_51214_, BlockState p_51215_, LevelAccessor p_51216_, BlockPos p_51217_, BlockPos p_51218_) {
		if (p_51214_ == Direction.DOWN && p_51215_.isAir()) {
			return Blocks.AIR.defaultBlockState();
		}
		return super.updateShape(p_51213_, p_51214_, p_51215_, p_51216_, p_51217_, p_51218_);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BITES);
	}

	@Override
	protected boolean canSurvive(BlockState p_51209_, LevelReader p_51210_, BlockPos p_51211_) {
		BlockState belowState = p_51210_.getBlockState(p_51211_.below());
		return belowState.isSolid() || belowState.getBlock() instanceof CherryCakeBlock || belowState.getBlock() instanceof CakeBlock;
	}
}
