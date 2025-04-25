package net.mcreator.concoction.init;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.enchantments.ButcheringEnchantmentEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentTarget;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;

import java.rmi.registry.Registry;

public class ConcoctionModEnchantments {

    public static final ResourceKey<Enchantment> BUTCHERING = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "butchering"));

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        var enchantment = context.lookup(Registries.ENCHANTMENT);
        var items = context.lookup(Registries.ITEM);

        register(context, BUTCHERING, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                items.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                4,
                2,
                Enchantment.dynamicCost(5,7),
                Enchantment.dynamicCost(25,7),
                2,
                EquipmentSlotGroup.MAINHAND
        )).exclusiveWith(enchantment.getOrThrow(EnchantmentTags.DAMAGE_EXCLUSIVE))
                .withEffect(EnchantmentEffectComponents.POST_ATTACK, EnchantmentTarget.ATTACKER,
                        EnchantmentTarget.VICTIM, new ButcheringEnchantmentEffect()));

    }

    private static void register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
        registry.register(key, builder.build(key.location()));
    }
}
