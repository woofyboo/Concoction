package net.mcreator.concoction.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ContainerRemainderHelper {
    private static final TagKey<Item> BUCKETS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "buckets"));
    private static final TagKey<Item> BOTTLES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "bottles"));
    private static final TagKey<Item> BOWLS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "bowls"));

    private ContainerRemainderHelper() {
    }

    public static ItemStack getRemainder(ItemStack stack, int count, boolean includeBowls) {
        if (stack.isEmpty() || count <= 0) {
            return ItemStack.EMPTY;
        }

        if (stack.is(BUCKETS)) {
            return new ItemStack(Items.BUCKET, count);
        }
        if (stack.is(BOTTLES)) {
            return new ItemStack(Items.GLASS_BOTTLE, count);
        }
        if (includeBowls && stack.is(BOWLS)) {
            return new ItemStack(Items.BOWL, count);
        }
        if (stack.hasCraftingRemainingItem()) {
            ItemStack remainder = stack.getCraftingRemainingItem();
            if (!remainder.isEmpty()) {
                ItemStack countedRemainder = remainder.copy();
                countedRemainder.setCount(count);
                return countedRemainder;
            }
        }

        return ItemStack.EMPTY;
    }
}
