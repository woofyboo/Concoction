package net.mcreator.concoction.item.food.types;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.init.ConcoctionModPotions;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public enum FoodEffectType implements StringRepresentable {
    SWEET("sweet"),
    SPICY("spicy"),
    MINTY("minty"),
    GLOW("glow"),
    INSTABILITY("instability");

    private final String name;
    private FoodEffectType(String name) {
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
            default -> throw new IllegalArgumentException("Invalid name: " + name);
        };
    }

    public static MobEffectInstance getEffect(FoodEffectType type, int level, int duration) {
        return switch (type) {
            case SWEET -> new MobEffectInstance(ConcoctionModMobEffects.SWEETNESS, duration*20, level-1);
            case SPICY -> new MobEffectInstance(ConcoctionModMobEffects.SPICY, duration*20, level-1);
            case MINTY -> new MobEffectInstance(ConcoctionModMobEffects.MINTY_BREATH, duration*20, level-1);
            case GLOW ->  new MobEffectInstance(MobEffects.GLOWING, duration*20, level-1);
            case INSTABILITY -> new MobEffectInstance(ConcoctionModMobEffects.INSTABILITY, duration*20, level-1);
        };
    }
}
