package net.mcreator.concoction.mixins;

import net.mcreator.concoction.handlers.FoodAftertasteHandler;
import net.mcreator.concoction.handlers.WeepingStateHandler;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMobEffectMixin {
    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void concoction$suspendIncomingEffectsDuringWeeping(
            MobEffectInstance effectInstance,
            net.minecraft.world.entity.Entity source,
            CallbackInfoReturnable<Boolean> cir
    ) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (WeepingStateHandler.suspendIncomingEffect(living, effectInstance)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "forceAddEffect", at = @At("HEAD"), cancellable = true)
    private void concoction$suspendForcedEffectsDuringWeeping(
            MobEffectInstance effectInstance,
            net.minecraft.world.entity.Entity source,
            CallbackInfo ci
    ) {
        LivingEntity living = (LivingEntity) (Object) this;
        if (WeepingStateHandler.suspendIncomingEffect(living, effectInstance)) {
            ci.cancel();
        }
    }

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

    @Inject(method = "onEffectAdded", at = @At("TAIL"))
    private void concoction$initializeWeepingState(MobEffectInstance effectInstance, net.minecraft.world.entity.Entity source, CallbackInfo ci) {
        if (effectInstance.is(ConcoctionModMobEffects.WEEPING)) {
            WeepingStateHandler.onWeepingAdded((LivingEntity) (Object) this);
        }
    }

    @Inject(method = "onEffectRemoved", at = @At("TAIL"))
    private void concoction$restoreWeepingState(MobEffectInstance effectInstance, CallbackInfo ci) {
        if (effectInstance.is(ConcoctionModMobEffects.WEEPING)) {
            WeepingStateHandler.onWeepingRemoved((LivingEntity) (Object) this);
        }
    }
}
