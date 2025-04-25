package net.mcreator.concoction.init;

import com.mojang.serialization.MapCodec;
import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.enchantments.ButcheringEnchantmentEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ConcoctionModEnchantmentsEffects {

    public static final DeferredRegister<MapCodec<? extends EnchantmentEntityEffect>> ENTITY_ENCHANTMENT_EFFECTS =
            DeferredRegister.create(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, ConcoctionMod.MODID);

    public static final Supplier<MapCodec<? extends EnchantmentEntityEffect>> BUTCHERING =
            ENTITY_ENCHANTMENT_EFFECTS.register("butchering", () -> ButcheringEnchantmentEffect.CODEC);

    public static void register(IEventBus eventBus) {
        ENTITY_ENCHANTMENT_EFFECTS.register(eventBus);
    }
}
