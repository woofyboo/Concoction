package net.mcreator.concoction.mixins;

import net.mcreator.concoction.custom.CustomAttackSunflowerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Zombie.class})
public class ZombieMixin {
    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void registerGoals(CallbackInfo ci) {
        Zombie zombie = (Zombie) (Object) this;
        zombie.goalSelector.addGoal(5, new CustomAttackSunflowerGoal(zombie));
    }

}
