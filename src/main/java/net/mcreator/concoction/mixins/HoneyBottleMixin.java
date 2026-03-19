package net.mcreator.concoction.mixins;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({net.minecraft.world.item.HoneyBottleItem.class})
public class HoneyBottleMixin {
    @Inject(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/stats/Stat;)V"))
    private void addEatEffect(ItemStack itemStack, Level p_41410_, LivingEntity player, CallbackInfoReturnable<ItemStack> cir) {
        player.addEffect(new MobEffectInstance(ConcoctionModMobEffects.SWEETNESS, 30*20, 2-1, false, false, true, null));

    }
}
