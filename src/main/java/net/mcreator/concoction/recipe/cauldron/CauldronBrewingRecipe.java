package net.mcreator.concoction.recipe.cauldron;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mcreator.concoction.block.CookingCauldron;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.mcreator.concoction.recipe.RecipeIngredientMatcher;
import net.mcreator.concoction.recipe.RecipeOutputData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class CauldronBrewingRecipe implements Recipe<CauldronBrewingRecipeInput> {
    private final BlockState inputState;
    private final int cookingTime;
    private final List<Ingredient> inputItems;
    private final RecipeOutputData result;

    public CauldronBrewingRecipe(BlockState inputState, int cookingTime, List<Ingredient> inputItems, RecipeOutputData result) {
        this.inputState = inputState;
        this.cookingTime = cookingTime;
        this.inputItems = inputItems;
        this.result = result;
    }

    public BlockState getInputState() {
        return inputState;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public List<Ingredient> getInputItems() {
        return inputItems;
    }

    public Ingredient getIngredient(int index) {
        if (index < 0 || index >= this.inputItems.size()) {
            return Ingredient.EMPTY;
        }
        return this.inputItems.get(index);
    }

    public RecipeOutputData getResult() {
        return result;
    }

    public RecipeOutputData getOutput() {
        return result;
    }

    @Override
    public boolean matches(CauldronBrewingRecipeInput input, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        if (this.inputState.getValue(CookingCauldron.LEVEL).equals(input.state().getValue(CookingCauldron.LEVEL))) {
            return RecipeIngredientMatcher.matchesExactly(this.inputItems, RecipeIngredientMatcher.slice(input.stack(), 0, 4));
        }
        return false;
    }

    @Override
    public ItemStack assemble(CauldronBrewingRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.toStack();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Items.CAULDRON);
    }

    @Override
    public boolean isSpecial() {
        return true;
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
                BlockState.CODEC.fieldOf("state").forGetter(CauldronBrewingRecipe::getInputState),
                Codec.INT.fieldOf("cooking_time").orElse(200).forGetter(CauldronBrewingRecipe::getCookingTime),
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").forGetter(CauldronBrewingRecipe::getInputItems),
                RecipeOutputData.CODEC.fieldOf("result").forGetter(CauldronBrewingRecipe::getResult)
        ).apply(inst, CauldronBrewingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CauldronBrewingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), CauldronBrewingRecipe::getInputState,
                        ByteBufCodecs.INT, CauldronBrewingRecipe::getCookingTime,
                        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), CauldronBrewingRecipe::getInputItems,
                        RecipeOutputData.STREAM_CODEC, CauldronBrewingRecipe::getResult,
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
