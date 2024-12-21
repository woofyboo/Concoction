package net.mcreator.concoction.datagen;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.world.worldgen.ConcoctionModBiomeModifiers;
import net.mcreator.concoction.world.worldgen.ConcoctionModConfiguredFeatures;
import net.mcreator.concoction.world.worldgen.ConcoctionModPlacedFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ConcoctionModDatapackProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
//            .add(Registries.TRIM_MATERIAL, ModTrimMaterials::bootstrap)
//            .add(Registries.TRIM_PATTERN, ModTrimPatterns::bootstrap)
//            .add(Registries.ENCHANTMENT, ModEnchantments::bootstrap)

            .add(Registries.CONFIGURED_FEATURE, ConcoctionModConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ConcoctionModPlacedFeatures::bootstrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ConcoctionModBiomeModifiers::bootstrap);

    public ConcoctionModDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(ConcoctionMod.MODID));
    }
}