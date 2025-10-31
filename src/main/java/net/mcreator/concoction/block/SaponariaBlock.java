package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class SaponariaBlock extends MultifaceBlock implements BonemealableBlock {

    // === Codec: требуется конструктор с Properties ===
    public static final MapCodec<SaponariaBlock> CODEC = simpleCodec(SaponariaBlock::new);
    @Override
    public MapCodec<? extends MultifaceBlock> codec() { return CODEC; }

    // Стадии роста 0..2
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 2);
    private static final float GROW_CHANCE   = 0.18f;
    private static final float SPREAD_CHANCE = 0.22f;

    private final MultifaceSpreader spreader = new MultifaceSpreader(this);

    // Конструктор, который нужен CODEC
    public SaponariaBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState().setValue(AGE, 0));
    }

    // Удобный безаргументный (если твоя регистрация его вызывает)
    public SaponariaBlock() {
        this(defaultProps());
    }

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

    // Должен быть public в 1.21.x
    @Override
    public MultifaceSpreader getSpreader() {
        return this.spreader;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rng) {
        int age = state.getValue(AGE);

        if (age < 2) {
            if (rng.nextFloat() < GROW_CHANCE) {
                level.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_CLIENTS);
            }
            return;
        }

        if (rng.nextFloat() < SPREAD_CHANCE) {
            // базовый спред как у лишайника; позже можно добавить приоритет вниз
            this.spreader.spreadFromRandomFaceTowardRandomDirection(state, level, pos, rng);
        }
    }

    /* ===== BonemealableBlock (сигнатуры 1.21.x) ===== */

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true; // всегда можно ускорить рост/спред
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource rng, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        if (age < 2) {
            level.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_CLIENTS);
        } else {
            for (int i = 0; i < 3; i++) {
                this.spreader.spreadFromRandomFaceTowardRandomDirection(state, level, pos, rng);
            }
        }
    }
}
