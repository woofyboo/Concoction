package net.mcreator.concoction.recipe.oven;

import com.mojang.serialization.Codec;
import net.mcreator.concoction.recipe.RecipeOutputData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Locale;

public enum OvenRecipeBookCategory {
    UNKNOWN(""),
    DISHES_AND_SOUPS("dishes_and_soups"),
    BEVERAGES_AND_CONDIMENTS("beverages_and_condiments"),
    SNACKS_AND_PASTRY("snacks_and_pastry"),
    FEASTS("feasts");

    public static final Codec<OvenRecipeBookCategory> CODEC = Codec.STRING.xmap(
            OvenRecipeBookCategory::fromSerializedName,
            OvenRecipeBookCategory::getSerializedName
    );

    private final String serializedName;

    OvenRecipeBookCategory(String serializedName) {
        this.serializedName = serializedName;
    }

    public String getSerializedName() {
        return this.serializedName;
    }

    public static OvenRecipeBookCategory fromSerializedName(String name) {
        if (name == null || name.isBlank()) {
            return UNKNOWN;
        }

        String normalized = name.toLowerCase(Locale.ROOT);
        for (OvenRecipeBookCategory value : values()) {
            if (value.serializedName.equals(normalized)) {
                return value;
            }
        }

        return switch (normalized) {
            case "food", "blocks", "misc" -> UNKNOWN;
            default -> UNKNOWN;
        };
    }

    public static OvenRecipeBookCategory resolve(OvenRecipeBookCategory category,
                                                 Ingredient bottleIngredient,
                                                 Ingredient bowlIngredient,
                                                 RecipeOutputData result) {
        if (category != null && category != UNKNOWN) {
            return category;
        }

        if (usesItem(bowlIngredient, Items.BUCKET) || hasResultWord(result, "bucket") || hasResultWord(result, "casserole")) {
            return FEASTS;
        }

        if (usesItem(bowlIngredient, Items.BOWL)) {
            return DISHES_AND_SOUPS;
        }

        if (!bowlIngredient.isEmpty()) {
            return SNACKS_AND_PASTRY;
        }

        if (looksLikeBottleFood(result) || !bottleIngredient.isEmpty()) {
            return BEVERAGES_AND_CONDIMENTS;
        }

        return SNACKS_AND_PASTRY;
    }

    private static boolean usesItem(Ingredient ingredient, Item item) {
        for (ItemStack stack : ingredient.getItems()) {
            if (stack.is(item)) {
                return true;
            }
        }

        return false;
    }

    private static boolean looksLikeBottleFood(RecipeOutputData result) {
        return hasResultWord(result, "bottle")
                || hasResultWord(result, "sauce")
                || hasResultWord(result, "tea")
                || hasResultWord(result, "juice")
                || hasResultWord(result, "oil")
                || hasResultWord(result, "brew")
                || hasResultWord(result, "drink")
                || hasResultWord(result, "condiment")
                || hasResultWord(result, "syrup");
    }

    private static boolean hasResultWord(RecipeOutputData result, String word) {
        ResourceLocation itemId = result.itemId();
        return itemId != null && itemId.getPath().contains(word);
    }
}
