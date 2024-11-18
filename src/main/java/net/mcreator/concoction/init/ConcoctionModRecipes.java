package net.mcreator.concoction.init;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.recipe.cauldron.CauldronBrewingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ConcoctionModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, ConcoctionMod.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, ConcoctionMod.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CauldronBrewingRecipe>> CAULDRON_BREWING_RECIPE_SERIALIZER =
            SERIALIZERS.register("cauldron_brewing", CauldronBrewingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<CauldronBrewingRecipe>> CAULDRON_BREWING_RECIPE_TYPE =
            TYPES.register("cauldron_brewing", () -> new RecipeType<CauldronBrewingRecipe>() {
                @Override
                public String toString() {
                    return "crystallizing";
                }
            });
    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
