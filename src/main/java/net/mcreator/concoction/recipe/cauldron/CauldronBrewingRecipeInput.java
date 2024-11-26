package net.mcreator.concoction.recipe.cauldron;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;

public record CauldronBrewingRecipeInput(BlockState state, NonNullList<ItemStack> stack) implements RecipeInput {
    @Override
    public ItemStack getItem(int pIndex) {
        return this.stack.get(pIndex);
    }
    @Override
    public int size() {
        return 6;
    }
}