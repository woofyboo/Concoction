package net.mcreator.concoction.mixins;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Optional;

@Mixin({FoodProperties.Builder.class})
public abstract class FoodPropertiesBuilderMixin {
    @Shadow
    private int nutrition;
    @Shadow
    private boolean canAlwaysEat;
    @Shadow
    private float eatSeconds;
    @Shadow
    private Optional<ItemStack> usingConvertsTo;
    @Final
    @Shadow
    private ImmutableList.Builder<FoodProperties.PossibleEffect> effects;

    @Inject(method = "build", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void build(CallbackInfoReturnable<FoodProperties> cir, float f) {
        cir.setReturnValue(new FoodProperties(this.nutrition, f, this.canAlwaysEat, this.eatSeconds, this.usingConvertsTo, this.effects.build()));
        cir.cancel();
    }
}

