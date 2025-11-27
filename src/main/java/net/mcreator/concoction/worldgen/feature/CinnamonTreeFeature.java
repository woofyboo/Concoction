package net.mcreator.concoction.worldgen.feature;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModBlocks;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class CinnamonTreeFeature extends Feature<NoneFeatureConfiguration> {

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, path);
    }

    /**
     * offsetX / offsetZ — позиция нижнего блока ствола
     * относительно (0,0,0) шаблона в базовой ориентации (без поворота).
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

    /**
     * Блоки ствола, которые используются в шаблонах деревьев.
     * Добавь сюда stripped / wood, если они есть в структурах.
     */
    private static final List<Supplier<Block>> CINNAMON_LOG_BLOCKS = List.of(
            ConcoctionModBlocks.CINNAMON_LOG
    );

    /**
     * Блоки листвы, которые используются в шаблонах деревьев.
     */
    private static final List<Supplier<Block>> CINNAMON_LEAF_BLOCKS = List.of(
            ConcoctionModBlocks.CINNAMON_LEAVES
    );

    public CinnamonTreeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        ServerLevel serverLevel = level.getLevel();
        RandomSource random = context.random();
        BlockPos trunkSurface = context.origin(); // позиция саженца / ствола

        // --- проверка почвы под стволом ---
        BlockPos groundPos = trunkSurface.below();
        BlockState groundState = level.getBlockState(groundPos);

        // не вода/лава/воздух
        if (!groundState.getFluidState().isEmpty() || !groundState.isSolid()) {
            return false;
        }

        // должен быть "земле-подобный" блок
        if (!isSoilLike(groundState)) {
            return false;
        }

        // защита от бедрока над головой (перестраховка)
        for (int dy = 0; dy <= 16; dy++) {
            if (level.getBlockState(trunkSurface.above(dy)).is(Blocks.BEDROCK)) {
                return false;
            }
        }

        // --- готовим все комбинации (вариант × поворот) ---
        List<VariantRotation> attempts = new ArrayList<>();
        for (int i = 0; i < VARIANTS.size(); i++) {
            for (Rotation rotation : Rotation.values()) {
                attempts.add(new VariantRotation(i, rotation));
            }
        }
        // перемешали, чтобы форма/поворот дерева были более рандомными
        Collections.shuffle(attempts, new java.util.Random(random.nextLong()));

        // --- пробуем каждую комбинацию по очереди ---
        for (VariantRotation attempt : attempts) {
            TreeVariant variant = VARIANTS.get(attempt.variantIndex());
            Rotation rotation = attempt.rotation();

            Optional<StructureTemplate> optTemplate =
                    serverLevel.getStructureManager().get(variant.template());
            if (optTemplate.isEmpty()) {
                continue;
            }
            StructureTemplate template = optTemplate.get();

            StructurePlaceSettings settings = new StructurePlaceSettings()
                    .setRotation(rotation)
                    .setMirror(Mirror.NONE)
                    .setIgnoreEntities(true);

            // оффсет ствола внутри шаблона (в локальных координатах)
            BlockPos localTrunkPos = new BlockPos(variant.offsetX(), 0, variant.offsetZ());

            // куда этот локальный ствол сдвинется при вращении
            BlockPos rotatedOffset = StructureTemplate.transform(
                    localTrunkPos,
                    Mirror.NONE,
                    rotation,
                    BlockPos.ZERO
            );

            // origin структуры в мире: так, чтобы ствол встал ровно в trunkSurface
            BlockPos structureOrigin = trunkSurface.subtract(rotatedOffset);

            // сначала симулируем пересечения — можно ли ставить такую структуру?
            if (!canPlaceTemplate(level, template, structureOrigin, settings, trunkSurface)) {
                continue; // этот вариант/поворот не подходит, пробуем дальше
            }

            // если симуляция ок — реально ставим структуру
            boolean success = template.placeInWorld(
                    level,
                    structureOrigin,
                    structureOrigin,
                    settings,
                    random,
                    2
            );

            if (success) {
                // делаем под стволом нормальную землю
                level.setBlock(groundPos, Blocks.DIRT.defaultBlockState(), Block.UPDATE_CLIENTS);
                return true;
            }
        }

        // ни одна комбинация (вариант × поворот) не поместилась — не растём
        return false;
    }

    /**
     * Описывает одну попытку: какой вариант дерева и какой поворот.
     */
    private record VariantRotation(int variantIndex, Rotation rotation) {}

    /**
     * Логика пересечений:
     *
     * 1) если ствол/лог пересекается с чем-то незаменяемым и это не листва — нельзя расти;
     * 2) если листва пересекается с чем-то незаменяемым и это не листва — нельзя расти;
     * 3) воздух / structural void внутри шаблона не проверяем вообще.
     */
    private boolean canPlaceTemplate(
            WorldGenLevel level,
            StructureTemplate template,
            BlockPos structureOrigin,
            StructurePlaceSettings settings,
            BlockPos trunkSurface
    ) {
        // Сначала проверяем все блоки ствола
        for (Supplier<Block> sup : CINNAMON_LOG_BLOCKS) {
            Block logBlock = sup.get();
            if (logBlock == null) continue;

            List<StructureTemplate.StructureBlockInfo> infos =
                    template.filterBlocks(structureOrigin, settings, logBlock);

            for (StructureTemplate.StructureBlockInfo info : infos) {
                BlockPos worldPos = info.pos();

                // тут стоит саженец — его мы всегда можем заменить
                if (worldPos.equals(trunkSurface)) {
                    continue;
                }

                BlockState existing = level.getBlockState(worldPos);

                if (!isAllowedToReplaceForTree(existing)) {
                    // лог сталкивается с чем-то "жёстким" → этот вариант дерева не подходит
                    return false;
                }
            }
        }

        // Теперь проверяем листву
        for (Supplier<Block> sup : CINNAMON_LEAF_BLOCKS) {
            Block leafBlock = sup.get();
            if (leafBlock == null) continue;

            List<StructureTemplate.StructureBlockInfo> infos =
                    template.filterBlocks(structureOrigin, settings, leafBlock);

            for (StructureTemplate.StructureBlockInfo info : infos) {
                BlockPos worldPos = info.pos();

                // на всякий случай — вдруг где-то внизу у корней тоже есть листва
                if (worldPos.equals(trunkSurface)) {
                    continue;
                }

                BlockState existing = level.getBlockState(worldPos);

                if (!isAllowedToReplaceForTree(existing)) {
                    // листва упирается в жёсткий блок → не растём этим вариантом
                    return false;
                }
            }
        }

        // воздух / structure_void и любые другие блоки внутри структуры,
        // которые не лог и не листва, не рассматриваются.
        return true;
    }

    /**
     * "Можно ли это место занять деревом?"
     *
     * Разрешаем:
     *  - воздух
     *  - листву
     *  - цветы
     *  - любые replaceable-блоки (трава, цветы, снег, жидкости, лианы и т.п.)
     *
     * Всё остальное = жёсткое препятствие.
     */
    private static boolean isAllowedToReplaceForTree(BlockState existing) {
        // воздух — ок
        if (existing.isAir()) {
            return true;
        }

        // листва — мягкий блок, дерево может в неё вырастать
        if (existing.is(BlockTags.LEAVES)) {
            return true;
        }

        // цветы — явно ок
        if (existing.is(BlockTags.FLOWERS)) {
            return true;
        }

        // всё, что игра считает заменяемым (трава, снег, маленькие растения, жидкости, лианы и т.п.)
        if (existing.canBeReplaced()) {
            return true;
        }

        // всё остальное — твёрдый блок: камень, доски, сундуки, стены, потолок и т.п.
        return false;
    }

    private static boolean isSoilLike(BlockState state) {
        return state.is(BlockTags.DIRT)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.MYCELIUM)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.ROOTED_DIRT);
    }
}
