package net.mcreator.concoction.item.food.passive;

import net.mcreator.concoction.handlers.FoodAftertasteHandler;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public enum FoodPassiveEffectType implements StringRepresentable {
    SPICE_INFUSED_MEAT("spice_infused_meat", false),
    HOT_BROTH("hot_broth", false),
    CRISPY_CRUST("crispy_crust", true),
    JITTERING_JELLY("jittering_jelly", true),
    STICKY_VISCOSITY("sticky_viscosity", true),
    GENTLE_CLEANSING("gentle_cleansing", false),
    GENTLE_CLEANSING_PLUS("gentle_cleansing_plus", false);

    private static final int SPICE_DURATION_SECONDS = 30;
    private static final int SPICE_LEVEL = 1;
    private static final float REGULAR_HEAL_AMOUNT = 2.0F;
    private static final float LOW_HEALTH_HEAL_AMOUNT = 10.0F;
    private static final float LOW_HEALTH_THRESHOLD = 0.5F;

    private final String name;
    private final boolean aftertaste;

    FoodPassiveEffectType(String name, boolean aftertaste) {
        this.name = name;
        this.aftertaste = aftertaste;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public static FoodPassiveEffectType getByName(String name) {
        for (FoodPassiveEffectType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid passive food effect name: " + name);
    }

    public void applyOnConsume(LivingEntity entity) {
        switch (this) {
            case SPICE_INFUSED_MEAT -> applySpiceInfusedMeat(entity);
            case HOT_BROTH -> applyHotBroth(entity);
            case GENTLE_CLEANSING -> applyGentleCleansing(entity);
            case GENTLE_CLEANSING_PLUS -> applyGentleCleansingPlus(entity);
            case CRISPY_CRUST, JITTERING_JELLY, STICKY_VISCOSITY -> {
            }
        }
    }

    public boolean isAftertaste() {
        return this.aftertaste;
    }

    public Component getTooltipTitle() {
        MutableComponent title = Component.translatable("food_passive_effect.concoction." + this.name)
                .withStyle(ChatFormatting.YELLOW);
        if (!this.aftertaste) {
            return title;
        }

        return Component.empty()
                .append(title)
                .append(Component.translatable("tooltip.concoction.aftertaste_suffix").withStyle(ChatFormatting.DARK_GRAY));
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

    private static void applyGentleCleansing(LivingEntity entity) {
        removeRandomMobEffect(entity);
        FoodAftertasteHandler.removeOldestAftertasteFoodOrOldestFood(entity);
    }

    private static void applyGentleCleansingPlus(LivingEntity entity) {
        removeAllMobEffects(entity);
        FoodAftertasteHandler.clearFoodHistory(entity);
    }

    private static void removeRandomMobEffect(LivingEntity entity) {
        List<MobEffectInstance> activeEffects = new ArrayList<>(entity.getActiveEffects());
        if (activeEffects.isEmpty()) {
            return;
        }

        MobEffectInstance removedEffect = activeEffects.get(entity.getRandom().nextInt(activeEffects.size()));
        entity.removeEffect(removedEffect.getEffect());
    }

    private static void removeAllMobEffects(LivingEntity entity) {
        List<MobEffectInstance> activeEffects = new ArrayList<>(entity.getActiveEffects());
        for (MobEffectInstance activeEffect : activeEffects) {
            entity.removeEffect(activeEffect.getEffect());
        }
    }
}
