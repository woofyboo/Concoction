package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SaponariaBlock extends MultifaceBlock implements BonemealableBlock {

    public static final MapCodec<SaponariaBlock> CODEC = simpleCodec(SaponariaBlock::new);
    @Override public MapCodec<? extends MultifaceBlock> codec() { return CODEC; }

    public static final IntegerProperty AGE   = IntegerProperty.create("age",   0, 2);
    public static final IntegerProperty VIGOR = IntegerProperty.create("vigor", 0, 2);

    private static final float GROW_CHANCE   = 0.18f;
    private static final float SPREAD_CHANCE = 0.22f;

    private final MultifaceSpreader spreader = new MultifaceSpreader(this);
    @Override public MultifaceSpreader getSpreader() { return this.spreader; }

    public SaponariaBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(AGE, 0)
                .setValue(VIGOR, 2));
    }
    public SaponariaBlock() { this(defaultProps()); }
    private static BlockBehaviour.Properties defaultProps() {
        return BlockBehaviour.Properties
                .of().mapColor(MapColor.COLOR_PINK)
                .sound(SoundType.GRASS)
                .noOcclusion()
                .randomTicks()
                .instabreak()
                .pushReaction(PushReaction.DESTROY)
                .isRedstoneConductor((bs, br, bp) -> false);
    }

    /* ---------- тики ---------- */
    @Override public boolean isRandomlyTicking(BlockState state) { return true; }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rng) {
        int vigor = state.getValue(VIGOR);
        if (vigor <= 0) return;

        int age = state.getValue(AGE);
        if (age < 2) {
            if (rng.nextFloat() < GROW_CHANCE) {
                level.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_CLIENTS);
            }
            return;
        }
        if (rng.nextFloat() < SPREAD_CHANCE) {
            trySpreadOnce(level, pos, state, rng);
        }
    }

    /* ---------- коллизия ---------- */
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    /* ---------- ручное добавление граней: только при AGE==0 ---------- */
    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext ctx) {
        BlockState candidate = this.getStateForPlacement(ctx);
        if (candidate != null && candidate.getBlock() == this && state.getValue(AGE) == 0) {
            for (Direction d : Direction.values()) {
                BooleanProperty p = getFaceProperty(d);
                if (candidate.hasProperty(p) && state.hasProperty(p)
                        && candidate.getValue(p) && !state.getValue(p)
                        && canAttachHere(ctx.getLevel(), ctx.getClickedPos(), d)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* ---------- выживание ---------- */
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (!level.getFluidState(pos).isEmpty()) return false; // никогда не в жидкость
        BlockState here = level.getBlockState(pos);
        if (here.getBlock() != this && !here.isAir() && !isReplaceableVegetation(here)) return false;
        return super.canSurvive(state, level, pos);
    }

    /* ---------- костная мука ---------- */
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        if (state.getValue(AGE) < 2) return true;
        return hasAnyValidSpreadTarget(level, pos);
    }
    @Override
    public boolean isBonemealSuccess(Level level, RandomSource r, BlockPos pos, BlockState st) {
        return isValidBonemealTarget(level, pos, st);
    }
    @Override
    public void performBonemeal(ServerLevel level, RandomSource rng, BlockPos pos, BlockState state) {
        if (state.getValue(AGE) < 2) {
            level.setBlock(pos, state.setValue(AGE, state.getValue(AGE) + 1), Block.UPDATE_CLIENTS);
        } else {
            for (int i = 0; i < 4; i++) {
                if (trySpreadOnce(level, pos, state, rng)) break;
            }
        }
    }

    /* ---------- стейт ---------- */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        super.createBlockStateDefinition(b);
        b.add(AGE, VIGOR);
    }

    /* ====================== helpers ====================== */

    private boolean canAttachHere(LevelReader level, BlockPos pos, Direction d) {
        if (!level.getFluidState(pos).isEmpty()) return false;
        BlockPos supportPos = pos.relative(d);
        BlockState support = level.getBlockState(supportPos);
        return canAttachTo(level, d, pos, support);
    }

    // для 1.21.1 достаточно REPLACEABLE
    private static boolean isReplaceableVegetation(BlockState state) {
        return state.is(BlockTags.REPLACEABLE);
    }

    private boolean hasAnyValidSpreadTarget(LevelReader level, BlockPos origin) {
        for (Direction dir : Direction.values()) {
            BlockPos targetPos = origin.relative(dir);
            if (!level.getFluidState(targetPos).isEmpty()) continue;
            BlockState target = level.getBlockState(targetPos);
            if (!(target.isAir() || isReplaceableVegetation(target))) continue;

            for (Direction face : Direction.values()) {
                BlockPos supportPos = targetPos.relative(face);
                BlockState support = level.getBlockState(supportPos);
                if (canAttachTo(level, face, targetPos, support)) return true;
            }
        }
        return false;
    }

    /** Одна попытка расползания: ставим НОВЫЙ блок в воздух/на replaceable без жидкости;
     *  после установки «заливаем» остальные поддерживаемые грани этой же клетки. */
    private boolean trySpreadOnce(ServerLevel level, BlockPos pos, BlockState parent, RandomSource rng) {
        int parentVigor = parent.getValue(VIGOR);

        Direction[] order = new Direction[]{Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP};
        for (int i = 1; i < order.length - 1; i++) {
            int j = 1 + rng.nextInt(order.length - 2);
            Direction tmp = order[i]; order[i] = order[j]; order[j] = tmp;
        }

        for (Direction dir : order) {
            BlockPos targetPos = pos.relative(dir);
            if (!level.getFluidState(targetPos).isEmpty()) continue;

            BlockState target = level.getBlockState(targetPos);
            if (!(target.isAir() || isReplaceableVegetation(target))) continue;

            // подбираем опору (любая грань)
            Direction[] faces = Direction.values();
            for (int i = 0; i < faces.length; i++) {
                int j = rng.nextInt(faces.length);
                Direction tmp = faces[i]; faces[i] = faces[j]; faces[j] = tmp;
            }
            for (Direction face : faces) {
                BlockPos supportPos = targetPos.relative(face);
                BlockState support = level.getBlockState(supportPos);
                if (!canAttachTo(level, face, targetPos, support)) continue;

                if (!target.isAir() && isReplaceableVegetation(target)) {
                    level.destroyBlock(targetPos, false);
                }

                int childVigor = computeChildVigor(rng, parentVigor);
                BlockState placed = this.defaultBlockState()
                        .setValue(AGE, 0)
                        .setValue(VIGOR, childVigor)
                        .setValue(getFaceProperty(face), true);

                if (this.canSurvive(placed, level, targetPos)) {
                    level.setBlock(targetPos, placed, Block.UPDATE_CLIENTS);
                    // ДОБИРАЕМ остальные валидные грани на новой клетке
                    fillAllSupportedFaces(level, targetPos);
                    return true;
                }
            }
        }
        return false;
    }

    /** Для существующего нашего блока стадии 0 можно добрать недостающие грани при добавлении одной — чтобы не было «резких обрывов». */
    private void fillAllSupportedFaces(ServerLevel level, BlockPos pos) {
        BlockState current = level.getBlockState(pos);
        if (current.getBlock() != this) return;

        BlockState updated = current;
        for (Direction d : Direction.values()) {
            BooleanProperty p = getFaceProperty(d);
            if (updated.getValue(p)) continue; // грань уже есть
            // не ставим грань, если в ячейке флюид
            if (!level.getFluidState(pos).isEmpty()) continue;

            BlockPos supportPos = pos.relative(d);
            BlockState support = level.getBlockState(supportPos);
            if (canAttachTo(level, d, pos, support)) {
                updated = updated.setValue(p, true);
            }
        }
        if (updated != current && this.canSurvive(updated, level, pos)) {
            level.setBlock(pos, updated, Block.UPDATE_CLIENTS);
        }
    }

    private static int computeChildVigor(RandomSource rng, int parentVigor) {
        if (parentVigor <= 0) return 0;
        boolean decrease = rng.nextFloat() < 0.75f;
        int val = parentVigor - (decrease ? 1 : 0);
        if (val < 0) val = 0;
        if (val > 2) val = 2;
        return val;
    }
}
