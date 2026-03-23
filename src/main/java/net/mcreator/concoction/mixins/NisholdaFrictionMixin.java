package net.mcreator.concoction.mixins;

import net.mcreator.concoction.handlers.NisholdaSlipHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class NisholdaFrictionMixin {
    private static final float NORMAL_SURFACE_FRICTION = 0.6F;

    @Inject(
            method = "getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F",
            at = @At("RETURN"),
            cancellable = true
    )
    private void concoction$normalizeSlipForNisholda(LevelReader level, BlockPos pos, Entity entity, CallbackInfoReturnable<Float> cir) {
        if (!(entity instanceof Player player) || !NisholdaSlipHandler.hasSlipNormalization(player)) {
            return;
        }

        float friction = cir.getReturnValueF();
        if (friction > NORMAL_SURFACE_FRICTION) {
            cir.setReturnValue(NORMAL_SURFACE_FRICTION);
        }
    }
}
