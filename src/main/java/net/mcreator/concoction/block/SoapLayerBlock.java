package net.mcreator.concoction.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.mcreator.concoction.init.ConcoctionModParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class SoapLayerBlock extends Block {

    private static final VoxelShape LAYER_SHAPE = box(0, 0, 0, 16, 1, 16);

    public SoapLayerBlock() {
        super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_PINK)
            .sound(SoundType.SLIME_BLOCK)
            .instabreak()
            .noOcclusion()
            .noCollission()
            .friction(1.079F)
            .pushReaction(PushReaction.DESTROY)
            .isRedstoneConductor((bs, br, bp) -> false));
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
        return LAYER_SHAPE;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(net.mcreator.concoction.init.ConcoctionModItems.SOAP.get());
    }

    @Override
    public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, net.minecraft.world.entity.Mob entity) {
        return PathType.STICKY_HONEY;
    }

    @Override
    public float getFriction(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
        return 1.079F;
    }

    /* ===========================
       БУБЛЫШКИ ПРИ ПРОХОДЕ ЧЕРЕЗ СЛОЙ
       =========================== */
   @Override
public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
    if (!level.isClientSide) return;
    if (!entity.onGround()) return;

    // реальная горизонтальная скорость по перемещению за тик — даёт тише поток
    double dx = entity.getX() - entity.xOld;
    double dz = entity.getZ() - entity.zOld;
    double speed = Math.sqrt(dx * dx + dz * dz);

    // порог, ниже — пусто
    final double minSpeed = 0.04;
    if (speed < minSpeed) return;

    // дроссель: примерно каждый 4-й тик для данного entity
    if (((level.getGameTime() + entity.getId()) & 3L) != 0L) return;

    // нормируем скорость
    final double maxRefSpeed = 0.20; // «быстрый бег/скольжение»
    double t = Mth.clamp((speed - minSpeed) / (maxRefSpeed - minSpeed), 0.0, 1.0);

    // шанс тика с частицами: реже, чем раньше
    float spawnProb = (float) (0.12 + 0.30 * t); // 12%..42%
    if (level.random.nextFloat() > spawnProb) return;

    // количество частиц (ещё меньше): обычно 1, изредка 2 на очень большой скорости
    int minParticles = 1;
    int maxParticles = 2;
    int count = Mth.clamp((int) Math.round(1 + t * 0.6), minParticles, maxParticles);

    for (int i = 0; i < count; i++) {
        // позиция: ближе к центру, но с более «богатым» разбросом
        // чуть выше слоя (слой 1/16 блока), поднимем ещё на 0.08
        double baseY = pos.getY() + (1.0 / 16.0) + 0.08;

        // равномерный разброс в пределах 0.2..0.8, но с небольшим гауссовым "выплеском"
        double ox = 0.2 + level.random.nextDouble() * 0.6 + level.random.nextGaussian() * 0.04;
        double oz = 0.2 + level.random.nextDouble() * 0.6 + level.random.nextGaussian() * 0.04;
        // не вылезаем за границы блока
        ox = Mth.clamp(ox, 0.1, 0.9);
        oz = Mth.clamp(oz, 0.1, 0.9);

        double x = pos.getX() + ox;
        double y = baseY;
        double z = pos.getZ() + oz;

        // скорости: только вверх, разброс по горизонтали разнообразнее, но мягкий
        double horizJitter = 0.006 + t * 0.006; // 0.006..0.012
        double vx = (level.random.nextGaussian()) * horizJitter;
        double vz = (level.random.nextGaussian()) * horizJitter;

        // базовый подъём + усиление от скорости + небольшой шум, НИЖЕ НУЛЯ НЕ УХОДИМ
        double vy = 0.02 + 0.04 * t + level.random.nextDouble() * 0.02; // всегда > 0

        level.addParticle(ConcoctionModParticleTypes.SOAP_BUBBLE.get(), x, y, z, vx, vy, vz);
    }
    // иногда лопаем пузырёк ванильным звуком
// дроссель по тикам + завязка на скорость (t) + рандом, чтобы не было «пулемёта»
if (((level.getGameTime() + entity.getId()) & 7L) == 0L) { // ~каждый 8-й тик для этого entity
    float popProb = (float) (0.08 + 0.20 * t); // 8%..28% шанс
    if (level.random.nextFloat() < popProb) {
        // звук ставим чуть выше слоя, около центра с небольшим разбросом
        double sx = pos.getX() + 0.3 + level.random.nextDouble() * 0.4;
        double sy = pos.getY() + (1.0 / 16.0) + 0.10;
        double sz = pos.getZ() + 0.3 + level.random.nextDouble() * 0.4;

        float volume = 0.25F + level.random.nextFloat() * 0.15F; // 0.25..0.40
        float pitch  = 0.9F  + level.random.nextFloat() * 0.3F;  // 0.90..1.20

        // клиентский локальный звук (слышен только игроку на этом клиенте)
        level.playLocalSound(sx, sy, sz, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.BLOCKS, volume, pitch, false);
    }
}

}



    /* --- Поддержка/опора снизу --- */

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = this.defaultBlockState();
        return canSurvive(state, ctx.getLevel(), ctx.getClickedPos()) ? state : null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (dir == Direction.DOWN && !canSurvive(state, level, pos)) {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }
        if (!canSurvive(state, level, pos)) {
            return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, dir, neighborState, level, pos, neighborPos);
    }
}
