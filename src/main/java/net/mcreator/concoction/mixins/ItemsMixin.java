package net.mcreator.concoction.mixins;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin({Items.class})
public class ItemsMixin {
    @Redirect(slice = @Slice(from = @At(value = "CONSTANT", args = {"stringValue=rabbit_stew"}, ordinal = 0)),
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 0), method = {"<clinit>"})
    private static Item bowl(Item.Properties properties) {
        return new Item((new Item.Properties()).stacksTo(16).food(Foods.RABBIT_STEW));
    }

    @Redirect(slice = @Slice(from = @At(value = "CONSTANT", args = {"stringValue=mushroom_stew"}, ordinal = 0)),
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 0), method = {"<clinit>"})
    private static Item bowl2(Item.Properties properties) {
        return new Item((new Item.Properties()).stacksTo(16).food(Foods.MUSHROOM_STEW));
    }

    @Redirect(slice = @Slice(from = @At(value = "CONSTANT", args = {"stringValue=beetroot_soup"}, ordinal = 0)),
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 0), method = {"<clinit>"})
    private static Item bowl3(Item.Properties properties) {
        return new Item((new Item.Properties()).stacksTo(16).food(Foods.BEETROOT_SOUP));
    }

    @Redirect(slice = @Slice(from = @At(value = "CONSTANT", args = {"stringValue=suspicious_stew"}, ordinal = 0)),
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/SuspiciousStewItem;", ordinal = 0), method = {"<clinit>"})
    private static SuspiciousStewItem bowl4(Item.Properties properties) {
        return new SuspiciousStewItem((new Item.Properties()).stacksTo(16).food(Foods.SUSPICIOUS_STEW));
    }

    @Redirect(slice = @Slice(from = @At(value = "CONSTANT", args = {"stringValue=potion"}, ordinal = 0)),
            at = @At(value = "NEW", target = "(Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/PotionItem;", ordinal = 0), method = {"<clinit>"})
    private static PotionItem potions(Item.Properties properties) {
        return new PotionItem((new Item.Properties()).stacksTo(16).component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY));
    }
}
