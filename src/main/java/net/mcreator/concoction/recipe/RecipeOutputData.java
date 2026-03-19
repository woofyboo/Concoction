package net.mcreator.concoction.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record RecipeOutputData(@Nullable ResourceLocation itemId, int count, String interactionType) {
    private static final String ID_KEY = "id";
    private static final String COUNT_KEY = "count";
    private static final String INTERACTION_TYPE_KEY = "interactionType";

    private static final Codec<Integer> FLEXIBLE_COUNT_CODEC = Codec.either(Codec.INT, Codec.STRING).comapFlatMap(
            either -> either.map(DataResult::success, RecipeOutputData::parseCount),
            Either::left
    );

    public static final MapCodec<RecipeOutputData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf(ID_KEY).forGetter(output -> Optional.ofNullable(output.itemId())),
            FLEXIBLE_COUNT_CODEC.optionalFieldOf(COUNT_KEY, 1).forGetter(RecipeOutputData::count),
            Codec.STRING.optionalFieldOf(INTERACTION_TYPE_KEY, "").forGetter(RecipeOutputData::interactionType)
    ).apply(instance, (itemId, count, interactionType) -> new RecipeOutputData(itemId.orElse(null), count, interactionType)));

    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeOutputData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, RecipeOutputData::itemIdString,
            ByteBufCodecs.INT, RecipeOutputData::count,
            ByteBufCodecs.STRING_UTF8, RecipeOutputData::interactionType,
            RecipeOutputData::fromNetwork
    );

    public static final RecipeOutputData EMPTY = new RecipeOutputData(null, 0, "");

    public RecipeOutputData {
        if (itemId == null) {
            count = 0;
            interactionType = "";
        } else {
            count = Math.max(0, count);
            interactionType = interactionType == null ? "" : interactionType;
            if (count == 0) {
                itemId = null;
                interactionType = "";
            }
        }
    }

    public static RecipeOutputData of(ResourceLocation itemId, int count, String interactionType) {
        return new RecipeOutputData(itemId, count, interactionType);
    }

    public static RecipeOutputData fromTag(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) {
            return EMPTY;
        }

        String itemId = tag.getString(ID_KEY);
        int count = tag.contains(COUNT_KEY, Tag.TAG_INT) ? tag.getInt(COUNT_KEY) : parseCountOrZero(tag.getString(COUNT_KEY));
        String interactionType = tag.getString(INTERACTION_TYPE_KEY);
        return fromComponents(itemId, count, interactionType);
    }

    public static RecipeOutputData fromContainerTag(CompoundTag parentTag, String key) {
        if (parentTag.contains(key, Tag.TAG_COMPOUND)) {
            return fromTag(parentTag.getCompound(key));
        }

        // Backward compatibility for worlds saved before craft results were moved off JSON strings.
        if (parentTag.contains(key, Tag.TAG_STRING)) {
            return fromLegacyJson(parentTag.getString(key));
        }

        return EMPTY;
    }

    public void saveToContainerTag(CompoundTag parentTag, String key) {
        parentTag.remove(key);
        if (!isEmpty()) {
            parentTag.put(key, toTag());
        }
    }

    public boolean isEmpty() {
        return itemId == null || count <= 0;
    }

    public String itemIdString() {
        return itemId == null ? "" : itemId.toString();
    }

    public Item item() {
        if (itemId == null || !BuiltInRegistries.ITEM.containsKey(itemId)) {
            return Items.AIR;
        }
        return BuiltInRegistries.ITEM.get(itemId);
    }

    public ItemStack toStack() {
        Item item = item();
        return isEmpty() || item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item, count);
    }

    public boolean canMergeInto(ItemStack currentStack) {
        ItemStack outputStack = toStack();
        if (outputStack.isEmpty()) {
            return false;
        }

        if (currentStack.isEmpty()) {
            return true;
        }

        return ItemStack.isSameItemSameComponents(outputStack, currentStack)
                && currentStack.getCount() + count <= currentStack.getMaxStackSize();
    }

    public ItemStack mergeInto(ItemStack currentStack) {
        ItemStack outputStack = toStack();
        if (outputStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (currentStack.isEmpty()) {
            return outputStack;
        }

        ItemStack mergedStack = currentStack.copy();
        if (ItemStack.isSameItemSameComponents(outputStack, mergedStack)) {
            int toAdd = Math.min(count, mergedStack.getMaxStackSize() - mergedStack.getCount());
            if (toAdd > 0) {
                mergedStack.grow(toAdd);
            }
        }

        return mergedStack;
    }

    public ItemStack toSingleStack() {
        Item item = item();
        return isEmpty() || item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }

    public RecipeOutputData decrement() {
        return count <= 1 ? EMPTY : new RecipeOutputData(itemId, count - 1, interactionType);
    }

    public RecipeInteractionType interaction() {
        return RecipeInteractionType.fromSerializedName(this.interactionType);
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        if (itemId != null) {
            tag.putString(ID_KEY, itemId.toString());
            tag.putInt(COUNT_KEY, count);
        }
        if (!interactionType.isEmpty()) {
            tag.putString(INTERACTION_TYPE_KEY, interactionType);
        }
        return tag;
    }

    private static RecipeOutputData fromNetwork(String itemId, int count, String interactionType) {
        return fromComponents(itemId, count, interactionType);
    }

    private static RecipeOutputData fromComponents(String itemId, int count, String interactionType) {
        if (itemId == null || itemId.isBlank()) {
            return EMPTY;
        }

        ResourceLocation resourceLocation = ResourceLocation.tryParse(itemId);
        if (resourceLocation == null) {
            return EMPTY;
        }

        return new RecipeOutputData(resourceLocation, count, interactionType);
    }

    private static RecipeOutputData fromLegacyJson(String json) {
        if (json == null || json.isBlank()) {
            return EMPTY;
        }

        try {
            JsonObject object = JsonParser.parseString(json).getAsJsonObject();
            String itemId = object.has(ID_KEY) ? object.get(ID_KEY).getAsString() : "";
            String interactionType = object.has(INTERACTION_TYPE_KEY) ? object.get(INTERACTION_TYPE_KEY).getAsString() : "";
            int count = object.has(COUNT_KEY) ? parseCountOrZero(object.get(COUNT_KEY).getAsString()) : 0;
            return fromComponents(itemId, count, interactionType);
        } catch (RuntimeException ignored) {
            return EMPTY;
        }
    }

    private static DataResult<Integer> parseCount(String count) {
        try {
            int parsed = Integer.parseInt(count.trim());
            if (parsed < 0) {
                return DataResult.error(() -> "Count must be non-negative: " + count);
            }
            return DataResult.success(parsed);
        } catch (NumberFormatException exception) {
            return DataResult.error(() -> "Invalid count: " + count);
        }
    }

    private static int parseCountOrZero(String count) {
        return parseCount(count).result().orElse(0);
    }
}
