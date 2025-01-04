package net.mcreator.concoction.mixins;

import net.mcreator.concoction.block.CropCornBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.security.auth.callback.Callback;

@Mixin({LightningBolt.class})
public abstract class LightningBoltMixin {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LightningBolt;powerLightningRod()V"))
    private void chargeCornCrop(CallbackInfo ci) {
        LightningBolt lightningBolt = (LightningBolt) (Object) this;
        Vec3 vec3 = lightningBolt.position();
        BlockPos blockpos = BlockPos.containing(vec3.x, vec3.y - 1.0E-6, vec3.z);
        BlockState blockstate = lightningBolt.level().getBlockState(blockpos);
        if (blockstate.getBlock() instanceof CropCornBlock)
            CropCornBlock.onLightningStrike(lightningBolt.level(), blockpos, blockstate);

    }
}