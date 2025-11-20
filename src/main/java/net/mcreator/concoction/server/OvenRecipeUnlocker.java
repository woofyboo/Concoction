package net.mcreator.concoction.server;

import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.mcreator.concoction.recipe.oven.OvenRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OvenRecipeUnlocker {

    public static void init() {
        // подписываемся на вход игрока на сервер
        NeoForge.EVENT_BUS.addListener(OvenRecipeUnlocker::onPlayerLogin);
    }

    private static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        RecipeManager recipeManager = player.server.getRecipeManager();

        // все рецепты типа concoction:oven
        List<RecipeHolder<OvenRecipe>> ovenRecipes =
                recipeManager.getAllRecipesFor(ConcoctionModRecipes.OVEN_RECIPE_TYPE.get());

        // приводим к Collection<RecipeHolder<?>>
        Collection<RecipeHolder<?>> unlocked = new ArrayList<>();
        for (RecipeHolder<OvenRecipe> holder : ovenRecipes) {
            unlocked.add(holder);
        }

        // выдаём
        player.awardRecipes(unlocked);
    }
}
