package net.mcreator.concoction.mixins;

import net.mcreator.concoction.block.CropCornBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import oshi.util.tuples.Pair;

import java.util.Optional;

@Mixin({LightningBolt.class})
public abstract class LightningBoltMixin {
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LightningBolt;powerLightningRod()V"))
    private void chargeCornCrop(CallbackInfo ci) {
        LightningBolt lightningBolt = (LightningBolt) (Object) this;
        Level pLevel = lightningBolt.level();
        Vec3 vec3 = lightningBolt.position();
        BlockPos pPos = BlockPos.containing(vec3.x, vec3.y - 1.0E-6, vec3.z);
        Optional<Pair<BlockPos, BlockState>> cornPair = BlockPos.betweenClosedStream(pPos.offset(-2, 0, -2), pPos.offset(2, 5, 2)).map(pos -> new Pair<>(pos, pLevel.getBlockState(pos))).
                filter(p -> p.getB().getBlock() instanceof CropCornBlock && p.getB().getValue(CropCornBlock.PART).equals(CropCornBlock.PartProperty.TOP) && p.getB().getValue(CropCornBlock.AGE) == CropCornBlock.MAX_AGE &&
                        CropCornBlock.isReplaceableBlocksAround(pLevel, p.getA())).findFirst();
        cornPair.ifPresent(blockPosBlockStatePair -> CropCornBlock.onLightningStrike(pLevel, blockPosBlockStatePair.getA(), blockPosBlockStatePair.getB()));
    }
}