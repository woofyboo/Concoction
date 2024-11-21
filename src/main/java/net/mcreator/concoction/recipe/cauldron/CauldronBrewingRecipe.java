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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CauldronBrewingRecipe implements Recipe<CauldronBrewingRecipeInput> {
    private final BlockState inputState;
    private final Boolean isCooking;
    private final List<Ingredient> inputItems;
    private final ItemStack result;

    public BlockState getInputState() {
        return inputState;
    }

    public List<Ingredient> getInputItems() {
        return inputItems;
    }

    public ItemStack getResult() {
        return result;
    }

    public Boolean isCooking() {
        return isCooking;
    }

    public CauldronBrewingRecipe(BlockState inputState, Boolean isCooking, List<Ingredient> inputItems, ItemStack result) {
        this.inputState = inputState;
        this.isCooking = isCooking;
        this.inputItems = inputItems;
        this.result = result;
    }

    @Override
    public boolean matches(CauldronBrewingRecipeInput pInput, Level pLevel) {
        if(pLevel.isClientSide()) {
            return false;
        }
        if (this.isCooking == pInput.isCooking()
                && this.inputState.getValue(CookingCauldron.LEVEL).equals(pInput.state().getValue(CookingCauldron.LEVEL))) {
            return containsAllElements(pInput.stack(), this.inputItems);
//            for (Ingredient inputItem : this.inputItems) {
//                ItemStack[] checkItems = inputItem.getItems();
//                ItemStack checkItem = checkItems[0];
//                if (!pInput.stack().contains(checkItem))
//                    return false;
//            }
//            return true;
        }
        return false;
    }

    public static boolean containsAllElements(NonNullList<ItemStack> list1, List<Ingredient> list2) {
        // Подсчитываем количество каждого объекта в первом списке
        Map<Item, Integer> countMap1 = new HashMap<>();
        for (ItemStack item : list1) {
            if (item.getItem() != Items.AIR)
                countMap1.put(item.getItem(), countMap1.getOrDefault(item.getItem(), 0) + 1);
        }

        // Подсчитываем количество каждого объекта во втором списке
        Map<Item, Integer> countMap2 = new HashMap<>();
        for (Ingredient ingr : list2) {
            countMap2.put(ingr.getItems()[0].getItem(), countMap2.getOrDefault(ingr.getItems()[0].getItem(), 0) + 1);
        }

        // Проверяем, что каждый объект из второго списка содержится в первом в нужном количестве
        if (countMap1.size() != countMap2.size()) return false; // Если количество различается
        for (Map.Entry<Item, Integer> entry : countMap2.entrySet()) {
            Item key = entry.getKey();
            int requiredCount = entry.getValue();
            int availableCount = countMap1.getOrDefault(key, 0);

            if (availableCount != requiredCount) return false; // Если объектов недостаточно

        }

        return true; // Все объекты содержатся в нужном количестве
    }


    @Override
    public ItemStack assemble(CauldronBrewingRecipeInput pInput, HolderLookup.Provider pRegistries) {
        return result;
    }
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }
    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return result.copy();
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
                Codec.BOOL.fieldOf("isCooking").forGetter(CauldronBrewingRecipe::isCooking),
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").forGetter(CauldronBrewingRecipe::getInputItems),
                ItemStack.CODEC.fieldOf("result").forGetter(CauldronBrewingRecipe::getResult)
        ).apply(inst, CauldronBrewingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CauldronBrewingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), CauldronBrewingRecipe::getInputState,
                        ByteBufCodecs.BOOL, CauldronBrewingRecipe::isCooking,
                        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), CauldronBrewingRecipe::getInputItems,
                        ItemStack.STREAM_CODEC, CauldronBrewingRecipe::getResult,
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