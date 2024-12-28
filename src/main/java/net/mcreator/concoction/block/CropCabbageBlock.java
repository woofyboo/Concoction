package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.SpecialPlantable;

// Класс растения, наследующий от CropBlock
public class CropCabbageBlock extends CropBlock {
	// Максимальный возраст растения
	public static final int MAX_AGE = 3;
	// Свойство возраста растения
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

	public CropCabbageBlock() {
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

			case 0 -> Block.box(4, 0, 4, 12, 8, 12);
			case 1 -> Block.box(3, 0, 3, 13, 8, 13);
			case 2 -> Block.box(4, 0, 4, 12, 8, 12);
			case 3 -> Block.box(3, 0, 3, 13, 10, 13);
			
		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		// Добавление свойства возраста в состояние блока
		builder.add(AGE);
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
            ConcoctionModItems.COTTON.get()
            );
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
		return ConcoctionModItems.COTTON.get();
	}

	@Override
	public IntegerProperty getAgeProperty() {
		// Возвращает свойство возраста растения
		return AGE; // не менять
	}
}
