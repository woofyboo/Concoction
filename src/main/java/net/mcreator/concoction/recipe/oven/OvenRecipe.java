package net.mcreator.concoction.recipe.oven;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModRecipes;
import net.mcreator.concoction.recipe.RecipeIngredientMatcher;
import net.mcreator.concoction.recipe.RecipeOutputData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public class OvenRecipe implements Recipe<OvenRecipeInput> {
    private final String group;
    private final OvenRecipeBookCategory category;
    private final int cookingTime;
    private final List<Ingredient> craftingIngredients;
    private final Ingredient bottleIngredient;
    private final Ingredient bowlIngredient;
    private final RecipeOutputData result;
    private final ItemStack resultStack;

    public OvenRecipe(String group,
                      OvenRecipeBookCategory category,
                      int cookingTime,
                      List<Ingredient> craftingIngredients,
                      Ingredient bottleIngredient,
                      Ingredient bowlIngredient,
                      RecipeOutputData result) {
        this.group = group;
        this.cookingTime = cookingTime;
        this.craftingIngredients = craftingIngredients;
        this.bottleIngredient = bottleIngredient;
        this.bowlIngredient = bowlIngredient;
        this.result = result;
        this.category = OvenRecipeBookCategory.resolve(category, bottleIngredient, bowlIngredient, result);
        this.resultStack = result.toStack();
    }

    public OvenRecipeBookCategory getCategory() {
        return this.category;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public List<Ingredient> getCraftingIngredients() {
        return craftingIngredients;
    }

    public Ingredient getBottleIngredient() {
        return bottleIngredient;
    }

    public Ingredient getBowlIngredient() {
        return bowlIngredient;
    }

    public RecipeOutputData getResult() {
        return result;
    }

    public int getOutputCount() {
        return result.isEmpty() ? 1 : result.count();
    }

    public int getBowlCost() {
        return bowlIngredient.isEmpty() ? 0 : getOutputCount();
    }

    private boolean matchesIngredients(NonNullList<ItemStack> inventory) {
        NonNullList<ItemStack> craftingSlots = NonNullList.withSize(6, ItemStack.EMPTY);
        for (int i = 1; i <= 6; i++) {
            craftingSlots.set(i - 1, inventory.get(i));
        }

        ItemStack bottleSlot = inventory.get(0);
        ItemStack bowlSlot = inventory.get(7);

        if (!RecipeIngredientMatcher.matchesExactly(craftingIngredients, craftingSlots)) {
            return false;
        }

        if (!bottleIngredient.isEmpty() && !bottleIngredient.test(bottleSlot)) {
            return false;
        }
        if (bottleIngredient.isEmpty() && !bottleSlot.isEmpty()) {
            return false;
        }

        if (!bowlIngredient.isEmpty() && !bowlIngredient.test(bowlSlot)) {
            return false;
        }
        if (bowlIngredient.isEmpty() && !bowlSlot.isEmpty()) {
            return false;
        }

        if (!bowlIngredient.isEmpty() && bowlSlot.getCount() < getOutputCount()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean matches(OvenRecipeInput input, Level level) {
        if (level.isClientSide()) {
            return false;
        }
        return matchesIngredients(input.items());
    }

    @Override
    public ItemStack assemble(OvenRecipeInput input, HolderLookup.Provider registries) {
        return resultStack.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.craftingIngredients.size();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return resultStack.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.addAll(this.craftingIngredients);

        if (!this.bottleIngredient.isEmpty()) {
            list.add(this.bottleIngredient);
        }

        if (!this.bowlIngredient.isEmpty()) {
            list.add(this.bowlIngredient);
        }

        return list;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ConcoctionModItems.OVEN.get());
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ConcoctionModRecipes.OVEN_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ConcoctionModRecipes.OVEN_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<OvenRecipe> {
        private static final StreamCodec<RegistryFriendlyByteBuf, List<Ingredient>> INGREDIENT_LIST_STREAM_CODEC =
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list());

        public static final MapCodec<OvenRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(OvenRecipe::getGroup),
                OvenRecipeBookCategory.CODEC.optionalFieldOf("category", OvenRecipeBookCategory.UNKNOWN).forGetter(OvenRecipe::getCategory),
                Codec.INT.fieldOf("cooking_time").orElse(200).forGetter(OvenRecipe::getCookingTime),
                Ingredient.LIST_CODEC_NONEMPTY.fieldOf("crafting_ingredients").forGetter(OvenRecipe::getCraftingIngredients),
                Ingredient.CODEC.optionalFieldOf("bottle_ingredient", Ingredient.EMPTY).forGetter(OvenRecipe::getBottleIngredient),
                Ingredient.CODEC.optionalFieldOf("bowl_ingredient", Ingredient.EMPTY).forGetter(OvenRecipe::getBowlIngredient),
                RecipeOutputData.CODEC.fieldOf("result").forGetter(OvenRecipe::getResult)
        ).apply(inst, OvenRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, OvenRecipe> STREAM_CODEC =
                StreamCodec.of(
                        Serializer::writeToNetwork,
                        Serializer::readFromNetwork
                );

        @Override
        public MapCodec<OvenRecipe> codec() {
            return CODEC;
        }

        private static OvenRecipeBookCategory readCategory(String name) {
            return OvenRecipeBookCategory.fromSerializedName(name);
        }

        private static void writeToNetwork(RegistryFriendlyByteBuf buf, OvenRecipe recipe) {
            ByteBufCodecs.STRING_UTF8.encode(buf, recipe.getGroup());
            ByteBufCodecs.STRING_UTF8.encode(buf, recipe.getCategory().getSerializedName());
            ByteBufCodecs.INT.encode(buf, recipe.getCookingTime());
            INGREDIENT_LIST_STREAM_CODEC.encode(buf, recipe.getCraftingIngredients());
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getBottleIngredient());
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.getBowlIngredient());
            RecipeOutputData.STREAM_CODEC.encode(buf, recipe.getResult());
        }

        private static OvenRecipe readFromNetwork(RegistryFriendlyByteBuf buf) {
            return new OvenRecipe(
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    readCategory(ByteBufCodecs.STRING_UTF8.decode(buf)),
                    ByteBufCodecs.INT.decode(buf),
                    INGREDIENT_LIST_STREAM_CODEC.decode(buf),
                    Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                    Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                    RecipeOutputData.STREAM_CODEC.decode(buf)
            );
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, OvenRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
