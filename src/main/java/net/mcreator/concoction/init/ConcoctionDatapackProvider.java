package net.mcreator.concoction.init;

import net.mcreator.concoction.ConcoctionMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.mcreator.concoction.init.ConcoctionModEnchantments;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ConcoctionDatapackProvider extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.ENCHANTMENT, ConcoctionModEnchantments::bootstrap);

    public ConcoctionDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(ConcoctionMod.MODID));
    }
}
