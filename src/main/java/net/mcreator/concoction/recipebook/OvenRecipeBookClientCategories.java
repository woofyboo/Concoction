package net.mcreator.concoction.recipebook;

import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.recipe.oven.OvenRecipeBookCategory;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;

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

    public static List<ItemStack> getIcons(RecipeBookCategories category) {
        if (category == DISHES_AND_SOUPS) {
            return List.of(
                    new ItemStack(ConcoctionModItems.NOODLES_WITH_MEATBALLS.get()),
                    new ItemStack(ConcoctionModItems.CHICKEN_CONFIT.get())
            );
        }

        if (category == BEVERAGES_AND_CONDIMENTS) {
            return List.of(
                    new ItemStack(ConcoctionModItems.HOT_CHOCOLATE.get()),
                    new ItemStack(ConcoctionModItems.MINT_BREW.get())
            );
        }

        if (category == SNACKS_AND_PASTRY) {
            return List.of(
                    new ItemStack(ConcoctionModItems.HASHBROWNS.get()),
                    new ItemStack(ConcoctionModItems.HONEY_COB.get())
            );
        }

        if (category == FEASTS) {
            return List.of(
                    new ItemStack(ConcoctionModItems.BUCKET_CASSEROLE.get()),
                    new ItemStack(ConcoctionModItems.TAHCHIN_BUCKET.get())
            );
        }

        return List.<ItemStack>of();
    }
}
