package net.mcreator.concoction.mixins;

import net.mcreator.concoction.event.ConcoctionFoodEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.item.MilkBucketItem.class)
public class MilkBucketMixin {
    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void concoction$captureState(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        ConcoctionFoodEvents.captureForcedConsumeSnapshot(entity);
    }

    @Inject(method = "finishUsingItem", at = @At("RETURN"))
    private void concoction$restoreState(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        ConcoctionFoodEvents.restoreForcedConsumeSnapshot(entity, stack);
    }
}
