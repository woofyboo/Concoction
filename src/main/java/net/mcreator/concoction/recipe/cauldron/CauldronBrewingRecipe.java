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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CauldronBrewingRecipe implements Recipe<CauldronBrewingRecipeInput> {
    private final BlockState inputState;
    private final List<Ingredient> inputItems;
    private final Map<String, String> result;

    public BlockState getInputState() {
        return inputState;
    }

    public List<Ingredient> getInputItems() {
        return inputItems;
    }

    public Map<String, String> getResult() {
        return result;
    }

    public CauldronBrewingRecipe(BlockState inputState, List<Ingredient> inputItems, Map<String, String> result) {
        this.inputState = inputState;
        this.inputItems = inputItems;
        this.result = result;
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

    public static boolean containsAllElements(NonNullList<ItemStack> list1, List<Ingredient> list2) {
        // Подсчитываем количество каждого объекта в первом списке
        Map<Item, Integer> Inventory = new HashMap<>();
//        list2.get(0).test()
        for (ItemStack item : list1) {
            if (item.getItem() != Items.AIR)
                Inventory.put(item.getItem(), Inventory.getOrDefault(item.getItem(), 0) + 1);
        }

        // Подсчитываем количество каждого объекта во втором списке
        Map<Item, Integer> CraftItems = new HashMap<>();
        for (Ingredient ingr : list2) {
            if (ingr.getItems().length > 1) {
                if (Arrays.stream(ingr.getItems()).anyMatch(item -> {
                            if (Inventory.getOrDefault(item.getItem(), 0) > 0) {
                                CraftItems.put(item.getItem(), CraftItems.getOrDefault(item.getItem(), 0) + 1);
                                return true;
                            } else return false;
                        }
                ));
                else return false;
            } else {
                CraftItems.put(ingr.getItems()[0].getItem(), CraftItems.getOrDefault(ingr.getItems()[0].getItem(), 0) + 1);
            }
        }

        // Проверяем, что каждый объект из второго списка содержится в первом в нужном количестве
        if (Inventory.size() != CraftItems.size()) return false; // Если количество различается
        for (Map.Entry<Item, Integer> entry : CraftItems.entrySet()) {
            Item key = entry.getKey();
            int requiredCount = entry.getValue();
            int availableCount = Inventory.getOrDefault(key, 0);

            if (availableCount != requiredCount) return false; // Если объектов недостаточно

        }
        return true; // Все объекты содержатся в нужном количестве
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
    public static class Serializer implements RecipeSerializer<CauldronBrewingRecipe> {
        public static final MapCodec<CauldronBrewingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                BlockState.CODEC.fieldOf("state").forGetter(CauldronBrewingRecipe::getInputState),
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("ingredients").forGetter(CauldronBrewingRecipe::getInputItems),
                Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("result").forGetter(CauldronBrewingRecipe::getResult)
        ).apply(inst, CauldronBrewingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CauldronBrewingRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), CauldronBrewingRecipe::getInputState,
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