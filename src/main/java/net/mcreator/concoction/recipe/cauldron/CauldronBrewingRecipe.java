package net.mcreator.concoction.recipe.cauldron;


import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mcreator.concoction.ConcoctionMod;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.spongepowered.include.com.google.gson.JsonObject;

public class CauldronBrewingRecipe implements Recipe<RecipeInput> {
    private final NonNullList<Ingredient> ingredients;
    private final ItemStack result;
    private final String group;
    private final CraftingBookCategory category;
    private final ResourceLocation id;

    public CauldronBrewingRecipe(String group, CraftingBookCategory craftBookCategory, ItemStack result,
                                 NonNullList<Ingredient> ingredients, ResourceLocation id) {
        this.ingredients = ingredients;
        this.result = result;
        this.group = group;
        this.category = craftBookCategory;
        this.id = id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPELESS_RECIPE;
    }// TODO

    @Override
    public String getGroup() {
        return this.group;
    }

    public CraftingBookCategory category() {
        return this.category;
    }

    @Override
    public boolean matches(RecipeInput pInput, Level pLevel) {
        if (pLevel.isClientSide()) return false;

        return ingredients.get(0).test(pInput.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeInput p_345149_, HolderLookup.Provider p_346030_) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_336125_) {
        return result.copy();
    }

    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }

    public static class Type implements RecipeType<CauldronBrewingRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "cauldron_brewing";

        @Override
        public String toString() {
            return "cauldron_brewing";
        }
    }

    public static class Serializer implements RecipeSerializer<CauldronBrewingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("cauldron_brewing");
        private static final MapCodec<CauldronBrewingRecipe> CODEC = RecordCodecBuilder.mapCodec(
            p_340779_ -> p_340779_.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(p_301127_ -> p_301127_.group),
                CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(p_301133_ -> p_301133_.category),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(p_301142_ -> p_301142_.result),
                Ingredient.CODEC_NONEMPTY
                        .listOf()
                        .fieldOf("ingredients")
                        .flatXmap(
                                p_301021_ -> {
                                    Ingredient[] aingredient = p_301021_.toArray(Ingredient[]::new); // Neo skip the empty check and immediately create the array.
                                    if (aingredient.length == 0) {
                                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                                    } else {
                                        return aingredient.length > ShapedRecipePattern.maxHeight * ShapedRecipePattern.maxWidth
                                                ? DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: %s".formatted(ShapedRecipePattern.maxHeight * ShapedRecipePattern.maxWidth))
                                                : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                                    }
                                },
                                DataResult::success
                        )
                        .forGetter(p_300975_ -> p_300975_.ingredients)
                    )
                    .apply(p_340779_, ShapelessRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CauldronBrewingRecipe> STREAM_CODEC = StreamCodec.of(
                CauldronBrewingRecipe.Serializer::toNetwork, CauldronBrewingRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<CauldronBrewingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CauldronBrewingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static CauldronBrewingRecipe fromNetwork(RegistryFriendlyByteBuf p_319905_) {
            String s = p_319905_.readUtf();
            CraftingBookCategory craftingbookcategory = p_319905_.readEnum(CraftingBookCategory.class);
            int i = p_319905_.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
            nonnulllist.replaceAll(p_319735_ -> Ingredient.CONTENTS_STREAM_CODEC.decode(p_319905_));
            ItemStack itemstack = ItemStack.STREAM_CODEC.decode(p_319905_);
            return new CauldronBrewingRecipe(s, craftingbookcategory, itemstack, nonnulllist);
        }

        private static void toNetwork(RegistryFriendlyByteBuf p_320371_, CauldronBrewingRecipe p_320323_) {
            p_320371_.writeUtf(p_320323_.group);
            p_320371_.writeEnum(p_320323_.category);
            p_320371_.writeVarInt(p_320323_.ingredients.size());

            for (Ingredient ingredient : p_320323_.ingredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(p_320371_, ingredient);
            }

            ItemStack.STREAM_CODEC.encode(p_320371_, p_320323_.result);
        }
        }
    }
}