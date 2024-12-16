package net.mcreator.concoction.mixins;

import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.HoneyBottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.*;

@Mixin({Item.class})
public class ItemMixin {
    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void addEatEffect(ItemStack itemStack, Level p_41410_, LivingEntity player, CallbackInfoReturnable<ItemStack> cir) {
        if (itemStack.get(FOOD_EFFECT.value()) != null && player != null) {
            FoodEffectComponent component = itemStack.get(FOOD_EFFECT.value());
            player.addEffect(FoodEffectType.getEffect(component.type(), component.level(), component.duration(), component.isHidden()));
        }
        if (itemStack.get(FOOD_EFFECT_2.value()) != null && player != null) {
            FoodEffectComponent component = itemStack.get(FOOD_EFFECT_2.value());
            player.addEffect(FoodEffectType.getEffect(component.type(), component.level(), component.duration(), component.isHidden()));
        }
        if (itemStack.get(FOOD_EFFECT_3.value()) != null && player != null) {
            FoodEffectComponent component = itemStack.get(FOOD_EFFECT_3.value());
            player.addEffect(FoodEffectType.getEffect(component.type(), component.level(), component.duration(), component.isHidden()));
        }

    }
}
