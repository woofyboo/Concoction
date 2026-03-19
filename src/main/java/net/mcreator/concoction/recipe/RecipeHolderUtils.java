package net.mcreator.concoction.recipe;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public final class RecipeHolderUtils {
    private RecipeHolderUtils() {
    }

    public static <T extends Recipe<?>> boolean sameRecipe(
            RecipeHolder<T> first,
            RecipeHolder<T> second,
            Function<T, List<Ingredient>> ingredientExtractor
    ) {
        if (first == null || second == null) {
            return false;
        }

        if (first.id().equals(second.id())) {
            return true;
        }

        List<Ingredient> firstIngredients = ingredientExtractor.apply(first.value());
        List<Ingredient> secondIngredients = ingredientExtractor.apply(second.value());
        if (firstIngredients.size() != secondIngredients.size()) {
            return false;
        }

        List<Ingredient> sortedFirst = new ArrayList<>(firstIngredients);
        List<Ingredient> sortedSecond = new ArrayList<>(secondIngredients);
        Comparator<Ingredient> comparator = Comparator.comparing(Ingredient::toString);
        sortedFirst.sort(comparator);
        sortedSecond.sort(comparator);

        for (int i = 0; i < sortedFirst.size(); i++) {
            if (!sortedFirst.get(i).toString().equals(sortedSecond.get(i).toString())) {
                return false;
            }
        }

        return true;
    }
}
