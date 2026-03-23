package net.mcreator.concoction.item.food.passive;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public enum FoodPassiveEffectType implements StringRepresentable {
    SPICE_INFUSED_MEAT("spice_infused_meat"),
    HOT_BROTH("hot_broth");

    private static final int SPICE_DURATION_SECONDS = 30;
    private static final int SPICE_LEVEL = 1;
    private static final float REGULAR_HEAL_AMOUNT = 2.0F;
    private static final float LOW_HEALTH_HEAL_AMOUNT = 10.0F;
    private static final float LOW_HEALTH_THRESHOLD = 0.5F;

    private final String name;

    FoodPassiveEffectType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public static FoodPassiveEffectType getByName(String name) {
        return switch (name) {
            case "spice_infused_meat" -> SPICE_INFUSED_MEAT;
            case "hot_broth" -> HOT_BROTH;
            default -> throw new IllegalArgumentException("Invalid passive food effect name: " + name);
        };
    }

    public void applyOnConsume(LivingEntity entity) {
        switch (this) {
            case SPICE_INFUSED_MEAT -> applySpiceInfusedMeat(entity);
            case HOT_BROTH -> applyHotBroth(entity);
        }
    }

    public Component getTooltipTitle() {
        return Component.translatable("food_passive_effect.concoction." + this.name)
                .withStyle(ChatFormatting.YELLOW);
    }

    public Component getTooltipDescription(boolean detailed) {
        String suffix = detailed ? ".desc" : ".simple_desc";
        return Component.translatable("food_passive_effect.concoction." + this.name + suffix)
                .withStyle(ChatFormatting.GRAY);
    }

    private static void applySpiceInfusedMeat(LivingEntity entity) {
        float healAmount = entity.getHealth() <= entity.getMaxHealth() * LOW_HEALTH_THRESHOLD
                ? LOW_HEALTH_HEAL_AMOUNT
                : REGULAR_HEAL_AMOUNT;

        entity.heal(healAmount);
        entity.addEffect(new MobEffectInstance(
                ConcoctionModMobEffects.SPICY,
                SPICE_DURATION_SECONDS * 20,
                SPICE_LEVEL - 1,
                false,
                true,
                true
        ));
    }

    private static void applyHotBroth(LivingEntity entity) {
        entity.setTicksFrozen(0);
    }
}
