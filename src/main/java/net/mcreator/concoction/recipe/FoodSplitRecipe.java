package net.mcreator.concoction.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;

public class FoodSplitRecipe extends CustomRecipe {
    private final String group;
    private final CraftingBookCategory category;
    private final Ingredient source;
    private final RecipeOutputData result;
    private final ItemStack resultStack;

    public FoodSplitRecipe(String group, CraftingBookCategory category, Ingredient source, RecipeOutputData result) {
        super(category);
        this.group = group;
        this.category = category;
        this.source = source;
        this.result = result;
        this.resultStack = result.toStack();
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return findSourceSlot(input) >= 0;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider lookup) {
        int sourceSlot = findSourceSlot(input);
        if (sourceSlot < 0) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = input.getItem(sourceSlot);
        ItemStack crafted = this.resultStack.copy();

        List<?> passiveEffects = sourceStack.get(ConcoctionModDataComponents.FOOD_PASSIVE_EFFECTS.get());
        if (passiveEffects != null && !passiveEffects.isEmpty()) {
            crafted.set(ConcoctionModDataComponents.FOOD_PASSIVE_EFFECTS.get(), sourceStack.get(ConcoctionModDataComponents.FOOD_PASSIVE_EFFECTS.get()));
        }

        return crafted;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remainingItems = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        int sourceSlot = findSourceSlot(input);
        if (sourceSlot < 0) {
            return remainingItems;
        }

        ItemStack sourceStack = input.getItem(sourceSlot);
        ItemStack remainder = ContainerRemainderHelper.getRemainder(sourceStack, 1, true);
        if (!remainder.isEmpty()) {
            remainingItems.set(sourceSlot, remainder);
        }

        return remainingItems;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.FOOD_SPLIT.get();
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookup) {
        return this.resultStack.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, this.source);
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    public Ingredient getSource() {
        return this.source;
    }

    public RecipeOutputData getConfiguredResult() {
        return this.result;
    }

    private int findSourceSlot(CraftingInput input) {
        int sourceSlot = -1;

        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }

            if (!this.source.test(stack)) {
                return -1;
            }

            if (sourceSlot >= 0) {
                return -1;
            }

            sourceSlot = slot;
        }

        return sourceSlot;
    }

    public static class Serializer implements RecipeSerializer<FoodSplitRecipe> {
        private static final MapCodec<FoodSplitRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(FoodSplitRecipe::getGroup),
                CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(recipe -> recipe.category),
                Ingredient.CODEC.fieldOf("source").forGetter(FoodSplitRecipe::getSource),
                RecipeOutputData.CODEC.fieldOf("result").forGetter(FoodSplitRecipe::getConfiguredResult)
        ).apply(instance, FoodSplitRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, FoodSplitRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                FoodSplitRecipe::getGroup,
                ByteBufCodecs.STRING_UTF8.map(Serializer::readCategory, CraftingBookCategory::getSerializedName),
                recipe -> recipe.category,
                Ingredient.CONTENTS_STREAM_CODEC,
                FoodSplitRecipe::getSource,
                RecipeOutputData.STREAM_CODEC,
                FoodSplitRecipe::getConfiguredResult,
                FoodSplitRecipe::new
        );

        private static CraftingBookCategory readCategory(String name) {
            for (CraftingBookCategory category : CraftingBookCategory.values()) {
                if (category.getSerializedName().equals(name)) {
                    return category;
                }
            }
            return CraftingBookCategory.MISC;
        }

        @Override
        public MapCodec<FoodSplitRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FoodSplitRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
