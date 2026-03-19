package net.mcreator.concoction.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RecipeIngredientMatcher {
    private RecipeIngredientMatcher() {
    }

    public static boolean matchesExactly(List<Ingredient> recipe, List<ItemStack> stacks) {
        return remainingRequirements(recipe, stacks) != null;
    }

    public static Map<Ingredient, Integer> remainingRequirements(List<Ingredient> recipe, List<ItemStack> stacks) {
        int nonEmptyStacks = 0;
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                nonEmptyStacks++;
            }
        }
        if (nonEmptyStacks != recipe.size()) {
            return null;
        }

        Map<Ingredient, Integer> requiredIngredients = createRequirements(recipe);
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) {
                continue;
            }

            boolean matched = false;
            for (Map.Entry<Ingredient, Integer> entry : requiredIngredients.entrySet()) {
                if (entry.getValue() > 0 && entry.getKey().test(stack)) {
                    entry.setValue(entry.getValue() - 1);
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return null;
            }
        }

        return requiredIngredients;
    }

    public static Map<Ingredient, Integer> createRequirements(List<Ingredient> recipe) {
        Map<Ingredient, Integer> requiredIngredients = new HashMap<>();
        for (Ingredient ingredient : recipe) {
            requiredIngredients.merge(ingredient, 1, Integer::sum);
        }
        return requiredIngredients;
    }

    public static NonNullList<ItemStack> slice(NonNullList<ItemStack> stacks, int startInclusive, int endExclusive) {
        NonNullList<ItemStack> slice = NonNullList.withSize(endExclusive - startInclusive, ItemStack.EMPTY);
        for (int i = startInclusive; i < endExclusive; i++) {
            slice.set(i - startInclusive, stacks.get(i));
        }
        return slice;
    }
}
