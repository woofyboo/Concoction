package net.mcreator.concoction.mixins;

import net.mcreator.concoction.block.entity.CinnamonHangingSignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class LocalPlayerHangingSignMixin {

	@Shadow
	@Final
	protected Minecraft minecraft;

	@Inject(method = "openTextEdit", at = @At("HEAD"), cancellable = true)
	private void concoction$openCinnamonHangingSignEdit(SignBlockEntity signBlockEntity, boolean frontText, CallbackInfo ci) {
		if (signBlockEntity instanceof CinnamonHangingSignBlockEntity cinnamonHangingSignBlockEntity) {
			this.minecraft.setScreen(new HangingSignEditScreen(cinnamonHangingSignBlockEntity, frontText, this.minecraft.isTextFilteringEnabled()));
			ci.cancel();
		}
	}
}
