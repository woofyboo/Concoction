package net.mcreator.concoction.world.worldgen;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.block.CropRiceBlock;
import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class ConcoctionModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILD_RICE_KEY = registerKey("wild_rice");
    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        register(context, WILD_RICE_KEY, Feature.RANDOM_PATCH,
                new RandomPatchConfiguration(
                        20,
                        4,
                        2,
                        PlacementUtils.inlinePlaced(
                                Feature.BLOCK_COLUMN,
                                new BlockColumnConfiguration(
                                    List.of(
                                        BlockColumnConfiguration.layer(ConstantInt.of(1), BlockStateProvider.simple(
                                                    ConcoctionModBlocks.CROP_RICE.get().defaultBlockState().
                                                            setValue(CropRiceBlock.AGE, 5).
                                                            setValue(CropRiceBlock.WATERLOGGED, true).
                                                            setValue(CropRiceBlock.HALF, DoubleBlockHalf.LOWER)
                                                )
                                        ),
                                        BlockColumnConfiguration.layer(ConstantInt.of(1), BlockStateProvider.simple(
                                                ConcoctionModBlocks.CROP_RICE.get().defaultBlockState().
                                                        setValue(CropRiceBlock.AGE, 5).
                                                        setValue(CropRiceBlock.WATERLOGGED, false).
                                                        setValue(CropRiceBlock.HALF, DoubleBlockHalf.UPPER)
                                                )
                                        )
                                    ),
                                    Direction.UP,
                                    BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE,
                                    true
                                ),

                                BlockPredicateFilter.forPredicate(
                                        BlockPredicate.allOf(
                                                BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE,
                                                BlockPredicate.wouldSurvive(ConcoctionModBlocks.CROP_RICE.get().defaultBlockState(), BlockPos.ZERO),
                                                BlockPredicate.anyOf(
                                                        BlockPredicate.matchesFluids(new BlockPos(0, 0, 0), Fluids.WATER, Fluids.FLOWING_WATER),
                                                        BlockPredicate.matchesBlocks(new BlockPos(0, 1, 0), Blocks.AIR)
                                                )
                                        )
                                )
                        )
                )
        );

    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}