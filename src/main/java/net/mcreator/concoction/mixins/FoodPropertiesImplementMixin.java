package net.mcreator.concoction.mixins;

import net.mcreator.concoction.item.food.types.FoodEffect;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({FoodProperties.class, FoodProperties.Builder.class})
@Implements(@Interface(iface = FoodEffect.class, prefix = "ident$"))
public abstract class FoodPropertiesImplementMixin {
    private FoodEffectType type = null;
    private int level = 1;
    private int duration = 30;

    public FoodEffectType ident$getType() {
        return this.type;
    }

    public int ident$getLevel() {
        return this.level;
    }

    public int ident$getDuration() {
        return this.duration;
    }

    public FoodProperties.Builder ident$setType(FoodProperties.Builder builder, FoodEffectType type) {
        this.type = type;
        return builder;
    }

}

