package net.mcreator.concoction.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ShulkerBoxBlock;

import java.util.Objects;

public class SoapCleaningRecipe extends CustomRecipe {
    private final CraftingBookCategory category;

    public SoapCleaningRecipe(CraftingBookCategory category) {
        super(category);
        this.category = category;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int soaps = 0;
        ItemStack target = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            if (isSoap(stack)) {
                soaps++;
                continue;
            }

            if (target.isEmpty()) {
                if (isSupportedTarget(stack)) {
                    target = stack;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        return soaps == 1 && !target.isEmpty() && canBeCleaned(target);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider lookup) {
        ItemStack target = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && !isSoap(stack)) {
                target = stack.copy();
                break;
            }
        }

        if (target.isEmpty()) {
            return ItemStack.EMPTY;
        }

        Item item = target.getItem();

        if (target.get(DataComponents.DYED_COLOR) != null) {
            target.set(DataComponents.DYED_COLOR, null);
            return target;
        }

        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock shulker && shulker.getColor() != null) {
            ItemStack cleaned = new ItemStack(Items.SHULKER_BOX);
            cleaned.applyComponents(target.getComponents());
            var blockEntityData = target.get(DataComponents.BLOCK_ENTITY_DATA);
            if (blockEntityData != null) {
                cleaned.set(DataComponents.BLOCK_ENTITY_DATA, blockEntityData);
            }
            cleaned.set(DataComponents.DYED_COLOR, null);
            cleaned.setCount(1);
            return cleaned;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SOAP_CLEANING.get();
    }

    private boolean isSoap(ItemStack stack) {
        return Objects.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), "concoction:soap");
    }

    private boolean isSupportedTarget(ItemStack stack) {
        Item item = stack.getItem();
        if (stack.get(DataComponents.DYED_COLOR) != null) {
            return true;
        }
        return item instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
    }

    private boolean canBeCleaned(ItemStack stack) {
        Item item = stack.getItem();
        if (stack.get(DataComponents.DYED_COLOR) != null) {
            return true;
        }
        return item instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof ShulkerBoxBlock shulker
                && shulker.getColor() != null;
    }

    public static class Serializer implements RecipeSerializer<SoapCleaningRecipe> {
        private static final MapCodec<SoapCleaningRecipe> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC)
                                .forGetter(recipe -> recipe.category)
                ).apply(inst, SoapCleaningRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, SoapCleaningRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> {
                        },
                        buf -> new SoapCleaningRecipe(CraftingBookCategory.MISC)
                );

        @Override
        public MapCodec<SoapCleaningRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SoapCleaningRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
