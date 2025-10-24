// src/main/java/net/mcreator/concoction/mixins/ItemEntitySoapFrictionMixin.java
package net.mcreator.concoction.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.mcreator.concoction.block.SoapLayerBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemEntity.class)
public abstract class ItemEntitySoapFrictionMixin {

    /**
     * В ItemEntity#tick локальная переменная f хранит коэффициент трения:
     *   f = 0.98F;
     *   if (onGround) f = blockBelow.getFriction(...) * 0.98F;
     * Мы перехватываем это значение и повышаем его, если над нижним блоком есть мыльный слой.
     */
    @ModifyVariable(
        method = "tick",
        at = @At(value = "STORE"), // первое сохранение float f
        ordinal = 0,
        name = "f"
    )
    private float concoction$soapAboveMakesItemsSlide(float original) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (!self.onGround()) return original;

        Level level = (Level) self.level();

        // Блок «почвы» под предметом
        BlockPos below = BlockPos.containing(self.getX(), self.getY() - 0.2D, self.getZ());
        // Мыльный слой висит НАД почвой
        BlockPos above = below.above();
        BlockState aboveState = level.getBlockState(above);

        if (aboveState.getBlock() instanceof SoapLayerBlock) {
            // У предметов f уже включает *0.98F*, потому подставляем "мыльную" фрикцию 0.98F так же,
            // т.е. f = 0.98F (мыло) * 0.98F (item damping)
            float soapItemF = 1F;

            // Берём максимум, чтобы не ухудшать лёд/синий лёд, если вдруг они ещё скользче вашей цели
            return Math.max(original, soapItemF);
        }
        return original;
    }
}
