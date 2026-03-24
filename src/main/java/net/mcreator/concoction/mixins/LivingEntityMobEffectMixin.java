package net.mcreator.concoction.mixins;

import net.mcreator.concoction.handlers.FoodAftertasteHandler;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMobEffectMixin {
    @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
    private void concoction$blockBlindnessFromSporeSediment(MobEffectInstance effectInstance, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (!effectInstance.is(MobEffects.BLINDNESS)) {
            return;
        }

        if (!FoodAftertasteHandler.hasEnabledAftertaste(living, FoodPassiveEffectType.SPORE_SEDIMENT)) {
            return;
        }

        if (!living.level().isClientSide() && living.getRandom().nextFloat() < 0.5F) {
            FoodAftertasteHandler.disableActiveAftertaste(living, FoodPassiveEffectType.SPORE_SEDIMENT);
        }

        cir.setReturnValue(false);
    }
}
