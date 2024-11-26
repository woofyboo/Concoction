package net.mcreator.concoction.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.mcreator.concoction.recipe.cauldron.CauldronBrewingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "jei_plugin");
    }
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CauldronRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()));
    }
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);
//        registration.addRecipes(ConcoctionModRecipes.CAULDRON_BREWING_RECIPE_TYPE, BloodMagicAPI.INSTANCE.getRecipeRegistrar().getTartaricForgeRecipes(world));

        RecipeManager recipeManager = world.getRecipeManager();
        List<CauldronBrewingRecipe> cookingCauldronRecipes = recipeManager
                .getAllRecipesFor(ConcoctionModRecipes.CAULDRON_BREWING_RECIPE_TYPE.get())
                .stream().map(RecipeHolder::value).toList();

        registration.addRecipes(CauldronRecipeCategory.CAULDRON_RECIPE_TYPE, cookingCauldronRecipes);
    }
//    @Override
//    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
//        registration.addRecipeClickArea(CauldronScreen.class, 70, 30, 25, 20,
//                CauldronRecipeCategory.CAULDRON_RECIPE_TYPE);
//    }
}