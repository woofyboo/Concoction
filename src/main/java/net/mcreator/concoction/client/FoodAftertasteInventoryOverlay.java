package net.mcreator.concoction.client;

import com.mojang.blaze3d.platform.Lighting;
import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.handlers.FoodAftertasteHandler;
import net.mcreator.concoction.utils.AlphaVertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class FoodAftertasteInventoryOverlay {
    private static final int CELL_SIZE = 20;
    private static final int ICON_OFFSET_X = 2;
    private static final int ICON_OFFSET_Y = 3;
    private static final int COLUMN_TOP_OFFSET = 2;
    private static final int COLUMN_GAP = 2;
    private static final int CELL_GAP = 5;
    private static final int BACKGROUND_COLOR = 0xC0100010;
    private static final int BORDER_LIGHT = 0x505000FF;
    private static final int BORDER_DARK = 0x5028007F;
    private static final int Z_OFFSET = 300;
    private static final int COMPACT_EFFECT_WIDTH = 32;
    private static final int TOOLTIP_WIDTH = 180;
    private static final String DESCRIPTION_INDENT = "   ";

    private FoodAftertasteInventoryOverlay() {
    }

    @SubscribeEvent
    public static void onContainerRender(ContainerScreenEvent.Render.Foreground event) {
        if (!shouldRenderOn(event.getContainerScreen())) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        List<FoodAftertasteHandler.ActiveAftertasteEntry> entries =
                FoodAftertasteHandler.getActiveAftertasteEntries(minecraft.player);
        if (entries.isEmpty()) {
            return;
        }

        AbstractContainerScreen<?> containerScreen = event.getContainerScreen();
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        ColumnLayout layout = getColumnLayout(containerScreen, minecraft, screenWidth, screenHeight, entries.size());
        drawEntries(
                event.getGuiGraphics(),
                minecraft,
                entries,
                layout.x() - containerScreen.getGuiLeft(),
                layout.y() - containerScreen.getGuiTop()
        );
    }

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> containerScreen) || !shouldRenderOn(event.getScreen())) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }

        List<FoodAftertasteHandler.ActiveAftertasteEntry> entries =
                FoodAftertasteHandler.getActiveAftertasteEntries(minecraft.player);
        if (entries.isEmpty()) {
            return;
        }

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        ColumnLayout layout = getColumnLayout(containerScreen, minecraft, screenWidth, screenHeight, entries.size());
        FoodAftertasteHandler.ActiveAftertasteEntry hoveredEntry =
                getHoveredEntry(entries, layout.x(), layout.y(), event.getMouseX(), event.getMouseY());

        if (hoveredEntry != null) {
            event.getGuiGraphics().renderTooltip(minecraft.font, buildTooltip(minecraft, hoveredEntry), event.getMouseX(), event.getMouseY());
        }
    }

    private static boolean shouldRenderOn(net.minecraft.client.gui.screens.Screen screen) {
        return screen instanceof InventoryScreen || screen instanceof CreativeModeInventoryScreen;
    }

    private static ColumnLayout getColumnLayout(
            AbstractContainerScreen<?> containerScreen,
            Minecraft minecraft,
            int screenWidth,
            int screenHeight,
            int entryCount
    ) {
        int columnHeight = entryCount * CELL_SIZE + Math.max(0, entryCount - 1) * CELL_GAP;
        int x = getColumnX(containerScreen, minecraft, screenWidth);
        int y = Math.max(4, Math.min(containerScreen.getGuiTop() + COLUMN_TOP_OFFSET, screenHeight - columnHeight - 4));
        return new ColumnLayout(x, y);
    }

    private static int getColumnX(AbstractContainerScreen<?> containerScreen, Minecraft minecraft, int screenWidth) {
        int x = containerScreen.getGuiLeft() + containerScreen.getXSize() + COLUMN_GAP;

        if (containerScreen instanceof EffectRenderingInventoryScreen<?> effectScreen
                && minecraft.player != null
                && !minecraft.player.getActiveEffects().isEmpty()
                && effectScreen.canSeeEffects()) {
            int effectStartX = containerScreen.getGuiLeft() + containerScreen.getXSize() + COLUMN_GAP;
            int remainingWidth = screenWidth - effectStartX;
            ScreenEvent.RenderInventoryMobEffects potionEvent = ClientHooks.onScreenPotionSize(
                    containerScreen,
                    remainingWidth,
                    remainingWidth < 120,
                    effectStartX
            );
            if (!potionEvent.isCanceled()) {
                x = potionEvent.getHorizontalOffset() + COMPACT_EFFECT_WIDTH + COLUMN_GAP;
            }
        }

        if (x + CELL_SIZE > screenWidth - 4) {
            x = containerScreen.getGuiLeft() - CELL_SIZE - COLUMN_GAP;
        }

        return Math.max(4, x);
    }

    private static void drawEntries(
            GuiGraphics graphics,
            Minecraft minecraft,
            List<FoodAftertasteHandler.ActiveAftertasteEntry> entries,
            int x,
            int y
    ) {
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, Z_OFFSET - 200);

        for (int i = 0; i < entries.size(); i++) {
            FoodAftertasteHandler.ActiveAftertasteEntry entry = entries.get(i);
            int cellY = y + i * (CELL_SIZE + CELL_GAP);

            graphics.fill(x, cellY, x + CELL_SIZE, cellY + CELL_SIZE, BACKGROUND_COLOR);
            graphics.fill(x - 1, cellY - 1, x + CELL_SIZE + 1, cellY, BORDER_LIGHT);
            graphics.fill(x - 1, cellY + CELL_SIZE, x + CELL_SIZE + 1, cellY + CELL_SIZE + 1, BORDER_DARK);
            graphics.fill(x - 1, cellY, x, cellY + CELL_SIZE, BORDER_LIGHT);
            graphics.fill(x + CELL_SIZE, cellY, x + CELL_SIZE + 1, cellY + CELL_SIZE, BORDER_DARK);
            renderItemWithAlpha(graphics, minecraft, entry, x + ICON_OFFSET_X, cellY + ICON_OFFSET_Y);
        }

        graphics.pose().popPose();
    }

    private static void renderItemWithAlpha(
            GuiGraphics graphics,
            Minecraft minecraft,
            FoodAftertasteHandler.ActiveAftertasteEntry entry,
            int itemX,
            int itemY
    ) {
        float alpha = getItemAlpha(minecraft, entry);
        renderAlphaItem(graphics, minecraft, entry, itemX, itemY, alpha);
    }

    private static float getItemAlpha(Minecraft minecraft, FoodAftertasteHandler.ActiveAftertasteEntry entry) {
        if (entry.disabled()) {
            if (!entry.imminentExpiration()) {
                return 0.5F;
            }

            return 0.25F + 0.25F * Mth.sin(minecraft.player.tickCount * 0.35F);
        }

        if (!entry.imminentExpiration()) {
            return 1.0F;
        }

        return 0.5F + 0.5F * Mth.sin(minecraft.player.tickCount * 0.35F);
    }

    private static void renderAlphaItem(
            GuiGraphics graphics,
            Minecraft minecraft,
            FoodAftertasteHandler.ActiveAftertasteEntry entry,
            int itemX,
            int itemY,
            float alpha
    ) {
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        BakedModel bakedModel = itemRenderer.getModel(entry.sourceStack(), minecraft.level, minecraft.player, 0);

        graphics.pose().pushPose();
        graphics.pose().translate(itemX + 8.0F, itemY + 8.0F, 150.0F + (bakedModel.isGui3d() ? 0.0F : 0.0F));
        graphics.pose().scale(16.0F, -16.0F, 16.0F);

        boolean flatLighting = !bakedModel.usesBlockLight();
        if (flatLighting) {
            Lighting.setupForFlatItems();
        }

        MultiBufferSource alphaBuffers = new AlphaMultiBufferSource(graphics.bufferSource(), alpha);
        itemRenderer.render(
                entry.sourceStack(),
                ItemDisplayContext.GUI,
                false,
                graphics.pose(),
                alphaBuffers,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                bakedModel
        );
        graphics.flush();

        if (flatLighting) {
            Lighting.setupFor3DItems();
        }

        graphics.pose().popPose();
    }

    private static List<FormattedCharSequence> buildTooltip(Minecraft minecraft, FoodAftertasteHandler.ActiveAftertasteEntry entry) {
        List<FormattedCharSequence> lines = new ArrayList<>();
        lines.addAll(minecraft.font.split(entry.sourceStack().getHoverName().copy().withStyle(ChatFormatting.WHITE), TOOLTIP_WIDTH));

        Component titleLine = Component.literal("- ").withStyle(ChatFormatting.DARK_GRAY).append(entry.type().getTooltipTitle());
        lines.addAll(minecraft.font.split(titleLine, TOOLTIP_WIDTH));

        List<FormattedCharSequence> descriptionLines = minecraft.font.split(
                entry.type().getTooltipDescription(FoodTooltipClientSettings.isDetailedView()),
                TOOLTIP_WIDTH - minecraft.font.width(DESCRIPTION_INDENT)
        );
        FormattedCharSequence indent = FormattedCharSequence.forward(DESCRIPTION_INDENT, Style.EMPTY.withColor(ChatFormatting.GRAY));
        for (FormattedCharSequence descriptionLine : descriptionLines) {
            lines.add(FormattedCharSequence.composite(indent, descriptionLine));
        }

        return lines;
    }

    private static FoodAftertasteHandler.ActiveAftertasteEntry getHoveredEntry(
            List<FoodAftertasteHandler.ActiveAftertasteEntry> entries,
            int x,
            int y,
            int mouseX,
            int mouseY
    ) {
        for (int i = 0; i < entries.size(); i++) {
            int cellY = y + i * (CELL_SIZE + CELL_GAP);
            if (mouseX >= x && mouseX < x + CELL_SIZE && mouseY >= cellY && mouseY < cellY + CELL_SIZE) {
                return entries.get(i);
            }
        }
        return null;
    }

    private record ColumnLayout(int x, int y) {
    }

    private record AlphaMultiBufferSource(MultiBufferSource delegate, float alphaMultiplier) implements MultiBufferSource {
        @Override
        public com.mojang.blaze3d.vertex.VertexConsumer getBuffer(net.minecraft.client.renderer.RenderType renderType) {
            return new AlphaVertexConsumer(delegate.getBuffer(renderType), alphaMultiplier);
        }
    }
}
