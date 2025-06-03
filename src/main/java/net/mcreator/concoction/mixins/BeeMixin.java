package net.mcreator.concoction.mixins;

import net.mcreator.concoction.custom.BeeFollowPlayerGoal;
import net.mcreator.concoction.custom.CustomAttackSunflowerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Bee.class})
public class BeeMixin {
    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        Bee bee = (Bee) (Object) this;
        bee.goalSelector.addGoal(5, new BeeFollowPlayerGoal(bee));
    }
}

