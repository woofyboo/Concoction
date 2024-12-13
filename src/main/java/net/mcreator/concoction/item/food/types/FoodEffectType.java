package net.mcreator.concoction.item.food.types;

import net.minecraft.util.StringRepresentable;

public enum FoodEffectType implements StringRepresentable {
    SWEET("sweet"),
    SPICY("spicy"),
    MINTY("minty");

    private final String name;
    private FoodEffectType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
