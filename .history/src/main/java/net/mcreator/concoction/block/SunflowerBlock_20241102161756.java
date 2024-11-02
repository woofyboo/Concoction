
package net.mcreator.concoction.block;

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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.Mob;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.mcreator.concoction.procedures.SunflowerOnTickUpdateProcedure;

public class SunflowerBlock extends Block {
	public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 5);
	public static final EnumProperty<HalfProperty> HALF = EnumProperty.create("half", HalfProperty.class);
	public static final EnumProperty<FacingProperty> FACING = EnumProperty.create("facing", FacingProperty.class);

	public SunflowerBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.GRASS).sound(SoundType.GRASS).strength(0f, 10f).noCollission().noOcclusion().randomTicks().pushReaction(PushReaction.DESTROY).isRedstoneConductor((bs, br, bp) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0).setValue(HALF, HalfProperty.UPPER).setValue(FACING, FacingProperty.EAST));
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
		return box(4, 0, 4, 12, 8, 12);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(STAGE, HALF, FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(STAGE, 0).setValue(HALF, HalfProperty.UPPER).setValue(FACING, FacingProperty.EAST);
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 100;
	}

	@Override
	public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
		return PathType.OPEN;
	}

	@Override
	public void randomTick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
		super.randomTick(blockstate, world, pos, random);
		SunflowerOnTickUpdateProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
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
