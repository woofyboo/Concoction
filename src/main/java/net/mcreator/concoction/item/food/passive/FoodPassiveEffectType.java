package net.mcreator.concoction.item.food.passive;

import net.mcreator.concoction.handlers.FoodAftertasteHandler;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;

import java.util.ArrayList;
import java.util.List;

public enum FoodPassiveEffectType implements StringRepresentable {
    SPICE_INFUSED_MEAT("spice_infused_meat", false),
    HOT_BROTH("hot_broth", false),
    SCALDING_HOT("scalding_hot", false),
    SPICINESS("spiciness", false),
    CONCENTRATED_SPICINESS("concentrated_spiciness", false),
    HONEY_BENEFIT("honey_benefit", false),
    ROTTEN_MEAT("rotten_meat", false),
    TETRODOTOXIN("tetrodotoxin", false),
    SPRING_MOOD("spring_mood", false),
    CHERRY_JUICE("cherry_juice", false),
    NAUSEATINGLY_VILE("nauseatingly_vile", false),
    LIGHT_SNACK("light_snack", false),
    SPIDER_VENOM("spider_venom", false),
    SALMONELLOSIS("salmonellosis", false),
    BLIGHTED_POTATO("blighted_potato", false),
    OTHERWORLDLY_MALADAPTATION("otherworldly_maladaptation", false),
    GILDED_RESTORATION("gilded_restoration", false),
    GILDED_RESTORATION_PLUS("gilded_restoration_plus", false),
    GOOD_MORNING("good_morning", true),
    CRISPY_CRUST("crispy_crust", true, true),
    SUGAR_CRYSTALLIZATION("sugar_crystallization", true),
    SPORE_SEDIMENT("spore_sediment", true),
    JITTERING_JELLY("jittering_jelly", true, true),
    STICKY_VISCOSITY("sticky_viscosity", true, true),
    GENTLE_CLEANSING("gentle_cleansing", false),
    GENTLE_CLEANSING_PLUS("gentle_cleansing_plus", false);

    private static final int SPICE_INFUSED_MEAT_DURATION_SECONDS = 16;
    private static final int SPICE_INFUSED_MEAT_LEVEL = 1;
    private static final int SPICINESS_DURATION_SECONDS = 12;
    private static final int SPICINESS_LEVEL = 1;
    private static final int CONCENTRATED_SPICINESS_DURATION_SECONDS = 24;
    private static final int CONCENTRATED_SPICINESS_LEVEL = 2;
    private static final int SPRING_MOOD_DURATION_SECONDS = 8;
    private static final int SPRING_MOOD_LEVEL = 2;
    private static final float REGULAR_HEAL_AMOUNT = 2.0F;
    private static final float LOW_HEALTH_HEAL_AMOUNT = 10.0F;
    private static final float LOW_HEALTH_THRESHOLD = 0.5F;
    private static final float CHERRY_JUICE_BASE_HEAL = 1.0F;
    private static final float CHERRY_JUICE_MAX_HEAL = 4.0F;

    private final String name;
    private final boolean aftertaste;
    private final boolean renewableAftertaste;

    FoodPassiveEffectType(String name, boolean aftertaste) {
        this(name, aftertaste, false);
    }

    FoodPassiveEffectType(String name, boolean aftertaste, boolean renewableAftertaste) {
        this.name = name;
        this.aftertaste = aftertaste;
        this.renewableAftertaste = renewableAftertaste;
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
        applyOnConsume(entity, ItemStack.EMPTY);
    }

    public void applyOnConsume(LivingEntity entity, ItemStack consumedStack) {
        switch (this) {
            case SPICE_INFUSED_MEAT -> applySpiceInfusedMeat(entity);
            case HOT_BROTH -> applyHotBroth(entity);
            case SCALDING_HOT -> applyScaldingHot(entity);
            case SPICINESS -> applySpiciness(entity);
            case CONCENTRATED_SPICINESS -> applyConcentratedSpiciness(entity);
            case HONEY_BENEFIT -> applyHoneyBenefit(entity);
            case ROTTEN_MEAT -> applyRottenMeat(entity);
            case TETRODOTOXIN -> applyTetrodotoxin(entity);
            case SPRING_MOOD -> applySpringMood(entity);
            case CHERRY_JUICE -> applyCherryJuice(entity);
            case NAUSEATINGLY_VILE -> applyNauseatinglyVile(entity, consumedStack);
            case LIGHT_SNACK -> applyLightSnack(entity);
            case SPIDER_VENOM -> applySpiderVenom(entity);
            case SALMONELLOSIS -> applySalmonellosis(entity);
            case BLIGHTED_POTATO -> applyBlightedPotato(entity);
            case OTHERWORLDLY_MALADAPTATION -> applyOtherworldlyMaladaptation(entity, consumedStack);
            case GILDED_RESTORATION -> applyGildedRestoration(entity);
            case GILDED_RESTORATION_PLUS -> applyGildedRestorationPlus(entity);
            case GENTLE_CLEANSING -> applyGentleCleansing(entity);
            case GENTLE_CLEANSING_PLUS -> applyGentleCleansingPlus(entity);
            case CRISPY_CRUST, SUGAR_CRYSTALLIZATION, SPORE_SEDIMENT, JITTERING_JELLY, STICKY_VISCOSITY, GOOD_MORNING -> {
            }
        }
    }

    public boolean isAftertaste() {
        return this.aftertaste;
    }

    public boolean canBeReactivated() {
        return this.renewableAftertaste;
    }

    public Component getTooltipTitle() {
        return getTooltipTitle(ChatFormatting.YELLOW);
    }

    public Component getTooltipTitle(ChatFormatting color) {
        MutableComponent title = Component.translatable("food_passive_effect.concoction." + this.name)
                .withStyle(color);
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

        applyHealing(entity, healAmount);
        entity.addEffect(new MobEffectInstance(
                ConcoctionModMobEffects.SPICY,
                SPICE_INFUSED_MEAT_DURATION_SECONDS * 20,
                SPICE_INFUSED_MEAT_LEVEL - 1,
                false,
                true,
                true
        ));
    }

    private static void applyHotBroth(LivingEntity entity) {
        entity.setTicksFrozen(0);
    }

    private static void applyScaldingHot(LivingEntity entity) {
        entity.hurt(entity.damageSources().onFire(), 1.0F);
        entity.igniteForSeconds(3.0F);
    }

    private static void applySpiciness(LivingEntity entity) {
        applySpicyEffect(entity, SPICINESS_DURATION_SECONDS, SPICINESS_LEVEL);
    }

    private static void applyConcentratedSpiciness(LivingEntity entity) {
        applySpicyEffect(entity, CONCENTRATED_SPICINESS_DURATION_SECONDS, CONCENTRATED_SPICINESS_LEVEL);
    }

    private static void applyHoneyBenefit(LivingEntity entity) {
        entity.removeEffect(MobEffects.POISON);
    }

    private static void applyRottenMeat(LivingEntity entity) {
        if (entity.getRandom().nextFloat() >= 0.8F) {
            return;
        }

        entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 30 * 20, 0, false, true, true));
    }

    private static void applyTetrodotoxin(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * 20, 3, false, true, true));
        entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 15 * 20, 2, false, true, true));
        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 15 * 20, 0, false, true, true));
    }

    private static void applySpringMood(LivingEntity entity) {
        if (!entity.level().getBiome(entity.blockPosition()).is(Biomes.CHERRY_GROVE)) {
            return;
        }

        entity.addEffect(new MobEffectInstance(
                MobEffects.REGENERATION,
                SPRING_MOOD_DURATION_SECONDS * 20,
                SPRING_MOOD_LEVEL - 1,
                false,
                true,
                true
        ));
    }

    private static void applyCherryJuice(LivingEntity entity) {
        int matchingFoodsInHistory = FoodAftertasteHandler.countRecentFoodsWithPassiveEffect(entity, CHERRY_JUICE);
        int previousMatchingFoods = Math.max(0, matchingFoodsInHistory - 1);
        float healAmount = CHERRY_JUICE_BASE_HEAL * (float) Math.pow(2.0D, Math.min(previousMatchingFoods, 2));
        applyHealingWithHearts(entity, Math.min(healAmount, CHERRY_JUICE_MAX_HEAL));
    }

    private static void applyNauseatinglyVile(LivingEntity entity, ItemStack consumedStack) {
        if (consumedStack.isEmpty()) {
            applyNauseatinglyVileNegativeEffects(entity);
            return;
        }

        int matchingFoodsInHistory = FoodAftertasteHandler.countRecentFoodsMatchingStack(entity, consumedStack);
        int previousMatchingFoods = Math.max(0, matchingFoodsInHistory - 1);
        float activationChance = 1.0F - previousMatchingFoods * 0.30F;

        if (activationChance > 0.0F && entity.getRandom().nextFloat() < activationChance) {
            applyNauseatinglyVileNegativeEffects(entity);
            return;
        }

        if (activationChance >= 0.0F) {
            return;
        }

        boolean onlyNetherSlopRecently = FoodAftertasteHandler.hasOnlyConsumedMatchingFoodRecently(entity, consumedStack);
        if (entity.level().dimension() == Level.NETHER) {
            applyHealing(entity, onlyNetherSlopRecently ? 6.0F : 2.0F);
        } else if (entity.level().dimension() == Level.OVERWORLD && onlyNetherSlopRecently) {
            applyHealing(entity, 2.0F);
        }
    }

    private static void applyLightSnack(LivingEntity entity) {
        FoodAftertasteHandler.reactivateExhaustedAftertastes(entity);
    }

    private static void applySpiderVenom(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.POISON, 4 * 20, 0, false, true, true));
    }

    private static void applySalmonellosis(LivingEntity entity) {
        if (entity.getRandom().nextFloat() >= 0.30F) {
            return;
        }

        entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 30 * 20, 0, false, true, true));
    }

    private static void applyBlightedPotato(LivingEntity entity) {
        if (entity.getRandom().nextFloat() >= 0.60F) {
            return;
        }

        entity.addEffect(new MobEffectInstance(MobEffects.POISON, 4 * 20, 0, false, true, true));
    }

    private static void applyOtherworldlyMaladaptation(LivingEntity entity, ItemStack consumedStack) {
        if (consumedStack.is(Items.CHORUS_FRUIT)) {
            return;
        }

        ChorusTeleportHelper.tryTeleportLikeChorusFruit(entity);
    }

    private static void applyGildedRestoration(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 120 * 20, 0, false, true, true));
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 5 * 20, 1, false, true, true));
    }

    private static void applyGildedRestorationPlus(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 120 * 20, 3, false, true, true));
        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 20, 1, false, true, true));
        entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300 * 20, 0, false, true, true));
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300 * 20, 0, false, true, true));
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

    private static void applySpicyEffect(LivingEntity entity, int durationSeconds, int level) {
        entity.addEffect(new MobEffectInstance(
                ConcoctionModMobEffects.SPICY,
                durationSeconds * 20,
                level - 1,
                false,
                true,
                true
        ));
    }

    private static void applyHealing(LivingEntity entity, float amount) {
        if (amount <= 0.0F) {
            return;
        }

        entity.heal(amount);
    }

    private static void applyHealingWithHearts(LivingEntity entity, float amount) {
        if (amount <= 0.0F) {
            return;
        }

        applyHealing(entity, amount);
        if (entity.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.HEART,
                    entity.getX(),
                    entity.getY() + entity.getBbHeight() * 0.45D,
                    entity.getZ(),
                    Math.max(1, Math.round(amount)),
                    0.25D,
                    0.2D,
                    0.25D,
                    0.0D
            );
        }
    }

    private static void applyNauseatinglyVileNegativeEffects(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 12 * 20, 0, false, true, true));
        entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 16 * 20, 1, false, true, true));
    }
}
