package net.mcreator.concoction.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.mcreator.concoction.init.ConcoctionModItems;

public class SoapLayerBlock extends Block {

    private static final VoxelShape LAYER_SHAPE = box(0, 0, 0, 16, 1, 16);

    public SoapLayerBlock() {
        super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PINK)
            .sound(SoundType.SLIME_BLOCK)     // шаги — как по слизи
            .instabreak()
            .noOcclusion()
            .noCollission()
            .friction(1.2F)                  // скользко как лёд (лед ~0.98F)
            .pushReaction(PushReaction.DESTROY)
            .isRedstoneConductor((bs, br, bp) -> false));
        // ВАЖНО: убрали noCollission(), чтобы по слою реально «стояли» и звук шел от него, а не от блока снизу
    }

    /* --- Внешний вид / свет --- */

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
        return LAYER_SHAPE;
    }

    

    /* --- Дроп/клонирование --- */

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(ConcoctionModItems.SOAP.get());
    }

    /* --- Проходимость для ИИ (как мед/слизь) --- */

    @Override
    public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, net.minecraft.world.entity.Mob entity) {
        return PathType.STICKY_HONEY;
    }

    /* --- Скользкость: доп. страховка (если вдруг свойства не сработают) --- */
    @Override
    public float getFriction(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
        // Поддерживаем ледяную «скользкость» даже если поведение в ядре поменяется
        return 1.2F;
    }

    /* --- Звук шагов по слизи --- */

    /* --- Поддержка/опора снизу --- */

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        // Слой может существовать только если блок снизу имеет твёрдую верхнюю грань
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        // Запрет размещения в воздухе
        BlockState state = this.defaultBlockState();
        return canSurvive(state, ctx.getLevel(), ctx.getClickedPos()) ? state : null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        // Если опора снизу пропала — исчезаем
        if (dir == Direction.DOWN && !canSurvive(state, level, pos)) {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }
        // На всякий случай делаем проверку и при любых соседних изменениях
        if (!canSurvive(state, level, pos)) {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, dir, neighborState, level, pos, neighborPos);
    }
}
