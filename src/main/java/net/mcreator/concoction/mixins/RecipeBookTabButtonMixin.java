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

import java.util.List;

@Mixin(RecipeBookTabButton.class)
public abstract class RecipeBookTabButtonMixin {
    @Shadow public abstract RecipeBookCategories getCategory();

    @Inject(method = "renderIcon", at = @At("HEAD"), cancellable = true)
    private void concoction$renderOvenCategoryIcons(GuiGraphics guiGraphics, ItemRenderer itemRenderer, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || !(minecraft.player.containerMenu instanceof OvenMenu)) {
            return;
        }

        List<ItemStack> icons = OvenRecipeBookClientCategories.getIcons(this.getCategory());
        if (icons.isEmpty()) {
            return;
        }

        AbstractWidget widget = (AbstractWidget) (Object) this;
        if (icons.size() == 1) {
            guiGraphics.renderItem(icons.get(0), widget.getX() + 9, widget.getY() + 5);
        } else {
            guiGraphics.renderItem(icons.get(0), widget.getX() + 3, widget.getY() + 5);
            guiGraphics.renderItem(icons.get(1), widget.getX() + 14, widget.getY() + 5);
        }
        ci.cancel();
    }
}
