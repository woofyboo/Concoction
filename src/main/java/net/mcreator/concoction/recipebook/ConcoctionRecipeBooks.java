package net.mcreator.concoction.recipebook;

import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.mcreator.concoction.recipe.oven.OvenRecipe;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;

public class ConcoctionRecipeBooks {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ConcoctionRecipeBooks::onRegisterRecipeBookCategories);
    }

    private static void onRegisterRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
        event.registerAggregateCategory(
                OvenRecipeBookClientCategories.SEARCH,
                OvenRecipeBookClientCategories.SEARCH_CATEGORIES
        );
        event.registerBookCategories(RecipeBookType.FURNACE, OvenRecipeBookClientCategories.OVEN_CATEGORIES);
        event.registerRecipeCategoryFinder(
                ConcoctionModRecipes.OVEN_RECIPE_TYPE.get(),
                (RecipeHolder<?> holder) -> holder.value() instanceof OvenRecipe ovenRecipe
                        ? OvenRecipeBookClientCategories.toVanilla(ovenRecipe.getCategory())
                        : OvenRecipeBookClientCategories.SNACKS_AND_PASTRY
        );
    }
}
