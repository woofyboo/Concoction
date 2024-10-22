package net.mcreator.concoction.procedures;

import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.concoction.init.ConcoctionModBlocks;

import javax.annotation.Nullable;

import java.util.Collections;

@EventBusSubscriber
public class RecipeMintSeedsProcedure {
	@SubscribeEvent
	public static void onPickup(ItemEntityPickupEvent.Pre event) {
		execute(event, event.getPlayer(), event.getItemEntity().getItem());
	}

	public static void execute(Entity entity, ItemStack itemstack) {
		execute(null, entity, itemstack);
	}

	private static void execute(@Nullable Event event, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (itemstack.getItem() == ConcoctionModBlocks.MINT.get().asItem()) {
			if (entity instanceof ServerPlayer _serverPlayer)
				_serverPlayer.awardRecipesByKey(Collections.singletonList(ResourceLocation.parse("concoction:purple_dye_craft")));
			if (entity instanceof ServerPlayer _serverPlayer)
				_serverPlayer.awardRecipesByKey(Collections.singletonList(ResourceLocation.parse("concoction:minty_brew_craft")));
			if (entity instanceof ServerPlayer _serverPlayer)
				_serverPlayer.awardRecipesByKey(Collections.singletonList(ResourceLocation.parse("concoction:mint_chocolate_cake_craft")));
			if (entity instanceof ServerPlayer _serverPlayer)
				_serverPlayer.awardRecipesByKey(Collections.singletonList(ResourceLocation.parse("concoction:mint_cookie_craft")));
		}
	}
}
