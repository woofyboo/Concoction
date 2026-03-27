package net.mcreator.concoction.worldgen.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.ArrayList;
import java.util.List;

public class CinnamonTreeClusterFeature extends Feature<NoneFeatureConfiguration> {
    private static final int MIN_TREES = 3;
    private static final int MAX_TREES = 7;
    private static final int CLUSTER_RADIUS = 32;
    private static final int MIN_TREE_SEPARATION = 8;
    private static final int MIN_CLUSTER_DISTANCE = 256;
    private static final int REGION_SIZE_CHUNKS = MIN_CLUSTER_DISTANCE / 16;
    private static final int REGION_CENTER_OFFSET_BLOCKS = MIN_CLUSTER_DISTANCE / 2;

    public CinnamonTreeClusterFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        if (!isDesignatedClusterChunk(origin)) {
            return false;
        }

        BlockPos center = getClusterCenter(level, origin);

        int targetCount = MIN_TREES + random.nextInt(MAX_TREES - MIN_TREES + 1);
        int attemptBudget = Math.max(40, targetCount * 20);

        List<BlockPos> placedTrees = new ArrayList<>();
        if (tryPlace(level, random, center, placedTrees) && placedTrees.size() >= targetCount) {
            return true;
        }

        for (int attempt = 0; attempt < attemptBudget && placedTrees.size() < targetCount; attempt++) {
            BlockPos candidate = randomClusterPosition(level, center, random);
            if (isTooClose(candidate, placedTrees)) {
                continue;
            }

            tryPlace(level, random, candidate, placedTrees);
        }

        return !placedTrees.isEmpty();
    }

    private static boolean isDesignatedClusterChunk(BlockPos origin) {
        int chunkX = origin.getX() >> 4;
        int chunkZ = origin.getZ() >> 4;
        int regionChunkX = Math.floorDiv(chunkX, REGION_SIZE_CHUNKS) * REGION_SIZE_CHUNKS;
        int regionChunkZ = Math.floorDiv(chunkZ, REGION_SIZE_CHUNKS) * REGION_SIZE_CHUNKS;
        int designatedChunkX = regionChunkX + REGION_SIZE_CHUNKS / 2;
        int designatedChunkZ = regionChunkZ + REGION_SIZE_CHUNKS / 2;
        return chunkX == designatedChunkX && chunkZ == designatedChunkZ;
    }

    private static BlockPos getClusterCenter(WorldGenLevel level, BlockPos origin) {
        int regionOriginX = Math.floorDiv(origin.getX(), MIN_CLUSTER_DISTANCE) * MIN_CLUSTER_DISTANCE;
        int regionOriginZ = Math.floorDiv(origin.getZ(), MIN_CLUSTER_DISTANCE) * MIN_CLUSTER_DISTANCE;
        BlockPos sample = new BlockPos(
                regionOriginX + REGION_CENTER_OFFSET_BLOCKS,
                origin.getY(),
                regionOriginZ + REGION_CENTER_OFFSET_BLOCKS
        );
        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, sample);
    }

    private static boolean tryPlace(WorldGenLevel level, RandomSource random, BlockPos pos, List<BlockPos> placedTrees) {
        if (!CinnamonTreeFeature.placeSingleTree(level, random, pos)) {
            return false;
        }

        placedTrees.add(pos);
        return true;
    }

    private static BlockPos randomClusterPosition(WorldGenLevel level, BlockPos center, RandomSource random) {
        int dx;
        int dz;
        do {
            dx = random.nextInt(CLUSTER_RADIUS * 2 + 1) - CLUSTER_RADIUS;
            dz = random.nextInt(CLUSTER_RADIUS * 2 + 1) - CLUSTER_RADIUS;
        } while (dx * dx + dz * dz > CLUSTER_RADIUS * CLUSTER_RADIUS);

        BlockPos sample = center.offset(dx, 0, dz);
        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, sample);
    }

    private static boolean isTooClose(BlockPos candidate, List<BlockPos> placedTrees) {
        for (BlockPos placedTree : placedTrees) {
            if (placedTree.closerThan(candidate, MIN_TREE_SEPARATION)) {
                return true;
            }
        }
        return false;
    }
}
