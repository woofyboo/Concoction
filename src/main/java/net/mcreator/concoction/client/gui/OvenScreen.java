package net.mcreator.concoction.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.mcreator.concoction.world.inventory.OvenMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;

public class OvenScreen extends AbstractContainerScreen<OvenMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.parse("concoction:textures/gui/hud/oven_gui_playerside.png");

    // индекс слота бутылки (совпадает с меню и миксином)
    private static final int BOTTLE_SLOT_INDEX = 36;

    private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
    private boolean widthTooNarrow;
    private int leftPosWithBook;

    private ImageButton recipeBookButton;

    public OvenScreen(OvenMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;

        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        this.leftPosWithBook = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        this.leftPos = this.leftPosWithBook;

        int guiTop = (this.height - this.imageHeight) / 2;

        this.recipeBookButton = new ImageButton(
                this.leftPos + 16,
                guiTop + 52,
                20,
                18,
                RecipeBookComponent.RECIPE_BUTTON_SPRITES,
                (button) -> {
                    this.recipeBookComponent.toggleVisibility();
                    this.leftPosWithBook = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
                    this.leftPos = this.leftPosWithBook;
                    button.setPosition(this.leftPos + 16, guiTop + 52);
                }
        );

        this.addRenderableWidget(this.recipeBookButton);
        this.addWidget(this.recipeBookComponent);
        this.setInitialFocus(this.recipeBookComponent);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.recipeBookComponent.tick();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.leftPos;
        int j = (this.height - this.imageHeight) / 2;

        guiGraphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);

        // --- ОГОНЁК ---
        if (this.menu.isLit()) {
            int flameHeight = 14;
            guiGraphics.blit(
                    TEXTURE,
                    i + 56 + 5,
                    j + 36 + 14 - flameHeight + 24,
                    176,
                    14 - flameHeight,
                    14,
                    flameHeight
            );
        }

        // --- ПОЛОСКА ПРОГРЕССА ---
        int progress = this.menu.getProgress();
        int max = this.menu.getMaxProgress();
        if (max > 0 && progress > 0) {
            int width = progress * 24 / max;
            if (width > 0) {
                guiGraphics.blit(
                        TEXTURE,
                        i + 79 + 23,
                        j + 34,
                        176,
                        14,
                        width + 1,
                        16
                );
            }
        }

        // --- МАСКА СЛОТА БУТЫЛКИ ---
        if (BOTTLE_SLOT_INDEX >= 0 && BOTTLE_SLOT_INDEX < this.menu.slots.size()) {
            Slot bottleSlot = this.menu.slots.get(BOTTLE_SLOT_INDEX);
            if (bottleSlot != null && bottleSlot.hasItem()) {
                guiGraphics.blit(
                        TEXTURE,
                        i + bottleSlot.x-1,
                        j + bottleSlot.y-1,
                        176,
                        30,
                        16,
                        16
                );
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
            this.recipeBookComponent.render(guiGraphics, mouseX, mouseY, partialTick);
        } else {
            super.render(guiGraphics, mouseX, mouseY, partialTick);
            this.recipeBookComponent.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        this.recipeBookComponent.renderGhostRecipe(
                guiGraphics,
                this.leftPos,
                (this.height - this.imageHeight) / 2,
                false,
                partialTick
        );

        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.recipeBookComponent.renderTooltip(
                guiGraphics,
                this.leftPos,
                (this.height - this.imageHeight) / 2,
                mouseX,
                mouseY
        );
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 6, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 0x404040, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.recipeBookComponent.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        super.slotClicked(slot, slotId, mouseButton, type);
        this.recipeBookComponent.slotClicked(slot);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.recipeBookComponent.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.recipeBookComponent.charTyped(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }
}
