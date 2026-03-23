package net.mcreator.concoction.event;

import net.mcreator.concoction.ConcoctionMod;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CakeBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class FoodTooltipEvents {
	private FoodTooltipEvents() {
	}

	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		if (Screen.hasControlDown() || !shouldShowFoodTooltip(event.getItemStack())) {
			return;
		}

		event.getToolTip().add(
				Component.translatable(
						"tooltip.concoction.hold_key",
						Component.literal("Ctrl")
				).withStyle(ChatFormatting.DARK_GRAY)
		);
	}

	private static boolean shouldShowFoodTooltip(ItemStack stack) {
		if (stack.has(DataComponents.FOOD)) {
			return true;
		}

		if (stack.getItem() instanceof BlockItem blockItem) {
			return blockItem.getBlock() instanceof CakeBlock;
		}

		return false;
	}
}
