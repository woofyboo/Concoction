package net.mcreator.concoction.world.worldgen;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.block.CropRiceBlock;
import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.mcreator.concoction.world.features.WildRiceFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.level.levelgen.feature.SeagrassFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.material.Fluids;

import java.util.List;

public class ConcoctionModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILD_RICE_KEY = registerKey("wild_rice");
    public static final WildRiceFeature WILD_RICE_FEATURE = Registry.register(BuiltInRegistries.FEATURE, "wild_rice_feature", new WildRiceFeature(ProbabilityFeatureConfiguration.CODEC));

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        register(context, WILD_RICE_KEY, WILD_RICE_FEATURE,
                new ProbabilityFeatureConfiguration(0.0F)
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