package net.mcreator.concoction.item.food.passive;

import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public final class FoodPassiveEffects {
    private static final List<FoodPassiveEffectComponent> HONEY_BOTTLE_EFFECTS = List.of(
            FoodPassiveEffectComponent.of(FoodPassiveEffectType.STICKY_VISCOSITY)
    );
    private static final List<FoodPassiveEffectComponent> MILK_BUCKET_EFFECTS = List.of(
            FoodPassiveEffectComponent.of(FoodPassiveEffectType.GENTLE_CLEANSING_PLUS)
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

        return List.of();
    }

    public static ItemStack getRepresentativeStack(FoodPassiveEffectType type) {
        return switch (type) {
            case CRISPY_CRUST -> new ItemStack(ConcoctionModItems.HASHBROWNS.get());
            case JITTERING_JELLY -> new ItemStack(ConcoctionModItems.SWEET_SLIME_JELLY.get());
            case STICKY_VISCOSITY -> new ItemStack(Items.HONEY_BOTTLE);
            case SPICE_INFUSED_MEAT -> new ItemStack(ConcoctionModItems.STIR_FRIED_FILET.get());
            case HOT_BROTH -> new ItemStack(ConcoctionModItems.VEGETABLE_SOUP.get());
            case GENTLE_CLEANSING -> new ItemStack(ConcoctionModItems.MILK_BOTTLE.get());
            case GENTLE_CLEANSING_PLUS -> new ItemStack(Items.MILK_BUCKET);
        };
    }
}
