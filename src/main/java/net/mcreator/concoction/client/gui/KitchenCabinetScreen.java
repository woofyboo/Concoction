package net.mcreator.concoction.client.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.GuiGraphics;

import com.mojang.blaze3d.systems.RenderSystem;

import net.mcreator.concoction.world.inventory.KitchenCabinetMenu;

public class KitchenCabinetScreen extends AbstractContainerScreen<KitchenCabinetMenu> {
    private static final ResourceLocation texture = ResourceLocation.parse("concoction:textures/gui/hud/kitchen_cabinet_gui.png");

    public KitchenCabinetScreen(KitchenCabinetMenu container, Inventory inventory, Component text) {
        // Use a translatable component instead of raw text
        super(container, inventory, Component.translatable("gui.concoction.kitchen_cabinet"));
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // 1. Dark translucent background
        guiGraphics.fillGradient(0, 0, this.width, this.height, 0x33000000, 0x33000000);
        
        // 2. Render GUI
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        // 3. Tooltips
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(texture, this.leftPos, this.topPos - 1, 0, 0, this.imageWidth, this.imageHeight); // move texture 1px up
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 5, 0x404040, false); // was 6, now 5
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 93, 0x404040, false); // was -92, now -93
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(key, b, c);
    }

    @Override
    public void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2 - 1; // Shift whole GUI up by 1px
    }
}
