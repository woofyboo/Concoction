package net.mcreator.concoction.mixins;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.Gui.class)
public class GuiMixin {
    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void renderFood(GuiGraphics guiGraphics, Player player, int x, int y, CallbackInfo ci) {
        if (Utils.isPhotosynthesisActive(player)) {
            ci.cancel();
        }
    }

    @Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
    private void renderHearts(GuiGraphics guiGraphics, Player player, int x, int y,
                              int height, int regen, float maxHealth, int health,
                              int displayHealth, int absorption, boolean blinking, CallbackInfo ci) {
        if (player.hasEffect(ConcoctionModMobEffects.SPICY)
                || player.hasEffect(ConcoctionModMobEffects.SUNSTRUCK_EFFECT)
                || player.hasEffect(ConcoctionModMobEffects.WEEPING)) {
            // These effects fully replace vanilla heart rendering with custom overlays.
            ci.cancel();
        }
    }
}
