package net.mcreator.concoction.worldgen;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.worldgen.feature.CinnamonTreeClusterFeature;
import net.mcreator.concoction.worldgen.feature.CinnamonTreeFeature;
import net.mcreator.concoction.worldgen.feature.SaponariaPatchConfig;
import net.mcreator.concoction.worldgen.feature.SaponariaPatchFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModWorldgen {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, ConcoctionMod.MODID);

    // коричное дерево
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> CINNAMON_TREE =
            FEATURES.register("cinnamon_tree", CinnamonTreeFeature::new);
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> CINNAMON_TREE_CLUSTER =
            FEATURES.register("cinnamon_tree_cluster", CinnamonTreeClusterFeature::new);

    // сапонария (как у тебя было)
    public static final DeferredHolder<Feature<?>, Feature<SaponariaPatchConfig>> SAPONARIA_PATCH =
            FEATURES.register("saponaria_patch", () -> new SaponariaPatchFeature(SaponariaPatchConfig.CODEC));



    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}
