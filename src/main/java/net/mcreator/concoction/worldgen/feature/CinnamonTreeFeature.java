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
     * относительно (0,0,0) шаблона в БАЗОВОЙ ориентаЦИИ (без поворота).
     */
    private record TreeVariant(ResourceLocation template, int offsetX, int offsetZ) {}

    private static final List<TreeVariant> VARIANTS = List.of(
            new TreeVariant(rl("cinnamon_tree_large1"), 4, 4),
            new TreeVariant(rl("cinnamon_tree_medium1"), 3, 3),
            new TreeVariant(rl("cinnamon_tree_medium2"), 3, 3),
            new TreeVariant(rl("cinnamon_tree_medium3"), 3, 3),
            new TreeVariant(rl("cinnamon_tree_medium4"), 3, 3),
            new TreeVariant(rl("cinnamon_tree_small1"), 2, 2),
            new TreeVariant(rl("cinnamon_tree_small2"), 3, 3),
            new TreeVariant(rl("cinnamon_tree_small3"), 3, 3)
    );

    public CinnamonTreeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        ServerLevel serverLevel = level.getLevel();
        RandomSource random = context.random();
        BlockPos origin = context.origin(); // ЭТО И ЕСТЬ ТОЧКА СТВОЛА


        // выбираем вариант дерева
        TreeVariant variant = VARIANTS.get(random.nextInt(VARIANTS.size()));

        Optional<StructureTemplate> optTemplate =
                serverLevel.getStructureManager().get(variant.template());
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

        // ствол должен быть РОВНО в origin (где саженец / точка worldgen)
        BlockPos trunkSurface = origin;

        // блок под стволом
        BlockPos groundPos = trunkSurface.below();
        BlockState groundState = level.getBlockState(groundPos);

        if (!groundState.getFluidState().isEmpty() || !groundState.isSolid()) {
            return false;
        }

        if (!isSoilLike(groundState)) {
            return false;
        }

        // маленькая проверка пространства вокруг ствола — 3×3×2
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 1; dy <= 2; dy++) { // проверяем только над стволом
                    BlockPos checkPos = trunkSurface.offset(dx, dy, dz);
                    BlockState existing = level.getBlockState(checkPos);

                    if (existing.isAir()) continue;
                    if (existing.is(BlockTags.LEAVES) || existing.is(BlockTags.LOGS)) continue;
                    if (existing.canBeReplaced()) continue;

                    // берём форму коллизии
                    var shape = existing.getCollisionShape(level, checkPos);

                    // если это НЕ пустая форма и это ПОЛНЫЙ блок (как камень/доски/земля) — считаем препятствием
                    if (!shape.isEmpty() && Block.isShapeFullBlock(shape)) {
                        return false;
                    }

                    // всё, что не full-block (горшок, факел, маленький декор) – игнорируем
                }
            }
        }

        // защита от бедрока в столбе над стволом
        for (int dy = 0; dy <= 16; dy++) {
            if (level.getBlockState(trunkSurface.above(dy)).is(Blocks.BEDROCK)) {
                return false;
            }
        }

        // оффсет ствола внутри шаблона
        BlockPos localTrunkPos = new BlockPos(variant.offsetX(), 0, variant.offsetZ());
        BlockPos rotatedOffset = StructureTemplate.transform(
                localTrunkPos,
                Mirror.NONE,
                rotation,
                BlockPos.ZERO
        );

        // origin структуры: чтобы ствол шаблона пришёлся ровно на trunkSurface
        BlockPos structureOrigin = trunkSurface.subtract(rotatedOffset);

        boolean success = template.placeInWorld(
                level,
                structureOrigin,
                structureOrigin,
                settings,
                random,
                2
        );

        if (success) {
            level.setBlock(groundPos, Blocks.DIRT.defaultBlockState(), Block.UPDATE_CLIENTS);
        }

        return success;
    }

    private static boolean isSoilLike(BlockState state) {
        return state.is(BlockTags.DIRT)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.MYCELIUM)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.ROOTED_DIRT);
    }
}
