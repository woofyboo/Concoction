package net.mcreator.concoction.mixins;

import net.minecraft.world.food.FoodProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin({FoodProperties.class})
public abstract class FoodPropertiesInitMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void addFoodEffectFields(int nutrition, float saturation, boolean canAlwaysEat, float eatSeconds, Optional usingConvertsTo, List effects, CallbackInfo ci) {
        
    }
}
