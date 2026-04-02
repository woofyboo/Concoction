package net.mcreator.concoction.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipeSerializers {
    public static final String MODID = "concoction";

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SoapCleaningRecipe>> SOAP_CLEANING =
            SERIALIZERS.register("soap_cleaning", SoapCleaningRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SoapShieldCleaningRecipe>> SOAP_SHIELD_CLEANING =
            SERIALIZERS.register("soap_shield_cleaning", SoapShieldCleaningRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HotSauceAdditionRecipe>> HOT_SAUCE_ADDITION =
            SERIALIZERS.register("hot_sauce_addition", HotSauceAdditionRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FoodSplitRecipe>> FOOD_SPLIT =
            SERIALIZERS.register("food_split", FoodSplitRecipe.Serializer::new);
}
