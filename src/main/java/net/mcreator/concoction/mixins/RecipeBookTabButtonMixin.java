package net.mcreator.concoction.mixins;

import net.mcreator.concoction.recipebook.OvenRecipeBookClientCategories;
import net.mcreator.concoction.world.inventory.OvenMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeBookTabButton.class)
public abstract class RecipeBookTabButtonMixin {
    @Shadow public abstract RecipeBookCategories getCategory();

    @Inject(method = "renderIcon", at = @At("HEAD"), cancellable = true)
    private void concoction$renderOvenCategoryIcons(GuiGraphics guiGraphics, ItemRenderer itemRenderer, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || !(minecraft.player.containerMenu instanceof OvenMenu)) {
            return;
        }

        ItemStack icon = OvenRecipeBookClientCategories.getIcon(this.getCategory());
        if (icon.isEmpty()) {
            return;
        }

        AbstractWidget widget = (AbstractWidget) (Object) this;
        guiGraphics.renderItem(icon, widget.getX() + 9, widget.getY() + 5);
        ci.cancel();
    }
}
