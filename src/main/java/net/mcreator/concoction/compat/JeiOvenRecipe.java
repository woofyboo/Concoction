package net.mcreator.concoction.compat;

import net.mcreator.concoction.recipe.oven.OvenRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class JeiOvenRecipe extends OvenRecipe {
    private final ItemStack jeiOutputStack;
    private final List<Ingredient> jeiCraftingIngredients;

    public JeiOvenRecipe(OvenRecipe baseRecipe, ItemStack jeiOutputStack, List<Ingredient> jeiCraftingIngredients) {
        super(
                baseRecipe.getGroup(),
                baseRecipe.getCategory(),
                baseRecipe.getCookingTime(),
                jeiCraftingIngredients,
                baseRecipe.getBottleIngredient(),
                baseRecipe.getBowlIngredient(),
                baseRecipe.getResult()
        );
        this.jeiOutputStack = jeiOutputStack.copy();
        this.jeiCraftingIngredients = List.copyOf(jeiCraftingIngredients);
    }

    public ItemStack getJeiOutputStack() {
        return this.jeiOutputStack.copy();
    }

    @Override
    public List<Ingredient> getCraftingIngredients() {
        return this.jeiCraftingIngredients;
    }
}
