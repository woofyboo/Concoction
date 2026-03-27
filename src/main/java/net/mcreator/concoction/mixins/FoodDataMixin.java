package net.mcreator.concoction.mixins;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    private static final float HEALTH_EPSILON = 0.001F;

    @Shadow
    private int foodLevel;

    @Shadow
    private float saturationLevel;

    @Shadow
    private float exhaustionLevel;

    @Shadow
    private int tickTimer;

    @Shadow
    private int lastFoodLevel;

    @Shadow
    public abstract void addExhaustion(float exhaustion);

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void concoction$usePreciseHealthCheckForNaturalRegeneration(Player player, CallbackInfo ci) {
        Difficulty difficulty = player.level().getDifficulty();
        this.lastFoodLevel = this.foodLevel;
        if (this.exhaustionLevel > 4.0F) {
            this.exhaustionLevel -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        boolean naturalRegeneration = player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
        boolean naturalRegenerationBlocked = player.hasEffect(ConcoctionModMobEffects.WEEPING)
                || player.getHealth() >= player.getMaxHealth() - HEALTH_EPSILON;
        boolean actuallyHurt = !naturalRegenerationBlocked
                && player.getHealth() > 0.0F
                && player.getHealth() < player.getMaxHealth() - HEALTH_EPSILON;
        if (naturalRegeneration && this.saturationLevel > 0.0F && actuallyHurt && this.foodLevel >= 20) {
            this.tickTimer++;
            if (this.tickTimer >= 10) {
                float healing = Math.min(this.saturationLevel, 6.0F);
                player.heal(healing / 6.0F);
                this.addExhaustion(healing);
                this.tickTimer = 0;
            }
        } else if (naturalRegeneration && this.foodLevel >= 18 && actuallyHurt) {
            this.tickTimer++;
            if (this.tickTimer >= 80) {
                player.heal(1.0F);
                this.addExhaustion(6.0F);
                this.tickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            this.tickTimer++;
            if (this.tickTimer >= 80) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.hurt(player.damageSources().starve(), 1.0F);
                }

                this.tickTimer = 0;
            }
        } else {
            this.tickTimer = 0;
        }

        ci.cancel();
    }
}
