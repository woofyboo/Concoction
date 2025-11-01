package net.mcreator.concoction.worldgen;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;        // <-- ВАЖНО: ванильный Registries
import net.minecraft.world.level.levelgen.feature.Feature;

public class ModWorldgen {
    public static final String MODID = "concoction";

    // Регистрируем Feature в ванильном реестре
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, MODID);

    public static final DeferredHolder<Feature<?>, Feature<SaponariaPatchConfig>> SAPONARIA_PATCH =
            FEATURES.register("saponaria_patch", () -> new SaponariaPatchFeature(SaponariaPatchConfig.CODEC));
}
