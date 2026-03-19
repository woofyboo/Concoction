package net.mcreator.concoction.recipebook;

import net.mcreator.concoction.recipe.oven.OvenRecipeBookCategory;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public final class OvenRecipeBookClientCategories {
    public static final RecipeBookCategories SEARCH = RecipeBookCategories.FURNACE_SEARCH;
    public static final RecipeBookCategories DISHES_AND_SOUPS = RecipeBookCategories.FURNACE_FOOD;
    public static final RecipeBookCategories BEVERAGES_AND_CONDIMENTS = RecipeBookCategories.SMOKER_FOOD;
    public static final RecipeBookCategories SNACKS_AND_PASTRY = RecipeBookCategories.CAMPFIRE;
    public static final RecipeBookCategories FEASTS = RecipeBookCategories.BLAST_FURNACE_MISC;

    public static final List<RecipeBookCategories> OVEN_CATEGORIES = List.of(
            SEARCH,
            DISHES_AND_SOUPS,
            BEVERAGES_AND_CONDIMENTS,
            SNACKS_AND_PASTRY,
            FEASTS
    );

    public static final List<RecipeBookCategories> SEARCH_CATEGORIES = List.of(
            DISHES_AND_SOUPS,
            BEVERAGES_AND_CONDIMENTS,
            SNACKS_AND_PASTRY,
            FEASTS
    );

    private OvenRecipeBookClientCategories() {
    }

    public static RecipeBookCategories toVanilla(OvenRecipeBookCategory category) {
        return switch (category) {
            case DISHES_AND_SOUPS -> OvenRecipeBookClientCategories.DISHES_AND_SOUPS;
            case BEVERAGES_AND_CONDIMENTS -> OvenRecipeBookClientCategories.BEVERAGES_AND_CONDIMENTS;
            case FEASTS -> OvenRecipeBookClientCategories.FEASTS;
            case SNACKS_AND_PASTRY, UNKNOWN -> OvenRecipeBookClientCategories.SNACKS_AND_PASTRY;
        };
    }

    public static ItemStack getIcon(RecipeBookCategories category) {
        if (category == DISHES_AND_SOUPS) {
            return new ItemStack(Items.BOWL);
        }

        if (category == BEVERAGES_AND_CONDIMENTS) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }

        if (category == SNACKS_AND_PASTRY) {
            return new ItemStack(Items.COOKIE);
        }

        if (category == FEASTS) {
            return new ItemStack(Items.CAKE);
        }

        return ItemStack.EMPTY;
    }
}
