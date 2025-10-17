
/*
*	MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.concoction.init;

import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

@EventBusSubscriber
public class ConcoctionModTrades {
	@SubscribeEvent
	public static void registerWanderingTrades(WandererTradesEvent event) {
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD, 16), new ItemStack(ConcoctionModBlocks.WANDERING_TRADER_CARPET.get()), 1, 5, 0.05f));
		event.getGenericTrades().add(new BasicItemListing(new ItemStack(Items.EMERALD), new ItemStack(ConcoctionModItems.SAPONARIA_ROOT.get(), 2), 5, 5, 0.05f));
	}
}
