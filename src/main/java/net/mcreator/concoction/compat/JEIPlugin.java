package net.mcreator.concoction.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModPotions;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.mcreator.concoction.recipe.brewing.SnowflakePotionCraftBrewingRecipe;
import net.mcreator.concoction.recipe.butterChurn.ButterChurnRecipe;
import net.mcreator.concoction.recipe.oven.OvenRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
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
        registration.addRecipeCategories(new ButterChurnRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()));

        registration.addRecipeCategories(new OvenRecipeCategory(
                registration.getJeiHelpers().getGuiHelper()
        ));
    }
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);
        RecipeManager recipeManager = world.getRecipeManager();

        List<ButterChurnRecipe> butterChurnRecipes = recipeManager
                .getAllRecipesFor(ConcoctionModRecipes.BUTTER_CHURN_RECIPE_TYPE.get())
                .stream().map(RecipeHolder::value).toList();
        registration.addRecipes(ButterChurnRecipeCategory.BUTTER_CHURN_RECIPE_TYPE, butterChurnRecipes);

        List<OvenRecipe> ovenRecipes = recipeManager
                .getAllRecipesFor(ConcoctionModRecipes.OVEN_RECIPE_TYPE.get())
                .stream()
                .map(RecipeHolder::value)
                .flatMap(recipe -> expandOvenRecipeForJei(recipe).stream())
                .toList();
        registration.addRecipes(OvenRecipeCategory.OVEN_RECIPE_TYPE, ovenRecipes);

//        registration.getIngredientManager().addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, List.of( new ItemStack(ConcoctionModItems.MINT.get())));

//        registration.addRecipes(SnowflakePotionCraftBrewingRecipe, BrewingRecipe.class);

//        registration.addIngredientInfo(new ItemStack(ConcoctionModItems.MINT.get()), VanillaTypes.ITEM_STACK, Component.translatable("concoction.jei.info.mint"));
//        registration.addIngredientInfo(new ItemStack(ConcoctionModItems.MINT_BREW.get()), VanillaTypes.ITEM_STACK, Component.translatable("concoction.jei.info.mint_brew"));
//        registration.addIngredientInfo(new ItemStack(ConcoctionModItems.MINT_COOKIE.get()), VanillaTypes.ITEM_STACK, Component.translatable("concoction.jei.info.mint_cookie"));
//        registration.addIngredientInfo(new ItemStack(ConcoctionModItems.MINT_CHOCOLATE_CAKE.get()), VanillaTypes.ITEM_STACK, Component.translatable("concoction.jei.info.mint_chocolate_cake"));
//        registration.addIngredientInfo(new ItemStack(ConcoctionModItems.MINTY_SLIME_JELLY.get()), VanillaTypes.ITEM_STACK,Component.translatable("concoction.jei.info.minty_slime_lelly"));
//        for (Item toAdd : List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION, Items.TIPPED_ARROW)) {
//            registration.addIngredientInfo(List.of(PotionContents.createItemStack(toAdd, ConcoctionModPotions.FLAME), PotionContents.createItemStack(toAdd, ConcoctionModPotions.FLAME_EXTENDED)),
//                    VanillaTypes.ITEM_STACK, Component.translatable("concoction.jei.info.flame"));
//
//            registration.addIngredientInfo(List.of(PotionContents.createItemStack(toAdd, ConcoctionModPotions.SNOWFLAKE), PotionContents.createItemStack(toAdd, ConcoctionModPotions.SNOWFLAKE_EXTENDED)),
//                    VanillaTypes.ITEM_STACK, Component.translatable("concoction.jei.info.snowflake"));
//        }

//        registration.addIngredientInfo(List.of(new ItemStack(ConcoctionModItems.WILD_CABBAGES.get()), new ItemStack(ModItems.CABBAGE.get()), new ItemStack(ModItems.CABBAGE_LEAF.get())), VanillaTypes.ITEM_STACK, TextUtils.getTranslation("jei.info.wild_cabbages"));
//        registration.addIngredientInfo(List.of(new ItemStack(ModItems.WILD_BEETROOTS.get()), new ItemStack(Items.BEETROOT)), VanillaTypes.ITEM_STACK, TextUtils.getTranslation("jei.info.wild_beetroots"));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ConcoctionModItems.BUTTER_CHURN.get()), ButterChurnRecipeCategory.BUTTER_CHURN_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ConcoctionModItems.OVEN.get()), OvenRecipeCategory.OVEN_RECIPE_TYPE);
    }

    private static List<OvenRecipe> expandOvenRecipeForJei(OvenRecipe recipe) {
        List<ItemStack> outputStacks = OvenRecipeCategory.getJeiOutputStacks(recipe, recipe.getResult());
        if (outputStacks.size() <= 1) {
            return List.of(recipe);
        }

        List<OvenRecipe> expandedRecipes = new ArrayList<>(outputStacks.size());
        for (ItemStack outputStack : outputStacks) {
            expandedRecipes.add(new JeiOvenRecipe(recipe, outputStack, getJeiCraftingIngredients(recipe, outputStack)));
        }
        return expandedRecipes;
    }

    private static List<Ingredient> getJeiCraftingIngredients(OvenRecipe recipe, ItemStack outputStack) {
        if (!outputStack.is(Items.SUSPICIOUS_STEW)) {
            return recipe.getCraftingIngredients();
        }

        SuspiciousStewEffects suspiciousEffects = outputStack.getOrDefault(DataComponents.SUSPICIOUS_STEW_EFFECTS, SuspiciousStewEffects.EMPTY);
        if (suspiciousEffects.equals(SuspiciousStewEffects.EMPTY)) {
            return recipe.getCraftingIngredients();
        }

        List<ItemStack> matchingFlowers = BuiltInRegistries.ITEM.stream()
                .filter(item -> {
                    SuspiciousEffectHolder holder = SuspiciousEffectHolder.tryGet(item);
                    return holder != null && holder.getSuspiciousEffects().equals(suspiciousEffects);
                })
                .map(ItemStack::new)
                .toList();
        if (matchingFlowers.isEmpty()) {
            return recipe.getCraftingIngredients();
        }

        List<Ingredient> ingredients = new ArrayList<>(recipe.getCraftingIngredients().size());
        boolean replacedFlowerIngredient = false;
        for (Ingredient ingredient : recipe.getCraftingIngredients()) {
            if (!replacedFlowerIngredient && containsSuspiciousFlower(ingredient)) {
                ingredients.add(Ingredient.of(matchingFlowers.stream()));
                replacedFlowerIngredient = true;
            } else {
                ingredients.add(ingredient);
            }
        }

        return ingredients;
    }

    private static boolean containsSuspiciousFlower(Ingredient ingredient) {
        for (ItemStack stack : ingredient.getItems()) {
            if (SuspiciousEffectHolder.tryGet(stack.getItem()) != null) {
                return true;
            }
        }
        return false;
    }
}
