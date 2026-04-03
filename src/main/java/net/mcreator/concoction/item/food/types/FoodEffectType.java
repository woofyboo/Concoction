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

public enum FoodEffectType implements StringRepresentable {
    SPICY("spicy"),
    MINTY("minty"),
    GLOW("glow"),
    INSTABILITY("instability"),
    BITTER("bitterness"),
    LIGHT_BITTERNESS("light_bitterness"),
    TART_BITTERNESS("tart_bitterness"),
    BITTERISH("bitterish"),
    HEAL("heal"),
    WEEPING("weeping");

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
            case "spicy" -> SPICY;
            case "minty" -> MINTY;
            case "glow" -> GLOW;
            case "instability" -> INSTABILITY;
            case "bitterness" -> BITTER;
            case "light_bitterness" -> LIGHT_BITTERNESS;
            case "tart_bitterness" -> TART_BITTERNESS;
            case "bitterish" -> BITTERISH;
            case "heal" -> HEAL;
            case "weeping" -> WEEPING;
            default -> throw new IllegalArgumentException("Invalid name: " + name);
        };
    }

    public static MobEffectInstance getEffect(FoodEffectType type, int level, int duration, boolean isHidden, LivingEntity entity) {
        return switch (type) {
            case HEAL -> null;
            case SPICY -> new MobEffectInstance(ConcoctionModMobEffects.SPICY, duration * 20, level - 1, false, !isHidden, true, null);
            case MINTY -> new MobEffectInstance(ConcoctionModMobEffects.MINTY_BREATH, duration * 20, level - 1, false, !isHidden, true, null);
            case GLOW -> new MobEffectInstance(MobEffects.GLOWING, duration * 20, level - 1, false, !isHidden, true, null);
            case INSTABILITY -> new MobEffectInstance(ConcoctionModMobEffects.INSTABILITY, duration * 20, level - 1, false, !isHidden, true, null);
            case BITTER -> new MobEffectInstance(ConcoctionModMobEffects.BITTERNESS, duration * 20, level - 1, false, !isHidden, true, null);
            case LIGHT_BITTERNESS, TART_BITTERNESS, BITTERISH -> new MobEffectInstance(ConcoctionModMobEffects.BITTERNESS, duration * 20, level - 1, false, false, true, null);
            case WEEPING -> new MobEffectInstance(ConcoctionModMobEffects.WEEPING, duration * 20, level - 1, false, !isHidden, true, null);
        };
    }

    public static void applyInstantEffect(FoodEffectType type, LivingEntity entity, int level) {
        if (type == HEAL && entity instanceof Player player) {
            player.heal(level * 2.0F);

            if (!player.level().isClientSide()) {
                return;
            }

            double x = player.getX();
            double y = player.getY() + player.getEyeHeight() / 2;
            double z = player.getZ();

            int particleCount = 5;
            double radiusX = 1.5;
            double radiusY = 1.0;
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
                        0,
                        0.05,
                        0
                );
            }
        }
    }

    public Component getTooltip(int level, int duration, boolean isHidden) {
        MutableComponent effectName;

        if (this == HEAL) {
            effectName = Component.translatable("taste.concoction.heal").withStyle(ChatFormatting.GRAY);

            MutableComponent healInfo = Component.literal(" (x").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.valueOf(level)).withStyle(ChatFormatting.GRAY))
                    .append(Component.literal("❤").withStyle(ChatFormatting.RED))
                    .append(Component.literal(")").withStyle(ChatFormatting.GRAY));

            return effectName.append(healInfo);
        } else {
            effectName = Component.translatable("taste.concoction." + this.name)
                    .withStyle(ChatFormatting.GRAY);

            String romanLevel = (level > 1) ? toRoman(level) : "";
            int totalSeconds = duration;
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            String timeFormatted = String.format("%d:%02d", minutes, seconds);

            MutableComponent levelAndTime = Component.literal(
                    (romanLevel.isEmpty() ? "" : " " + romanLevel) + " (" + timeFormatted + ")"
            ).withStyle(ChatFormatting.GRAY);

            return effectName.append(levelAndTime);
        }
    }

    public Component getTooltipTitle() {
        return Component.translatable("food_effect.concoction." + this.name)
                .withStyle(ChatFormatting.YELLOW);
    }

    public Component getTooltipDescription(boolean detailed) {
        String suffix = detailed ? ".desc" : ".simple_desc";
        return Component.translatable("food_effect.concoction." + this.name + suffix)
                .withStyle(ChatFormatting.GRAY);
    }

    private static String toRoman(int number) {
        String[] romans = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        if (number >= 1 && number <= 10) {
            return romans[number - 1];
        }
        return String.valueOf(number);
    }
}
