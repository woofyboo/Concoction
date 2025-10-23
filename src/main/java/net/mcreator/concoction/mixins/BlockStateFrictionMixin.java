// src/main/java/net/mcreator/concoction/mixins/EntityGroundPosMixin.java
package net.mcreator.concoction.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.mcreator.concoction.block.SoapLayerBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class BlockStateFrictionMixin {

    @Inject(
        method = "getBlockPosBelowThatAffectsMyMovement()Lnet/minecraft/core/BlockPos;",
        at = @At("HEAD"),
        cancellable = true
    )
    private void concoction$preferSoapLayerWhenInside(CallbackInfoReturnable<BlockPos> cir) {
        Entity self = (Entity)(Object)this;
        Level level = (Level) self.level();

        // Позиция по координате Y, округлённой вниз — обычно это как раз блок мыла-«слоя», если мы стоим на тонком слое
        BlockPos here = self.blockPosition();
        BlockState stateHere = level.getBlockState(here);

        // 1) Простой случай: моб стоит прямо в блоке мыльного слоя
        if (stateHere.getBlock() instanceof SoapLayerBlock) {
            cir.setReturnValue(here);
            return;
        }

        // 2) Граничный случай: хитбокс задевает слой, который висит «над» нижним блоком (на всякий случай)
        BlockPos above = here.above();
        BlockState stateAbove = level.getBlockState(above);
        if (stateAbove.getBlock() instanceof SoapLayerBlock) {
            // Проверим, что нижняя грань хитбокса реально внутри «тонкого пирога» мыла (0..1 пиксель от низа блока)
            double feetY = self.getBoundingBox().minY;
            double layerMinY = above.getY();       // низ блока мыла
            double layerMaxY = layerMinY + 1.0/16; // твоя форма: высота 1/16
            if (feetY >= layerMinY && feetY <= layerMaxY + 1.0e-6) {
                cir.setReturnValue(above);
            }
        }
    }
}
