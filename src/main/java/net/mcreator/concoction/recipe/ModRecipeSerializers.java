package net.mcreator.concoction.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModRecipeSerializers {
    public static final String MODID = "concoction";

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SoapCleaningRecipe>> SOAP_CLEANING =
            SERIALIZERS.register("soap_cleaning", SoapCleaningRecipe.Serializer::new);
}
