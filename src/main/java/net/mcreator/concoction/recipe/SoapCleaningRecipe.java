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
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;              // ВАЖНО: правильный импорт
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
            ItemStack s = input.getItem(i);
            if (s.isEmpty()) continue;

            if (isSoap(s)) {
                soaps++;
                continue;
            }

            if (target.isEmpty()) {
                if (isSupportedTarget(s)) {
                    target = s;
                } else {
                    return false; // посторонний предмет
                }
            } else {
                return false; // больше одного целевого
            }
        }

        if (soaps != 1 || target.isEmpty()) return false;
        return canBeCleaned(target);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider lookup) {
        ItemStack target = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack s = input.getItem(i);
            if (!s.isEmpty() && !isSoap(s)) {
                target = s.copy();
                break;
            }
        }
        if (target.isEmpty()) return ItemStack.EMPTY;

        Item item = target.getItem();

        // A) Любой предмет с компонентом DYED_COLOR (кожа, волчья/конская броня и т.п.)
        if (target.get(DataComponents.DYED_COLOR) != null) {
            target.set(DataComponents.DYED_COLOR, null);
            return target;
        }

        // B) Щит — удаляем баннер/базовый цвет
        if (item instanceof ShieldItem) {
            if (target.get(DataComponents.BANNER_PATTERNS) != null ||
                    target.get(DataComponents.BASE_COLOR) != null) {
                target.set(DataComponents.BANNER_PATTERNS, null);
                target.set(DataComponents.BASE_COLOR, null);
                return target;
            }
        }

        // C) Цветной шалкер → обычный шалкер, с переносом инвентаря и всех компонентов
        if (item instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock sh) {
            if (sh.getColor() != null) {
                ItemStack cleaned = new ItemStack(Items.SHULKER_BOX);
                cleaned.applyComponents(target.getComponents()); // имя/чары и т.п.
                var bed = target.get(DataComponents.BLOCK_ENTITY_DATA);
                if (bed != null) cleaned.set(DataComponents.BLOCK_ENTITY_DATA, bed); // инвентарь
                cleaned.set(DataComponents.DYED_COLOR, null);
                cleaned.setCount(1);
                return cleaned;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w * h >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.SOAP_CLEANING.get();
    }

    // ===== helpers =====

    private boolean isSoap(ItemStack s) {
        return Objects.equals(BuiltInRegistries.ITEM.getKey(s.getItem()).toString(), "concoction:soap");
    }

    private boolean isSupportedTarget(ItemStack s) {
        Item i = s.getItem();
        if (s.get(DataComponents.DYED_COLOR) != null) return true;                 // всё крашенное
        if (i instanceof ShieldItem) return true;                                  // щит
        if (i instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock) return true; // шалкеры
        return false;
    }

    private boolean canBeCleaned(ItemStack s) {
        Item i = s.getItem();
        if (s.get(DataComponents.DYED_COLOR) != null) return true;
        if (i instanceof ShieldItem) {
            return s.get(DataComponents.BANNER_PATTERNS) != null ||
                    s.get(DataComponents.BASE_COLOR) != null;
        }
        if (i instanceof BlockItem bi && bi.getBlock() instanceof ShulkerBoxBlock sh) {
            return sh.getColor() != null; // только цветные
        }
        return false;
    }

    // ========== Serializer ==========
    public static class Serializer implements RecipeSerializer<SoapCleaningRecipe> {
        // В JSON храним только категорию (на будущее), сейчас используем "misc" по умолчанию
        private static final MapCodec<SoapCleaningRecipe> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC)
                                .forGetter(r -> CraftingBookCategory.MISC)
                ).apply(inst, SoapCleaningRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, SoapCleaningRecipe> STREAM_CODEC =
                StreamCodec.of(
                        (buf, recipe) -> { /* ничего не пишем */ },
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
