package net.mcreator.concoction.worldgen.feature;

import net.mcreator.concoction.ConcoctionMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.List;
import java.util.Optional;

public class CinnamonTreeFeature extends Feature<NoneFeatureConfiguration> {

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, path);
    }

    /**
     * offsetX / offsetZ — позиция нижнего блока ствола
     * относительно (0,0,0) шаблона в БАЗОВОЙ ориентации (без поворота).
     */
    private record TreeVariant(ResourceLocation template, int offsetX, int offsetZ) {}

    // твои измеренные оффсеты
    private static final List<TreeVariant> VARIANTS = List.of(
            new TreeVariant(rl("cinnamon_tree1"), 2, 2),
            new TreeVariant(rl("cinnamon_tree2"), 2, 4),
            new TreeVariant(rl("cinnamon_tree3"), 2, 2),
            new TreeVariant(rl("cinnamon_tree4"), 2, 3),
            new TreeVariant(rl("cinnamon_tree5"), 5, 5)
    );

    public CinnamonTreeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        ServerLevel serverLevel = level.getLevel();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        // выбираем вариант дерева
        TreeVariant variant = VARIANTS.get(random.nextInt(VARIANTS.size()));

        Optional<StructureTemplate> optTemplate = serverLevel
                .getStructureManager()
                .get(variant.template());

        if (optTemplate.isEmpty()) {
            return false;
        }

        StructureTemplate template = optTemplate.get();

        // рандомный поворот
        Rotation rotation = Rotation.getRandom(random);
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(rotation)
                .setMirror(Mirror.NONE)
                .setIgnoreEntities(true);

        // локальная позиция ствола в шаблоне (без поворота)
        BlockPos localTrunkPos = new BlockPos(variant.offsetX(), 0, variant.offsetZ());

        // крутим оффсет так же, как Minecraft крутит блоки структуры
        BlockPos rotatedOffset = StructureTemplate.transform(
                localTrunkPos,
                Mirror.NONE,
                rotation,
                BlockPos.ZERO
        );

        // heightmap даёт позицию воздуха над землёй — туда хотим посадить ствол
        BlockPos trunkSurface = level.getHeightmapPos(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                origin
        );

        // блок ПОД стволом (почва)
        BlockPos groundPos = trunkSurface.below();
        BlockState groundState = level.getBlockState(groundPos);

        // не ставим дерево на жидкость/нетвёрдые вещи
        if (!groundState.getFluidState().isEmpty() || !groundState.isSolid()) {
            return false;
        }

        // ТРЕБУЕМ нормальную почву
        if (!isSoilLike(groundState)) {
            return false;
        }

        // --- простая проверка "8 блоков вокруг" ---

        // проверяем кольцо 3x3 вокруг ствола на высоте ствола и на один блок выше:
        // все эти блоки должны быть воздухом или заменяемыми (трава, кустики и т.п.)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue; // центр — сам ствол, его не трогаем

                for (int dy = 0; dy <= 1; dy++) {
                    BlockPos checkPos = trunkSurface.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(checkPos);

                    if (!isReplaceableAiry(state)) {
                        return false;
                    }
                }
            }
        }

        // --- только теперь реально трогаем мир ---

        // под стволом всегда dirt, как у ванильных деревьев
        level.setBlock(groundPos, Blocks.DIRT.defaultBlockState(), Block.UPDATE_CLIENTS);

        // структура ставится так, что (0,0,0) шаблона окажется в structureOrigin
        // хотим, чтобы rotatedOffset внутри шаблона попал в trunkSurface
        // => structureOrigin = trunkSurface - rotatedOffset
        BlockPos structureOrigin = trunkSurface.subtract(rotatedOffset);

        template.placeInWorld(
                level,
                structureOrigin,
                structureOrigin,
                settings,
                random,
                2
        );

        return true;
    }

    private static boolean isSoilLike(BlockState state) {
        return state.is(BlockTags.DIRT)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.MYCELIUM)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.ROOTED_DIRT);
    }

    // воздух или условно "мягкий" блок, который можно спокойно заменить деревом
    private static boolean isReplaceableAiry(BlockState state) {
        if (state.isAir()) return true;

        // почву вокруг ствола не трогаем, иначе дерево начнёт жрать ландшафт
        if (isSoilLike(state)) return false;

        // canBeReplaced() — современный способ проверки "можно ли это заменить"
        return state.canBeReplaced();
    }
}
