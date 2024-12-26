package net.mcreator.concoction.recipe.butterChurn;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mcreator.concoction.block.ButterChurnBlock;
import net.mcreator.concoction.block.CookingCauldron;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.mcreator.concoction.recipe.cauldron.CauldronBrewingRecipe;
import net.mcreator.concoction.recipe.cauldron.CauldronBrewingRecipeInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ButterChurnRecipe implements Recipe<ButterChurnRecipeInput> {
    private final BlockState inputState;
    private final List<Ingredient> inputItems;
    private final Map<String, String> result;

    public BlockState getInputState() {
        return inputState;
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

    public Map<String, String> getResult() {
        return result;
    }

    public ButterChurnRecipe(BlockState inputState, List<Ingredient> inputItems, Map<String, String> result) {
        this.inputState = inputState;
        this.inputItems = inputItems;
        this.result = result;
    }

    @Override
    public boolean matches(ButterChurnRecipeInput pInput, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }
        if (this.inputState.getValue(ButterChurnBlock.FULL).equals(pInput.state().getValue(ButterChurnBlock.FULL))) {
            return this.inputItems.getFirst().getItems()[0].getItem().equals(pInput.getItem(0).getItem()) &&
                    this.inputItems.size() == pInput.getItem(0).getCount();
        }
        return false;
    }

    @Override
    public ItemStack assemble(ButterChurnRecipeInput pInput, HolderLookup.Provider pRegistries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_336125_) {
        return null;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ConcoctionModItems.BUTTER_CHURN.get());
    }

    public Map<String, String> getOutput() {
        return result;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ConcoctionModRecipes.BUTTER_CHURN_RECIPE_SERIALIZER.get();
    }
    @Override
    public RecipeType<?> getType() {
        return ConcoctionModRecipes.BUTTER_CHURN_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ButterChurnRecipe> {
        public static final MapCodec<ButterChurnRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                BlockState.CODEC.fieldOf("state").forGetter(ButterChurnRecipe::getInputState),
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").forGetter(ButterChurnRecipe::getInputItems),
                Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("result").forGetter(ButterChurnRecipe::getResult)
        ).apply(inst, ButterChurnRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ButterChurnRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), ButterChurnRecipe::getInputState,
                        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), ButterChurnRecipe::getInputItems,
                        ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8), ButterChurnRecipe::getResult,
                        ButterChurnRecipe::new);

        @Override
        public MapCodec<ButterChurnRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ButterChurnRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
