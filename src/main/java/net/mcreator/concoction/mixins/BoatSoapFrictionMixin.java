// src/main/java/net/mcreator/concoction/mixins/BoatSoapFrictionMixin.java
package net.mcreator.concoction.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.mcreator.concoction.block.SoapLayerBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Boat.class)
public abstract class BoatSoapFrictionMixin {

    /**
     * Boat#getGroundFriction() возвращает коэффициент трения суши для лодки.
     * Мы повышаем его, если НАД блоком-«почвой» лежит тонкий мыльный слой.
     */
    @Inject(
        method = "getGroundFriction()F",
        at = @At("RETURN"),
        cancellable = true
    )
    private void concoction$soapMakesBoatsSlide(CallbackInfoReturnable<Float> cir) {
        Boat self = (Boat) (Object) this;
        Level level = self.level();

        // Блок под лодкой
        BlockPos below = BlockPos.containing(self.getX(), self.getY() - 0.2D, self.getZ());
        // Мыльный слой висит над «почвой»
        BlockPos above = below.above();
        BlockState aboveState = level.getBlockState(above);

        if (aboveState.getBlock() instanceof SoapLayerBlock) {
            // Желаемая «ледяная» фрикция для суши под лодкой
            float desired = 0.98F;
            float current = cir.getReturnValueF();
            // Не понижаем более скользкие поверхности, только повышаем до нужного
            if (current < desired) {
                cir.setReturnValue(desired);
            }
        }
    }
}
