package net.mcreator.concoction.recipe.cauldron;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mcreator.concoction.block.CookingCauldron;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CauldronBrewingRecipe implements Recipe<CauldronBrewingRecipeInput> {
    private final BlockState inputState;
    private final int cookingTime;
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

    public CauldronBrewingRecipe(BlockState inputState, int cookingTime, List<Ingredient> inputItems, Map<String, String> result) {
        this.inputState = inputState;
        this.cookingTime = cookingTime;
        this.inputItems = inputItems;
        this.result = result;
    }

    private boolean containsAllElements(NonNullList<ItemStack> inventory, List<Ingredient> recipe) {
        // Создаем карту для подсчета количества каждого типа предмета в инвентаре
        Map<Ingredient, Integer> requiredIngredients = new HashMap<>();
        for (Ingredient ingredient : recipe) {
            requiredIngredients.merge(ingredient, 1, Integer::sum);
        }
        
        // Проверяем каждый слот инвентаря
        for (ItemStack itemStack : inventory) {
            if (itemStack.isEmpty()) continue;
            
            // Проверяем каждый требуемый ингредиент
            for (Map.Entry<Ingredient, Integer> entry : requiredIngredients.entrySet()) {
                if (entry.getValue() > 0 && entry.getKey().test(itemStack)) {
                    // Уменьшаем требуемое количество этого ингредиента
                    entry.setValue(entry.getValue() - 1);
                    break;
                }
            }
        }
        
        // Проверяем, что все требуемые ингредиенты найдены (их количество стало 0 или меньше)
        return requiredIngredients.values().stream().allMatch(count -> count <= 0);
    }

    @Override
    public boolean matches(CauldronBrewingRecipeInput pInput, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }
        if (this.inputState.getValue(CookingCauldron.LEVEL).equals(pInput.state().getValue(CookingCauldron.LEVEL))) {
            return containsAllElements(pInput.stack(), this.inputItems);
        }
        return false;
    }

    @Override
    public ItemStack assemble(CauldronBrewingRecipeInput pInput, HolderLookup.Provider pRegistries) {
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
        return new ItemStack(Items.CAULDRON);
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
        return ConcoctionModRecipes.CAULDRON_BREWING_RECIPE_SERIALIZER.get();
    }
    @Override
    public RecipeType<?> getType() {
        return ConcoctionModRecipes.CAULDRON_BREWING_RECIPE_TYPE.get();
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public static class Serializer implements RecipeSerializer<CauldronBrewingRecipe> {
        public static final MapCodec<CauldronBrewingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                BlockState.CODEC.fieldOf("state").forGetter(CauldronBrewingRecipe::getInputState),
                Codec.INT.fieldOf("cooking_time").orElse(200).forGetter(CauldronBrewingRecipe::getCookingTime),
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").forGetter(CauldronBrewingRecipe::getInputItems),
                Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("result").forGetter(CauldronBrewingRecipe::getResult)
        ).apply(inst, CauldronBrewingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CauldronBrewingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), CauldronBrewingRecipe::getInputState,
                        ByteBufCodecs.INT, CauldronBrewingRecipe::getCookingTime,
                        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), CauldronBrewingRecipe::getInputItems,
                        ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8), CauldronBrewingRecipe::getResult,
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

//    private Boolean isLit() {
//        return LIT;
//    }
}