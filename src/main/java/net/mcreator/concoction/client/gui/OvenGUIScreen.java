package net.mcreator.concoction.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.mcreator.concoction.world.inventory.OvenGUIMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.ClickType;

import java.util.HashMap;

public class OvenGUIScreen extends AbstractContainerScreen<OvenGUIMenu> {
    private static final HashMap<String, Object> guistate = OvenGUIMenu.guistate;
    private final Level world;
    private final int x, y, z;
    private final Player entity;

    private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
    private boolean widthTooNarrow;

    private static final ResourceLocation TEXTURE =
            ResourceLocation.parse("concoction:textures/gui/hud/oven_gui_playerside.png");

    public OvenGUIScreen(OvenGUIMenu container, Inventory inventory, Component text) {
        super(container, inventory, text);
        this.world = container.world;
        this.x = container.x;
        this.y = container.y;
        this.z = container.z;
        this.entity = container.entity;
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = 74;
    }

    @Override
    protected void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;

        Minecraft mc = this.minecraft;
        if (mc == null) return;

        // инициализируем книгу рецептов
        this.recipeBookComponent.init(this.width, this.height, mc, this.widthTooNarrow, this.menu);
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);

        // зелёная кнопка книги
        this.addRenderableWidget(new ImageButton(
                this.leftPos + 17,
                this.height / 2 - 31,
                20,
                18,
                RecipeBookComponent.RECIPE_BUTTON_SPRITES,
                button -> {
                    this.recipeBookComponent.toggleVisibility();
                    this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
                    button.setPosition(this.leftPos + 17, this.height / 2 - 31);
                }
        ));


        this.titleLabelX = 8;
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.recipeBookComponent.tick();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            // когда экран узкий и книга открыта — фон отдельно, GUI печки не рисуем
            this.renderBackground(guiGraphics, mouseX, mouseY, partialTicks);
            this.recipeBookComponent.render(guiGraphics, mouseX, mouseY, partialTicks);
        } else {
            // нормальный режим: рисуем GUI духовки и поверх — книгу
            guiGraphics.fillGradient(0, 0, this.width, this.height, 0x33000000, 0x33000000);
            super.render(guiGraphics, mouseX, mouseY, partialTicks);
            this.recipeBookComponent.render(guiGraphics, mouseX, mouseY, partialTicks);
            // ВАЖНО: НЕ рендерим ghostRecipe, чтобы не было криво стоящих "призраков" слотов
            // this.recipeBookComponent.renderGhostRecipe(guiGraphics, this.leftPos, this.topPos, true, partialTicks);
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.recipeBookComponent.renderTooltip(guiGraphics, this.leftPos, this.topPos, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int gx, int gy) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        // прогресс-бар готовки
        if (menu.isCooking() && menu.getMaxProgress() > 0) {
            int progress = menu.getProgress();
            int maxProgress = menu.getMaxProgress();
            progress = Math.min(progress, maxProgress);
            int progressSize = maxProgress > 0 ? (24 * progress) / maxProgress : 0;
            if (progressSize > 0) {
                guiGraphics.blit(TEXTURE, this.leftPos + 103, this.topPos + 34, 176, 14, progressSize, 16, 256, 256);
            }
        }

        // иконка огня
        if (menu.isLit()) {
            guiGraphics.blit(TEXTURE, this.leftPos + 61, this.topPos + 60, 176, 0, 14, 14, 256, 256);
        }

        RenderSystem.disableBlend();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 6, 4210752, false);
        guiGraphics.drawString(this.font, Component.translatable("container.inventory"), 8, 72, 4210752, false);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType clickType) {
        super.slotClicked(slot, slotId, mouseButton, clickType);
        this.recipeBookComponent.slotClicked(slot);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.recipeBookComponent.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (this.widthTooNarrow && this.recipeBookComponent.isVisible()) {
            // когда экран узкий и книга открыта — клики по самому GUI игнорим
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
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

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        boolean outsideMain =
                mouseX < (double) left
                        || mouseY < (double) top
                        || mouseX >= (double) (left + this.imageWidth)
                        || mouseY >= (double) (top + this.imageHeight);

        return this.recipeBookComponent.hasClickedOutside(
                mouseX,
                mouseY,
                this.leftPos,
                this.topPos,
                this.imageWidth,
                this.imageHeight,
                button
        ) && outsideMain;
    }
}
