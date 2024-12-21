package net.mcreator.concoction.world.features;

import com.mojang.serialization.Codec;
import net.mcreator.concoction.block.CropRiceBlock;
import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

public class WildRiceFeature extends Feature<ProbabilityFeatureConfiguration> {
    public WildRiceFeature(Codec<ProbabilityFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<ProbabilityFeatureConfiguration> placeContext) {
        RandomSource randomsource = placeContext.random();
        WorldGenLevel worldgenlevel = placeContext.level();
        BlockPos blockpos = placeContext.origin();
        ProbabilityFeatureConfiguration probabilityfeatureconfiguration = placeContext.config();
        int i = randomsource.nextInt(8) - randomsource.nextInt(8);
        int j = randomsource.nextInt(8) - randomsource.nextInt(8);
        int k = worldgenlevel.getHeight(Heightmap.Types.OCEAN_FLOOR, blockpos.getX() + i, blockpos.getZ() + j);
        BlockPos blockpos1 = new BlockPos(blockpos.getX() + i, k, blockpos.getZ() + j);
        if (worldgenlevel.getBlockState(blockpos1).is(Blocks.WATER) && worldgenlevel.getBlockState(blockpos1.above()).is(Blocks.AIR)) {
//            boolean flag1 = randomsource.nextDouble() < (double)probabilityfeatureconfiguration.probability;
            BlockState blockstate = ConcoctionModBlocks.CROP_RICE.get().defaultBlockState();
            if (blockstate.canSurvive(worldgenlevel, blockpos1)) {
                BlockState blockstate_down = blockstate.
                        setValue(CropRiceBlock.HALF, DoubleBlockHalf.LOWER).
                        setValue(CropRiceBlock.AGE, 5).
                        setValue(CropRiceBlock.WATERLOGGED, true);
                BlockState blockstate_up = blockstate.
                        setValue(CropRiceBlock.HALF, DoubleBlockHalf.UPPER).
                        setValue(CropRiceBlock.AGE, 5).
                        setValue(CropRiceBlock.WATERLOGGED, false);
                BlockPos blockpos_up = blockpos1.above();

                worldgenlevel.setBlock(blockpos1, blockstate_down, 2);
                worldgenlevel.setBlock(blockpos_up, blockstate_up, 2);

                return true;
            }
        }
        return false;
    }
}

