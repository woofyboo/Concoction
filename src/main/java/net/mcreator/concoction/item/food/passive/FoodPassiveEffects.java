package net.mcreator.concoction.item.food.passive;

import net.mcreator.concoction.block.ConcoctionCakeBlock;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public final class FoodPassiveEffects {
    private static final List<FoodPassiveEffectComponent> HONEY_BOTTLE_EFFECTS = List.of(
            FoodPassiveEffectComponent.of(FoodPassiveEffectType.STICKY_VISCOSITY),
            FoodPassiveEffectComponent.of(FoodPassiveEffectType.HONEY_BENEFIT)
    );
    private static final List<FoodPassiveEffectComponent> MILK_BUCKET_EFFECTS = List.of(
            FoodPassiveEffectComponent.of(FoodPassiveEffectType.GENTLE_CLEANSING_PLUS)
    );
    private static final List<FoodPassiveEffectComponent> PUFFERFISH_EFFECTS = List.of(
            FoodPassiveEffectComponent.of(FoodPassiveEffectType.TETRODOTOXIN)
    );
    private static final List<FoodPassiveEffectComponent> BEETROOT_SOUP_EFFECTS = List.of(
            FoodPassiveEffectComponent.of(FoodPassiveEffectType.HOT_BROTH)
    );

    private FoodPassiveEffects() {
    }

    public static List<FoodPassiveEffectComponent> get(ItemStack stack) {
        List<FoodPassiveEffectComponent> passiveEffects = stack.get(ConcoctionModDataComponents.FOOD_PASSIVE_EFFECTS.get());
        if (passiveEffects != null) {
            return passiveEffects;
        }

        if (stack.is(Items.HONEY_BOTTLE)) {
            return HONEY_BOTTLE_EFFECTS;
        }

        if (stack.is(Items.MILK_BUCKET)) {
            return MILK_BUCKET_EFFECTS;
        }

        if (stack.is(Items.PUFFERFISH)) {
            return PUFFERFISH_EFFECTS;
        }

        if (stack.is(Items.BEETROOT_SOUP)) {
            return BEETROOT_SOUP_EFFECTS;
        }

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

    public static ItemStack getRepresentativeStack(FoodPassiveEffectType type) {
        return switch (type) {
            case CRISPY_CRUST -> new ItemStack(ConcoctionModItems.HASHBROWNS.get());
            case JITTERING_JELLY -> new ItemStack(ConcoctionModItems.SWEET_SLIME_JELLY.get());
            case STICKY_VISCOSITY -> new ItemStack(Items.HONEY_BOTTLE);
            case LIGHT_SNACK -> new ItemStack(ConcoctionModItems.POPCORN.get());
            case SPICE_INFUSED_MEAT -> new ItemStack(ConcoctionModItems.STIR_FRIED_FILET.get());
            case HOT_BROTH -> new ItemStack(ConcoctionModItems.VEGETABLE_SOUP.get());
            case SCALDING_HOT -> new ItemStack(ConcoctionModItems.MAGMA_EGG.get());
            case SPICINESS -> new ItemStack(ConcoctionModItems.SPICY_PEPPER.get());
            case CONCENTRATED_SPICINESS -> new ItemStack(ConcoctionModItems.HOT_SAUCE_BOTTLE.get());
            case HONEY_BENEFIT -> new ItemStack(Items.HONEY_BOTTLE);
            case TETRODOTOXIN -> new ItemStack(Items.PUFFERFISH);
            case SPRING_MOOD, CHERRY_JUICE -> new ItemStack(ConcoctionModItems.CHERRY.get());
            case NAUSEATINGLY_VILE -> new ItemStack(ConcoctionModItems.NETHER_SLOP.get());
            case GENTLE_CLEANSING -> new ItemStack(ConcoctionModItems.MILK_BOTTLE.get());
            case GENTLE_CLEANSING_PLUS -> new ItemStack(Items.MILK_BUCKET);
            case SPORE_SEDIMENT -> new ItemStack(ConcoctionModItems.PUFFBALL_SOUP.get());
        };
    }
}
