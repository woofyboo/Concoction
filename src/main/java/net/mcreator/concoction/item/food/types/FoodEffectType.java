package net.mcreator.concoction.item.food.types;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleTypes;


import net.mcreator.concoction.init.ConcoctionModParticles;

public enum FoodEffectType implements StringRepresentable {
    SWEET("sweet"),
    SPICY("spicy"),
    MINTY("minty"),
    GLOW("glow"),
    INSTABILITY("instability"),
    SALTY("saltness"),
    FLAMING("fiery_touch"),
    WARM("warming"),
    BITTER("bitterness"),
    BREAKFAST("breakfast"),
    HEAL("heal");

    private final String name;

    FoodEffectType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public static FoodEffectType getByName(String name) {
        return switch (name) {
            case "sweet" -> SWEET;
            case "spicy" -> SPICY;
            case "minty" -> MINTY;
            case "glow" -> GLOW;
            case "instability" -> INSTABILITY;
            case "saltness" -> SALTY;
            case "fiery_touch" -> FLAMING;
            case "warming" -> WARM;
            case "bitterness" -> BITTER;
            case "breakfast" -> BREAKFAST;
            case "heal" -> HEAL;
            default -> throw new IllegalArgumentException("Invalid name: " + name);
        };
    }

    /**
     * Возвращает MobEffectInstance для обычных эффектов.
     * Для HEAL возвращает null, чтобы не применялся через эффекты.
     */
    public static MobEffectInstance getEffect(FoodEffectType type, int level, int duration, boolean isHidden, LivingEntity entity) {
        switch (type) {
            case HEAL -> {
                return null; // мгновальное лечение обрабатывается отдельно
            }
            case SWEET -> {
                return new MobEffectInstance(ConcoctionModMobEffects.SWEETNESS, duration * 20, level - 1, false, !isHidden, true, null);
            }
            case SPICY -> {
                return new MobEffectInstance(ConcoctionModMobEffects.SPICY, duration * 20, level - 1, false, !isHidden, true, null);
            }
            case MINTY -> {
                return new MobEffectInstance(ConcoctionModMobEffects.MINTY_BREATH, duration * 20, level - 1, false, !isHidden, true, null);
            }
            case GLOW -> {
                return new MobEffectInstance(MobEffects.GLOWING, duration * 20, level - 1, false, !isHidden, true, null);
            }
            case INSTABILITY -> {
                return new MobEffectInstance(ConcoctionModMobEffects.INSTABILITY, duration * 20, level - 1, false, !isHidden, true, null);
            }
            case SALTY -> {
                return new MobEffectInstance(ConcoctionModMobEffects.SALTNESS, duration * 20, level - 1, false, !isHidden, true, null);
            }
            case FLAMING -> {
                return new MobEffectInstance(ConcoctionModMobEffects.FIERY_TOUCH, duration * 20, level - 1, false, !isHidden, true, null);
            }
            case WARM -> {
                return new MobEffectInstance(ConcoctionModMobEffects.WARMING, duration * 20, level - 1, false, !isHidden, true, null);
            }
            case BITTER -> {
                return new MobEffectInstance(ConcoctionModMobEffects.BITTERNESS, duration * 20, level - 1, false, !isHidden, true, null);
            }
            case BREAKFAST -> {
                return new MobEffectInstance(ConcoctionModMobEffects.BREAKFAST, duration * 20, level - 1, false, !isHidden, true, null);
            }
        }
        return null;
    }

    /**
     * Мгновенное действие для HEAL (лечит игрока)
     */
    public static void applyInstantEffect(FoodEffectType type, LivingEntity entity, int level) {
    if (type == HEAL && entity instanceof Player player) {
        player.heal(level * 2.0F);

        if (!player.level().isClientSide()) return;

    double x = player.getX();
    double y = player.getY() + player.getEyeHeight() / 2; // чуть ниже лица
    double z = player.getZ();

    int particleCount = 5; // меньше частиц
    double radiusX = 1.5;  // горизонтальный разброс
    double radiusY = 1.0;  // вертикальный разброс
    double radiusZ = 1.5;

    for (int i = 0; i < particleCount; i++) {
        double offsetX = (player.getRandom().nextDouble() - 0.5) * 2 * radiusX;
        double offsetY = (player.getRandom().nextDouble() - 0.5) * 2 * radiusY;
        double offsetZ = (player.getRandom().nextDouble() - 0.5) * 2 * radiusZ;

        player.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.HEART,
            x + offsetX,
            y + offsetY,
            z + offsetZ,
            0, 0.05, 0
        );
    }
    }
}

public Component getTooltip(int level, int duration, boolean isHidden) {
    MutableComponent effectName;

    if (this == HEAL) {
    // Основная часть названия вкуса — серая
    effectName = Component.translatable("taste.concoction.heal").withStyle(ChatFormatting.GRAY);

    // Текст " (x" и ")" — серые, сердечко — красное
    MutableComponent healInfo = Component.literal(" (x").withStyle(ChatFormatting.GRAY)
            .append(Component.literal(String.valueOf(level)).withStyle(ChatFormatting.GRAY)) // число тоже серое
            .append(Component.literal("❤").withStyle(ChatFormatting.RED)) // только сердечко красное
            .append(Component.literal(")").withStyle(ChatFormatting.GRAY));

    return effectName.append(healInfo);
}
 else {
        // Название эффекта
        effectName = Component.translatable("taste.concoction." + this.name)
                .withStyle(ChatFormatting.GRAY);

        // Уровень римскими цифрами, но если уровень = 1, оставляем пустое
        String romanLevel = (level > 1) ? toRoman(level) : "";

        // Длительность в формате m:ss (duration из секунд)
        int totalSeconds = duration; // duration уже в секундах
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String timeFormatted = String.format("%d:%02d", minutes, seconds);

        // Добавляем уровень и длительность к тултипу
        MutableComponent levelAndTime = Component.literal(
                (romanLevel.isEmpty() ? "" : " " + romanLevel) + " (" + timeFormatted + ")"
        ).withStyle(ChatFormatting.GRAY);

        return effectName.append(levelAndTime);
    }
}

/**
 * Преобразует число в римскую цифру (1 -> I, 2 -> II, ...)
 */
private static String toRoman(int number) {
    String[] romans = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
    if (number >= 1 && number <= 10) return romans[number - 1];
    return String.valueOf(number); // на случай числа больше 10
}


}
