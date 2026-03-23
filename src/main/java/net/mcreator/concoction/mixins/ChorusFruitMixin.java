package net.mcreator.concoction.mixins;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.world.item.ChorusFruitItem.class)
public abstract class ChorusFruitMixin extends Item {
    protected ChorusFruitMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "finishUsingItem", at = @At("HEAD"), cancellable = true)
    private void concoction$disableChorusTeleport(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        cir.setReturnValue(super.finishUsingItem(stack, level, entity));
    }
}
