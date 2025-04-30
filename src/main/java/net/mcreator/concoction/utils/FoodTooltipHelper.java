package net.mcreator.concoction.utils;

import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class FoodTooltipHelper {
    public static void addFoodEffectTooltip(ItemStack stack, List<Component> tooltip) {
        // Проверяем первый эффект
        if (stack.has(ConcoctionModDataComponents.FOOD_EFFECT.get())) {
            var effect = stack.get(ConcoctionModDataComponents.FOOD_EFFECT.get());
            if (effect != null) {
                tooltip.add(effect.type().getTooltip(effect.level(), effect.duration(), effect.isHidden()));
            }
        }
        
        // Проверяем второй эффект
        if (stack.has(ConcoctionModDataComponents.FOOD_EFFECT_2.get())) {
            var effect = stack.get(ConcoctionModDataComponents.FOOD_EFFECT_2.get());
            if (effect != null) {
                tooltip.add(effect.type().getTooltip(effect.level(), effect.duration(), effect.isHidden()));
            }
        }
        
        // Проверяем третий эффект
        if (stack.has(ConcoctionModDataComponents.FOOD_EFFECT_3.get())) {
            var effect = stack.get(ConcoctionModDataComponents.FOOD_EFFECT_3.get());
            if (effect != null) {
                tooltip.add(effect.type().getTooltip(effect.level(), effect.duration(), effect.isHidden()));
            }
        }
        
        // Проверяем четвертый эффект
        if (stack.has(ConcoctionModDataComponents.FOOD_EFFECT_4.get())) {
            var effect = stack.get(ConcoctionModDataComponents.FOOD_EFFECT_4.get());
            if (effect != null) {
                tooltip.add(effect.type().getTooltip(effect.level(), effect.duration(), effect.isHidden()));
            }
        }
        
        // Проверяем пятый эффект
        if (stack.has(ConcoctionModDataComponents.FOOD_EFFECT_5.get())) {
            var effect = stack.get(ConcoctionModDataComponents.FOOD_EFFECT_5.get());
            if (effect != null) {
                tooltip.add(effect.type().getTooltip(effect.level(), effect.duration(), effect.isHidden()));
            }
        }
    }
}
