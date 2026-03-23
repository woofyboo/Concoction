package net.mcreator.concoction.mixins;

import net.mcreator.concoction.handlers.FoodAftertasteHandler;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityJumpMixin {
    @Shadow
    protected abstract float getJumpPower();

    @Redirect(
            method = "jumpFromGround",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getJumpPower()F")
    )
    private float concoction$reduceStickyJumpHeight(LivingEntity ignored) {
        LivingEntity living = (LivingEntity) (Object) this;
        float jumpPower = this.getJumpPower();
        if (!FoodAftertasteHandler.hasEnabledAftertaste(living, FoodPassiveEffectType.STICKY_VISCOSITY)) {
            return jumpPower;
        }

        return jumpPower * 0.9F;
    }
}
