package net.mcreator.concoction.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum RecipeInteractionType {
    HAND("hand", ItemStack.EMPTY),
    BOTTLE("bottle", new ItemStack(Items.GLASS_BOTTLE)),
    BUCKET("bucket", new ItemStack(Items.BUCKET)),
    BOWL("bowl", new ItemStack(Items.BOWL)),
    STICK("stick", new ItemStack(Items.STICK)),
    UNKNOWN("", ItemStack.EMPTY);

    private final String serializedName;
    private final ItemStack catalystStack;

    RecipeInteractionType(String serializedName, ItemStack catalystStack) {
        this.serializedName = serializedName;
        this.catalystStack = catalystStack;
    }

    public static RecipeInteractionType fromSerializedName(String name) {
        for (RecipeInteractionType type : values()) {
            if (type.serializedName.equals(name)) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public String serializedName() {
        return this.serializedName;
    }

    public boolean requiresContainer() {
        return this != HAND && this != UNKNOWN;
    }

    public ItemStack catalystStack() {
        return this.catalystStack.copy();
    }

    public boolean matchesContainer(ItemStack stack) {
        if (this == HAND) {
            return true;
        }
        if (this == UNKNOWN || stack.isEmpty()) {
            return false;
        }

        return switch (this) {
            case BOTTLE -> stack.getItem() == Items.GLASS_BOTTLE;
            case BUCKET -> stack.getItem() == Items.BUCKET;
            case BOWL -> stack.getItem() == Items.BOWL;
            case STICK -> stack.getItem() == Items.STICK;
            case HAND, UNKNOWN -> false;
        };
    }
}
