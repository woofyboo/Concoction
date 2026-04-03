package net.mcreator.concoction.event;

import net.mcreator.concoction.client.FoodTooltipClientSettings;
import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectComponent;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffects;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CakeBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.joml.Vector2ic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class FoodTooltipEvents {
	private static final int PANEL_GAP = 12;
	private static final int PANEL_MAX_WIDTH = 180;
	private static final int PANEL_PADDING_X = 6;
	private static final int PANEL_PADDING_Y = 6;
	private static final int DESCRIPTION_INDENT = 10;
	private static final int LINE_SPACING = 2;
	private static final int BACKGROUND_COLOR = 0xF0100010;
	private static final int BORDER_LIGHT = 0x505000FF;
	private static final int BORDER_DARK = 0x5028007F;
	private static final int TOOLTIP_Z_OFFSET = 400;
	private static boolean altWasDown = false;

	private FoodTooltipEvents() {
	}

	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		if (!shouldShowFoodTooltip(event.getItemStack())) {
			altWasDown = false;
			return;
		}

		if (!Screen.hasControlDown()) {
			event.getToolTip().add(
					Component.translatable(
							"tooltip.concoction.hold_key",
							Component.literal("Ctrl")
						).withStyle(ChatFormatting.DARK_GRAY)
			);
			altWasDown = false;
			return;
		}

		if (!getFoodEffects(event.getItemStack()).isEmpty() || !getPassiveEffects(event.getItemStack()).isEmpty()) {
			String hintKey = FoodTooltipClientSettings.isDetailedView()
					? "tooltip.concoction.alt_for_simple_view"
					: "tooltip.concoction.alt_for_detailed_view";
			event.getToolTip().add(
					Component.translatable(
							hintKey,
							Component.literal("Alt")
						).withStyle(ChatFormatting.DARK_GRAY)
			);
		}
	}

	@SubscribeEvent
	public static void onRenderTooltip(RenderTooltipEvent.Pre event) {
		if (!Screen.hasControlDown() || !shouldShowFoodTooltip(event.getItemStack())) {
			altWasDown = false;
			return;
		}

		handleAltToggle(event.getItemStack());

		Font font = event.getFont();
		List<PanelLine> panelLines = getPropertyTexts(event.getItemStack());
		List<WrappedLine> wrappedLines = wrapLines(font, panelLines);
		if (wrappedLines.isEmpty()) {
			return;
		}

		int primaryTooltipWidth = getTooltipWidth(event.getComponents(), font);
		int primaryTooltipHeight = getTooltipHeight(event.getComponents(), font);
		int panelWidth = getPanelWidth(font, wrappedLines);
		int panelHeight = getPanelHeight(font, wrappedLines);
		Vector2ic primaryTooltipPosition = getTooltipPosition(event, primaryTooltipWidth, primaryTooltipHeight);

		int x = primaryTooltipPosition.x() + primaryTooltipWidth + PANEL_GAP;
		if (x + panelWidth > event.getScreenWidth()) {
			x = primaryTooltipPosition.x() - panelWidth - PANEL_GAP;
		}
		x = Math.max(4, Math.min(x, event.getScreenWidth() - panelWidth - 4));

		int y = Math.max(4, Math.min(primaryTooltipPosition.y(), event.getScreenHeight() - panelHeight - 4));
		renderPanel(event, font, wrappedLines, x, y, panelWidth, panelHeight);
	}

	private static boolean shouldShowFoodTooltip(ItemStack stack) {
		if (!getFoodEffects(stack).isEmpty()) {
			return true;
		}

		if (!getPassiveEffects(stack).isEmpty()) {
			return true;
		}

		if (stack.has(DataComponents.FOOD)) {
			return true;
		}

		if (stack.getItem() instanceof BlockItem blockItem) {
			return blockItem.getBlock() instanceof CakeBlock;
		}

		return false;
	}

	private static List<PanelLine> getPropertyTexts(ItemStack stack) {
		List<PanelLine> lines = new ArrayList<>();
		lines.add(new PanelLine(
				Component.translatable("tooltip.concoction.properties_title").withStyle(ChatFormatting.GOLD),
				0,
				1
		));

		List<FoodEffectComponent> foodEffects = getFoodEffects(stack);
		Set<FoodEffectComponent> baseFoodEffects = new HashSet<>(FoodEffects.getBase(stack));
		List<FoodPassiveEffectComponent> passiveEffects = getPassiveEffects(stack);
		Set<FoodPassiveEffectComponent> basePassiveEffects = new HashSet<>(FoodPassiveEffects.getBase(stack));
		if (foodEffects.isEmpty() && passiveEffects.isEmpty()) {
			lines.add(new PanelLine(
					Component.translatable("tooltip.concoction.no_special_effects").withStyle(ChatFormatting.GRAY),
					0,
					0
			));
			return lines;
		}

		for (int i = 0; i < foodEffects.size(); i++) {
			FoodEffectComponent foodEffect = foodEffects.get(i);
			ChatFormatting titleColor = baseFoodEffects.contains(foodEffect) ? ChatFormatting.YELLOW : ChatFormatting.AQUA;
			lines.add(new PanelLine(
					Component.literal("- ").withStyle(ChatFormatting.DARK_GRAY)
							.append(foodEffect.getTooltipTitle().copy().withStyle(titleColor)),
					0,
					0
			));
			lines.add(new PanelLine(
					foodEffect.getTooltipDescription(FoodTooltipClientSettings.isDetailedView()),
					DESCRIPTION_INDENT,
					i < foodEffects.size() - 1 || !passiveEffects.isEmpty() ? 1 : 0
			));
		}

		for (int i = 0; i < passiveEffects.size(); i++) {
			FoodPassiveEffectComponent passiveEffect = passiveEffects.get(i);
			ChatFormatting titleColor = basePassiveEffects.contains(passiveEffect) ? ChatFormatting.YELLOW : ChatFormatting.AQUA;
			lines.add(new PanelLine(
					Component.literal("- ").withStyle(ChatFormatting.DARK_GRAY).append(passiveEffect.type().getTooltipTitle(titleColor)),
					0,
					0
			));
			lines.add(new PanelLine(
					passiveEffect.type().getTooltipDescription(FoodTooltipClientSettings.isDetailedView()),
					DESCRIPTION_INDENT,
					i < passiveEffects.size() - 1 ? 1 : 0
			));
		}

		return lines;
	}

	private static List<FoodEffectComponent> getFoodEffects(ItemStack stack) {
		return FoodEffects.get(stack);
	}

	private static List<FoodPassiveEffectComponent> getPassiveEffects(ItemStack stack) {
		return FoodPassiveEffects.get(stack);
	}

	private static void handleAltToggle(ItemStack stack) {
		boolean altDown = Screen.hasAltDown();
		if (altDown && !altWasDown && (!getFoodEffects(stack).isEmpty() || !getPassiveEffects(stack).isEmpty())) {
			FoodTooltipClientSettings.toggleDetailedView();
		}
		altWasDown = altDown;
	}

	private static List<WrappedLine> wrapLines(Font font, List<PanelLine> texts) {
		List<WrappedLine> result = new ArrayList<>();
		for (PanelLine text : texts) {
			int availableWidth = Math.max(1, PANEL_MAX_WIDTH - text.indent());
			List<net.minecraft.util.FormattedCharSequence> split = font.split(text.text(), availableWidth);
			if (split.isEmpty()) {
				continue;
			}

			result.add(new WrappedLine(split, text.indent(), text.extraSpacing()));
		}
		return result;
	}

	private static int getTooltipWidth(List<ClientTooltipComponent> components, Font font) {
		int width = 0;
		for (ClientTooltipComponent component : components) {
			width = Math.max(width, component.getWidth(font));
		}
		return width;
	}

	private static int getPanelWidth(Font font, List<WrappedLine> lines) {
		int width = 0;
		for (WrappedLine line : lines) {
			for (net.minecraft.util.FormattedCharSequence sequence : line.lines()) {
				width = Math.max(width, font.width(sequence) + line.indent());
			}
		}
		return width + PANEL_PADDING_X * 2;
	}

	private static int getPanelHeight(Font font, List<WrappedLine> lines) {
		int height = PANEL_PADDING_Y * 2;
		for (WrappedLine line : lines) {
			height += line.lines().size() * font.lineHeight;
			height += line.extraSpacing() * LINE_SPACING;
		}
		return height;
	}

	private static int getTooltipHeight(List<ClientTooltipComponent> components, Font font) {
		int height = 0;
		for (ClientTooltipComponent component : components) {
			height += component.getHeight();
		}

		if (components.size() > 1) {
			height += 2;
		}

		return height;
	}

	private static Vector2ic getTooltipPosition(RenderTooltipEvent.Pre event, int width, int height) {
		ClientTooltipPositioner positioner = event.getTooltipPositioner();
		return positioner.positionTooltip(
				event.getScreenWidth(),
				event.getScreenHeight(),
				event.getX(),
				event.getY(),
				width,
				height
		);
	}

	private static void renderPanel(RenderTooltipEvent.Pre event, Font font, List<WrappedLine> lines, int x, int y, int width, int height) {
		int left = x;
		int top = y;
		int right = x + width;
		int bottom = y + height;

		var graphics = event.getGraphics();
		graphics.pose().pushPose();
		graphics.pose().translate(0.0F, 0.0F, TOOLTIP_Z_OFFSET);
		graphics.fill(left, top, right, bottom, BACKGROUND_COLOR);
		graphics.fill(left - 1, top - 1, right + 1, top, BORDER_LIGHT);
		graphics.fill(left - 1, bottom, right + 1, bottom + 1, BORDER_DARK);
		graphics.fill(left - 1, top, left, bottom, BORDER_LIGHT);
		graphics.fill(right, top, right + 1, bottom, BORDER_DARK);

		int textY = top + PANEL_PADDING_Y;
		for (WrappedLine line : lines) {
			for (net.minecraft.util.FormattedCharSequence sequence : line.lines()) {
				graphics.drawString(font, sequence, left + PANEL_PADDING_X + line.indent(), textY, 0xFFFFFF, false);
				textY += font.lineHeight;
			}
			textY += line.extraSpacing() * LINE_SPACING;
		}
		graphics.pose().popPose();
	}

	private record PanelLine(FormattedText text, int indent, int extraSpacing) {
	}

	private record WrappedLine(List<net.minecraft.util.FormattedCharSequence> lines, int indent, int extraSpacing) {
	}
}
