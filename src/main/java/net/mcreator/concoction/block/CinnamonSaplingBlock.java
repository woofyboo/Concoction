package net.mcreator.concoction.block;

import com.mojang.serialization.MapCodec;
import net.mcreator.concoction.ConcoctionMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CinnamonSaplingBlock extends BushBlock implements BonemealableBlock {

    // === codec() обязателен для BushBlock в новых версиях ===
    public static final MapCodec<CinnamonSaplingBlock> CODEC =
            simpleCodec(CinnamonSaplingBlock::new);

    @Override
    protected @NotNull MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    public CinnamonSaplingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    private static final VoxelShape SHAPE = Block.box(
            2.0D, 0.0D, 2.0D,   // minX, minY, minZ
            14.0D, 12.0D, 14.0D // maxX, maxY, maxZ
    );

    // то, что рисуется при наведении
    @Override
    public VoxelShape getShape(BlockState state,
                               BlockGetter level,
                               BlockPos pos,
                               CollisionContext context) {
        return SHAPE;
    }

    // а вот СЮДА возвращаем пустую форму, чтобы нельзя было стоять
    @Override
    public VoxelShape getCollisionShape(BlockState state,
                                        BlockGetter level,
                                        BlockPos pos,
                                        CollisionContext context) {
        return Shapes.empty();
    }

    // === РАНДОМНЫЙ РОСТ КАК У САПЛИНГА ===

    @Override
    public void randomTick(@NotNull BlockState state,
                           @NotNull ServerLevel level,
                           @NotNull BlockPos pos,
                           @NotNull RandomSource random) {
        super.randomTick(state, level, pos, random);

        // как у ванильного: достаточно света сверху + шанс
        if (level.getMaxLocalRawBrightness(pos.above()) >= 9 && random.nextInt(7) == 0) {
            tryGrow(level, random, pos, state);
        }
    }

    // === КОСТНАЯ МУКА ===

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level,
                                         @NotNull BlockPos pos,
                                         @NotNull BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level,
                                     @NotNull RandomSource random,
                                     @NotNull BlockPos pos,
                                     @NotNull BlockState state) {
        // как у ванильных саженцев: около 45% шанса вырасти от одной костной муки
        return random.nextFloat() < 0.45F;
    }


    @Override
    public void performBonemeal(@NotNull ServerLevel level,
                                @NotNull RandomSource random,
                                @NotNull BlockPos pos,
                                @NotNull BlockState state) {
        tryGrow(level, random, pos, state);
    }

    // === НА ЧТО МОЖНО СТАВИТЬ САЖЕНЕЦ ===

    @Override
    protected boolean mayPlaceOn(@NotNull BlockState state,
                                 @NotNull BlockGetter level,
                                 @NotNull BlockPos pos) {
        return state.is(BlockTags.DIRT)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.MYCELIUM)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.ROOTED_DIRT)
                || super.mayPlaceOn(state, level, pos);
    }

    // === ЛОГИКА РОСТА В ДЕРЕВО ===

    private void tryGrow(ServerLevel level,
                         RandomSource random,
                         BlockPos pos,
                         BlockState state) {

        // ключ к твоему configured feature concoction:cinnamon_tree
        ResourceKey<ConfiguredFeature<?, ?>> key =
                ResourceKey.create(
                        Registries.CONFIGURED_FEATURE,
                        ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "cinnamon_tree")
                );

        var registry = level.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
        Optional<Holder.Reference<ConfiguredFeature<?, ?>>> holderOpt = registry.getHolder(key);

        if (holderOpt.isEmpty()) {
            // фича не найдена — не растём, чтобы не ловить краши
            return;
        }

        ConfiguredFeature<?, ?> feature = holderOpt.get().value();

        // удаляем саженец перед попыткой вырастить дерево
        level.removeBlock(pos, false);

        boolean success = feature.place(
                level,
                level.getChunkSource().getGenerator(),
                random,
                pos
        );

        // если генерация неудачна (мало места / странный ландшафт) — вернуть саженец
        if (!success) {
            level.setBlock(pos, state, Block.UPDATE_CLIENTS);
        }
    }
}
