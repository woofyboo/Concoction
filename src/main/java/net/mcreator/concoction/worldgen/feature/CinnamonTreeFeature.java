package net.mcreator.concoction.worldgen.feature;

import net.mcreator.concoction.ConcoctionMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;

import java.util.List;
import java.util.Optional;

public class CinnamonTreeFeature extends Feature<NoneFeatureConfiguration> {

    private static final List<ResourceLocation> VARIANTS = List.of(
            ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "cinnamon_tree1"),
            ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "cinnamon_tree2"),
            ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "cinnamon_tree3"),
            ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "cinnamon_tree4")
    );

    public CinnamonTreeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();

        // ServerLevel нужен только чтобы достать шаблон
        ServerLevel serverLevel = level.getLevel();

        RandomSource random = context.random();
        BlockPos origin = context.origin();

        ConcoctionMod.LOGGER.info("[CINNAMON TREE] worldgen try at {}, {} (y={})",
                origin.getX(), origin.getZ(), origin.getY());

        // привязываемся к поверхности
        BlockPos basePos = level.getHeightmapPos(
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                origin
        );

        // выбираем случайный вариант дерева
        ResourceLocation rl = VARIANTS.get(random.nextInt(VARIANTS.size()));
        Optional<StructureTemplate> optTemplate = serverLevel.getStructureManager().get(rl);
        if (optTemplate.isEmpty()) {
            ConcoctionMod.LOGGER.warn("Cinnamon tree template {} not found", rl);
            return false;
        }

        StructureTemplate template = optTemplate.get();

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(Rotation.getRandom(random))
                .setMirror(Mirror.NONE)
                .setIgnoreEntities(true);

        // ВАЖНО: ставим в WorldGenLevel, а не в ServerLevel
        template.placeInWorld(
                level,      // ← вот тут раньше был serverLevel
                basePos,
                basePos,
                settings,
                random,
                2
        );

        return true;
    }
}
