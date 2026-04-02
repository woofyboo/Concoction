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
    private static final List<FoodPassiveEffectComponent> SPIDER_EYE_EFFECTS = List.of(
            FoodPassiveEffectComponent.of(FoodPassiveEffectType.SPIDER_VENOM)
    );
    private static final List<FoodPassiveEffectComponent> RAW_CHICKEN_EFFECTS = List.of(
            FoodPassiveEffectComponent.of(FoodPassiveEffectType.SALMONELLOSIS)
    );
    private static final List<FoodPassiveEffectComponent> POISONOUS_POTATO_EFFECTS = List.of(
            FoodPassiveEffectComponent.of(FoodPassiveEffectType.BLIGHTED_POTATO)
    );
    private static final List<FoodPassiveEffectComponent> CHORUS_FRUIT_EFFECTS = List.of(
            FoodPassiveEffectComponent.of(FoodPassiveEffectType.OTHERWORLDLY_MALADAPTATION)
    );
    private static final List<FoodPassiveEffectComponent> OBSIDIAN_TEARS_BOTTLE_EFFECTS = List.of(
            FoodPassiveEffectComponent.of(FoodPassiveEffectType.OTHERWORLDLY_MALADAPTATION)
    );
    private static final List<FoodPassiveEffectComponent> ROTTEN_FLESH_EFFECTS = List.of(
            FoodPassiveEffectComponent.of(FoodPassiveEffectType.ROTTEN_MEAT)
    );
    private static final List<FoodPassiveEffectComponent> GOLDEN_APPLE_EFFECTS = List.of(
            FoodPassiveEffectComponent.of(FoodPassiveEffectType.GILDED_RESTORATION)
    );
    private static final List<FoodPassiveEffectComponent> ENCHANTED_GOLDEN_APPLE_EFFECTS = List.of(
            FoodPassiveEffectComponent.of(FoodPassiveEffectType.GILDED_RESTORATION_PLUS)
    );
    private static final List<FoodPassiveEffectComponent> BEETROOT_SOUP_EFFECTS = List.of(
            FoodPassiveEffectComponent.of(FoodPassiveEffectType.HOT_BROTH)
    );
    private static final List<FoodPassiveEffectComponent> KOZINAK_EFFECTS = List.of(
            FoodPassiveEffectComponent.of(FoodPassiveEffectType.SUGAR_CRYSTALLIZATION)
    );

    private FoodPassiveEffects() {
    }

    public static List<FoodPassiveEffectComponent> get(ItemStack stack) {
        if (stack.isEmpty()) {
            return List.of();
        }

        List<FoodPassiveEffectComponent> passiveEffects = stack.get(ConcoctionModDataComponents.FOOD_PASSIVE_EFFECTS.get());
        if (passiveEffects != null) {
            return passiveEffects;
        }

        return getIntrinsicEffects(stack);
    }

    public static List<FoodPassiveEffectComponent> getBase(ItemStack stack) {
        if (stack.isEmpty()) {
            return List.of();
        }

        return get(stack.getItem().getDefaultInstance());
    }

    private static List<FoodPassiveEffectComponent> getIntrinsicEffects(ItemStack stack) {
        if (stack.is(Items.HONEY_BOTTLE)) {
            return HONEY_BOTTLE_EFFECTS;
        }

        if (stack.is(Items.MILK_BUCKET)) {
            return MILK_BUCKET_EFFECTS;
        }

        if (stack.is(Items.PUFFERFISH)) {
            return PUFFERFISH_EFFECTS;
        }

        if (stack.is(Items.SPIDER_EYE)) {
            return SPIDER_EYE_EFFECTS;
        }

        if (stack.is(Items.CHICKEN)) {
            return RAW_CHICKEN_EFFECTS;
        }

        if (stack.is(Items.POISONOUS_POTATO)) {
            return POISONOUS_POTATO_EFFECTS;
        }

        if (stack.is(Items.CHORUS_FRUIT)) {
            return CHORUS_FRUIT_EFFECTS;
        }

        if (stack.is(Items.ROTTEN_FLESH)) {
            return ROTTEN_FLESH_EFFECTS;
        }

        if (stack.is(Items.GOLDEN_APPLE)) {
            return GOLDEN_APPLE_EFFECTS;
        }

        if (stack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
            return ENCHANTED_GOLDEN_APPLE_EFFECTS;
        }

        if (stack.is(Items.BEETROOT_SOUP)) {
            return BEETROOT_SOUP_EFFECTS;
        }

        if (stack.is(ConcoctionModItems.KOZINAK.get())) {
            return KOZINAK_EFFECTS;
        }

        if (stack.is(ConcoctionModItems.OBSIDIAN_TEARS_BOTTLE.get())) {
            return OBSIDIAN_TEARS_BOTTLE_EFFECTS;
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
            case SUGAR_CRYSTALLIZATION -> new ItemStack(ConcoctionModItems.KOZINAK.get());
            case JITTERING_JELLY -> new ItemStack(ConcoctionModItems.SWEET_SLIME_JELLY.get());
            case STICKY_VISCOSITY -> new ItemStack(Items.HONEY_BOTTLE);
            case LIGHT_SNACK -> new ItemStack(ConcoctionModItems.POPCORN.get());
            case SPICE_INFUSED_MEAT -> new ItemStack(ConcoctionModItems.STIR_FRIED_FILET.get());
            case HOT_BROTH -> new ItemStack(ConcoctionModItems.VEGETABLE_SOUP.get());
            case SCALDING_HOT -> new ItemStack(ConcoctionModItems.MAGMA_EGG.get());
            case SPICINESS -> new ItemStack(ConcoctionModItems.SPICY_PEPPER.get());
            case CONCENTRATED_SPICINESS -> new ItemStack(ConcoctionModItems.HOT_SAUCE_BOTTLE.get());
            case HONEY_BENEFIT -> new ItemStack(Items.HONEY_BOTTLE);
            case ROTTEN_MEAT -> new ItemStack(Items.ROTTEN_FLESH);
            case TETRODOTOXIN -> new ItemStack(Items.PUFFERFISH);
            case SPIDER_VENOM -> new ItemStack(Items.SPIDER_EYE);
            case SALMONELLOSIS -> new ItemStack(Items.CHICKEN);
            case BLIGHTED_POTATO -> new ItemStack(Items.POISONOUS_POTATO);
            case OTHERWORLDLY_MALADAPTATION -> new ItemStack(Items.CHORUS_FRUIT);
            case GILDED_RESTORATION -> new ItemStack(Items.GOLDEN_APPLE);
            case GILDED_RESTORATION_PLUS -> new ItemStack(Items.ENCHANTED_GOLDEN_APPLE);
            case SPRING_MOOD, CHERRY_JUICE -> new ItemStack(ConcoctionModItems.CHERRY.get());
            case NAUSEATINGLY_VILE -> new ItemStack(ConcoctionModItems.NETHER_SLOP.get());
            case GENTLE_CLEANSING -> new ItemStack(ConcoctionModItems.MILK_BOTTLE.get());
            case GENTLE_CLEANSING_PLUS -> new ItemStack(Items.MILK_BUCKET);
            case SPORE_SEDIMENT -> new ItemStack(ConcoctionModItems.PUFFBALL_SOUP.get());
            case GOOD_MORNING -> new ItemStack(ConcoctionModItems.BACON_AND_EGGS.get());
        };
    }
}
