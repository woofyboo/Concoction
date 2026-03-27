package net.mcreator.concoction.mixins;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityEffectAccess {
    @Accessor("effectsDirty")
    void concoction$setEffectsDirty(boolean effectsDirty);

    @Invoker("refreshDirtyAttributes")
    void concoction$invokeRefreshDirtyAttributes();
}
