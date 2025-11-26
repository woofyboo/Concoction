package net.mcreator.concoction.worldgen;

import net.mcreator.concoction.ConcoctionMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Optional;

public class CinnamonTreeGrower {

    // ключ на configured feature concoction:cinnamon_tree
    public static final ResourceKey<ConfiguredFeature<?, ?>> CINNAMON_TREE_KEY =
            ResourceKey.create(
                    Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "cinnamon_tree")
            );

    // наш TreeGrower для корицы
    public static final TreeGrower CINNAMON = new TreeGrower(
            "concoction_cinnamon",
            /* megaTree (2x2 большие деревья) */ Optional.empty(),
            /* обычное дерево */ Optional.of(CINNAMON_TREE_KEY),
            /* "цветочная" версия */ Optional.empty()
    );
}
