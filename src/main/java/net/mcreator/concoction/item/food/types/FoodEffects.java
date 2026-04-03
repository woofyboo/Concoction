package net.mcreator.concoction.item.food.types;

import net.mcreator.concoction.block.ConcoctionCakeBlock;
import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public final class FoodEffects {
    private static final List<FoodEffectComponent> LIGHT_BITTERNESS_EFFECTS = List.of(
            FoodEffectComponent.of(FoodEffectType.LIGHT_BITTERNESS, 1, 30)
    );
    private static final List<FoodEffectComponent> TART_BITTERNESS_EFFECTS = List.of(
            FoodEffectComponent.of(FoodEffectType.TART_BITTERNESS, 2, 180)
    );
    private static final List<FoodEffectComponent> BITTERISH_EFFECTS = List.of(
            FoodEffectComponent.of(FoodEffectType.BITTERISH, 1, 180)
    );

    private FoodEffects() {
    }

    public static List<FoodEffectComponent> get(ItemStack stack) {
        if (stack.isEmpty()) {
            return List.of();
        }

        List<FoodEffectComponent> stackEffects = readStackEffects(stack);
        if (!stackEffects.isEmpty()) {
            return stackEffects;
        }

        return getIntrinsicEffects(stack);
    }

    public static List<FoodEffectComponent> getBase(ItemStack stack) {
        if (stack.isEmpty()) {
            return List.of();
        }

        return get(stack.getItem().getDefaultInstance());
    }

    private static List<FoodEffectComponent> readStackEffects(ItemStack stack) {
        List<FoodEffectComponent> effects = new ArrayList<>(5);
        addIfPresent(effects, stack.get(ConcoctionModDataComponents.FOOD_EFFECT.get()));
        addIfPresent(effects, stack.get(ConcoctionModDataComponents.FOOD_EFFECT_2.get()));
        addIfPresent(effects, stack.get(ConcoctionModDataComponents.FOOD_EFFECT_3.get()));
        addIfPresent(effects, stack.get(ConcoctionModDataComponents.FOOD_EFFECT_4.get()));
        addIfPresent(effects, stack.get(ConcoctionModDataComponents.FOOD_EFFECT_5.get()));
        return effects;
    }

    private static void addIfPresent(List<FoodEffectComponent> effects, FoodEffectComponent component) {
        if (component != null) {
            effects.add(component);
        }
    }

    private static List<FoodEffectComponent> getIntrinsicEffects(ItemStack stack) {
        if (stack.getFoodProperties(null) == null) {
            if (stack.getItem() instanceof BlockItem blockItem) {
                if (blockItem.getBlock() instanceof ConcoctionCakeBlock cakeBlock) {
                    return get(cakeBlock.getConsumedSliceStack());
                }

                if (blockItem.getBlock() == Blocks.CAKE) {
                    return get(new ItemStack(ConcoctionModItems.CAKE_SLICE.get()));
                }
            }

            return List.of();
        }

        if (isLightBitternessFood(stack)) {
            return LIGHT_BITTERNESS_EFFECTS;
        }

        if (isTartBitternessFood(stack)) {
            return TART_BITTERNESS_EFFECTS;
        }

        if (isBitterishFood(stack)) {
            return BITTERISH_EFFECTS;
        }

        return List.of();
    }

    private static boolean isLightBitternessFood(ItemStack stack) {
        return stack.is(Items.BEETROOT)
                || stack.is(Items.DRIED_KELP)
                || stack.is(ConcoctionModItems.GREEN_ONION.get())
                || stack.is(ConcoctionModItems.ROASTED_SUNFLOWER_SEEDS.get());
    }

    private static boolean isTartBitternessFood(ItemStack stack) {
        return stack.is(Items.POISONOUS_POTATO)
                || stack.is(Items.PUFFERFISH)
                || stack.is(Items.SPIDER_EYE)
                || stack.is(ConcoctionModItems.DANDELION_TEA.get())
                || stack.is(ConcoctionModItems.REAPPER.get())
                || stack.is(ConcoctionModItems.SUNFLOWER_SEEDS_BREW.get());
    }

    private static boolean isBitterishFood(ItemStack stack) {
        return stack.is(Items.BEETROOT_SOUP)
                || stack.is(ConcoctionModItems.CHOCOLATE.get())
                || stack.is(ConcoctionModItems.MINT_BREW.get());
    }
}
