package net.mcreator.concoction.mixins;

import net.mcreator.concoction.init.ConcoctionModGameRules;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class, priority = 900)
public abstract class PlayerCanEatMixin {

    // Player#canEat(boolean ignoreHunger) : boolean
    @Inject(method = "canEat(Z)Z", at = @At("HEAD"), cancellable = true)
    private void concoction$alwaysEatWhenRule(boolean ignoreHunger, CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player) (Object) this;

        // ВАЖНО: только на сервере!
        if (self.level() != null && !self.level().isClientSide &&
            self.level().getGameRules().getBoolean(ConcoctionModGameRules.CAN_ALWAYS_EAT)) {
            cir.setReturnValue(true);
        }
    }
}
