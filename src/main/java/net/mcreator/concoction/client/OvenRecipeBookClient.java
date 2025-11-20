package net.mcreator.concoction.client;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModRecipes;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeHolder;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class OvenRecipeBookClient {

    @SubscribeEvent
    public static void registerRecipeBook(RegisterRecipeBookCategoriesEvent event) {
        // Говорим книге: все рецепты типа concoction:oven относятся к категории FURNACE_FOOD
        event.registerRecipeCategoryFinder(
                ConcoctionModRecipes.OVEN_RECIPE_TYPE.get(),
                (RecipeHolder<?> holder) -> RecipeBookCategories.FURNACE_FOOD
        );
    }
}
