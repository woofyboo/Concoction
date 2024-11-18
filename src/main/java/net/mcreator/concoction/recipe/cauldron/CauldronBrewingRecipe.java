package net.mcreator.concoction.recipe.cauldron;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record CauldronBrewingRecipe(Ingredient inputItem, ItemStack output) implements Recipe<CauldronBrewingRecipeInput> {
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }
    @Override
    public boolean matches(CauldronBrewingRecipeInput pInput, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }
        return inputItem.test(pInput.getItem(0));
    }
    @Override
    public ItemStack assemble(CauldronBrewingRecipeInput pInput, HolderLookup.Provider pRegistries) {
        return output.copy();
    }
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }
    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return output;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ConcoctionModRecipes.CAULDRON_BREWING_RECIPE_SERIALIZER.get();
    }
    @Override
    public RecipeType<?> getType() {
        return ConcoctionModRecipes.CAULDRON_BREWING_RECIPE_TYPE.get();
    }
    public static class Serializer implements RecipeSerializer<CauldronBrewingRecipe> {
        public static final MapCodec<CauldronBrewingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CauldronBrewingRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(CauldronBrewingRecipe::output)
        ).apply(inst, CauldronBrewingRecipe::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, CauldronBrewingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, CauldronBrewingRecipe::inputItem,
                        ItemStack.STREAM_CODEC, CauldronBrewingRecipe::output,
                        CauldronBrewingRecipe::new);
        @Override
        public MapCodec<CauldronBrewingRecipe> codec() {
            return CODEC;
        }
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CauldronBrewingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}