package net.mcreator.concoction.item.food.types;

import net.minecraft.world.food.FoodProperties;

public interface FoodEffect {
    public abstract FoodEffectType getType();
    public abstract int getLevel();
    public abstract int getDuration();
    public abstract FoodProperties.Builder setType(FoodProperties.Builder builder, FoodEffectType type);
    public abstract FoodProperties.Builder setLevel(FoodProperties.Builder builder, int level);
    public abstract FoodProperties.Builder setDuration(FoodProperties.Builder builder, int duration);
}

