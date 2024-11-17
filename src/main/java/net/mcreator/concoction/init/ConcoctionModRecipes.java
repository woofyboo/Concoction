package net.mcreator.concoction.init;

import net.mcreator.concoction.recipe.cauldron.*;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ConcoctionModRecipes {

    public static final RecipeSerializer<CauldronBrewingRecipe> CAULDRON_BREWING_RECIPE_SERIALIZER = create("cauldron_brewing_recipe", new CauldronBrewingRecipe.Serializer());
    public static final RecipeType<CauldronBrewingRecipe> CAULDRON_BREWING_RECIPE_TYPE = create("cauldron_brewing_recipe");

    private static <T extends Recipe<?>> RecipeSerializer<T> create(String name, RecipeSerializer<T> serializer) {
        RECIPE_SERIALIZERS.put(serializer, Bewitchment.id(name));
        return serializer;
    }

    private static <T extends Recipe<?>> RecipeType<T> create(String name) {
        RecipeType<T> type = new RecipeType<>() {
            @Override
            public String toString() {
                return name;
            }
        };
        RECIPE_TYPES.put(type, Bewitchment.id(name));
        return type;
    }

    public static void init() {
        RECIPE_SERIALIZERS.keySet().forEach(recipeSerializer -> Registry.register(Registries.RECIPE_SERIALIZER, RECIPE_SERIALIZERS.get(recipeSerializer), recipeSerializer));
        RECIPE_TYPES.keySet().forEach(recipeType -> Registry.register(Registries.RECIPE_TYPE, RECIPE_TYPES.get(recipeType), recipeType));
    }
}
