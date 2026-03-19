package net.mcreator.concoction.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class SoapShieldCleaningRecipe extends CustomRecipe {
    private static final String GROUP = "soap_cleaning_shield";

    public SoapShieldCleaningRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int soaps = 0;
        ItemStack shield = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (stack.is(ConcoctionModItems.SOAP.get())) {
                soaps++;
                continue;
            }

            if (shield.isEmpty() && isCleanableShield(stack)) {
                shield = stack;
                continue;
            }

            return false;
        }

        return soaps == 1 && !shield.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider lookup) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (isCleanableShield(stack)) {
                ItemStack cleaned = stack.copy();
                cleaned.set(DataComponents.BANNER_PATTERNS, null);
                cleaned.set(DataComponents.BASE_COLOR, null);
                return cleaned;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SOAP_SHIELD_CLEANING.get();
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public String getGroup() {
        return GROUP;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        ItemStack displayShield = new ItemStack(Items.SHIELD);
        displayShield.set(DataComponents.BASE_COLOR, DyeColor.WHITE);

        return NonNullList.of(
                Ingredient.EMPTY,
                Ingredient.of(ConcoctionModItems.SOAP.get()),
                Ingredient.of(displayShield)
        );
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookup) {
        return new ItemStack(Items.SHIELD);
    }

    private static boolean isCleanableShield(ItemStack stack) {
        return stack.is(Items.SHIELD)
                && (stack.get(DataComponents.BANNER_PATTERNS) != null
                || stack.get(DataComponents.BASE_COLOR) != null);
    }

    public static class Serializer implements RecipeSerializer<SoapShieldCleaningRecipe> {
        private static final MapCodec<SoapShieldCleaningRecipe> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC)
                                .forGetter(recipe -> recipe.category())
                ).apply(inst, SoapShieldCleaningRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, SoapShieldCleaningRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> {
                        },
                        buf -> new SoapShieldCleaningRecipe(CraftingBookCategory.MISC)
                );

        @Override
        public MapCodec<SoapShieldCleaningRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SoapShieldCleaningRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
