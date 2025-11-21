package net.mcreator.concoction.mixins;

import net.mcreator.concoction.interfaces.ICowMilkLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.AgeableMob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AgeableMob.class)
public abstract class AgeableMobMilkMixin {

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void concoction$saveMilk(CompoundTag tag, CallbackInfo ci) {
        // Сюда попадут все мобы, наследующие AgeableMob (в т.ч. коровы и козы),
        // но нас интересуют только те, что реализуют ICowMilkLevel.
        if ((Object) this instanceof ICowMilkLevel milk) {
            tag.putInt("ConcoctionMilkLevel", milk.concoction$getMilkLevel());
            tag.putLong("ConcoctionLastMilked", milk.concoction$getLastMilkedTime());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void concoction$loadMilk(CompoundTag tag, CallbackInfo ci) {
        if ((Object) this instanceof ICowMilkLevel milk) {
            if (tag.contains("ConcoctionMilkLevel", Tag.TAG_INT)) {
                milk.concoction$setMilkLevel(tag.getInt("ConcoctionMilkLevel"));
            }
            if (tag.contains("ConcoctionLastMilked", Tag.TAG_LONG)) {
                milk.concoction$setLastMilkedTime(tag.getLong("ConcoctionLastMilked"));
            }
        }
    }
}
