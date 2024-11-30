
/*
*	MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.concoction.init;

import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.npc.VillagerProfession;

@EventBusSubscriber
public class ConcoctionModTrades {
	@SubscribeEvent
	public static void registerTrades(VillagerTradesEvent event) {
		if (event.getType() == VillagerProfession.FARMER) {
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(Items.EMERALD, 3), new ItemStack(ConcoctionModBlocks.MINT.get()), 16, 5, 0.05f));
		}
		if (event.getType() == VillagerProfession.FARMER) {
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(ConcoctionModItems.CHERRY.get(), 8), new ItemStack(Items.EMERALD), 16, 5, 0.05f));
		}
		if (event.getType() == VillagerProfession.FARMER) {
			event.getTrades().get(4).add(new BasicItemListing(new ItemStack(Items.EMERALD), new ItemStack(ConcoctionModBlocks.MINT_CHOCOLATE_CAKE.get()), 12, 15, 0.05f));
		}
		if (event.getType() == VillagerProfession.SHEPHERD) {
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(ConcoctionModItems.FABRIC.get(), 28), new ItemStack(Items.EMERALD), 16, 5, 0.05f));
		}
		if (event.getType() == VillagerProfession.SHEPHERD) {
			event.getTrades().get(5).add(new BasicItemListing(new ItemStack(Items.EMERALD), new ItemStack(ConcoctionModBlocks.PILLOW_BLOCK.get(), 14), 8, 30, 0.05f));
		}
		if (event.getType() == VillagerProfession.BUTCHER) {
			event.getTrades().get(1).add(new BasicItemListing(new ItemStack(ConcoctionModItems.BUTTER.get(), 4), new ItemStack(Items.EMERALD), 16, 5, 0.05f));
		}
		if (event.getType() == VillagerProfession.FARMER) {
			event.getTrades().get(3).add(new BasicItemListing(new ItemStack(Items.EMERALD), new ItemStack(ConcoctionModItems.CHOCOLATE.get(), 4), 12, 10, 0.05f));
		}
		if (event.getType() == VillagerProfession.CLERIC) {
			event.getTrades().get(2).add(new BasicItemListing(new ItemStack(ConcoctionModItems.OBSIDIAN_TEARS_BOTTLE.get()), new ItemStack(Items.EMERALD, 8), 4, 10, 0.05f));
		}
		if (event.getType() == VillagerProfession.LEATHERWORKER) {
			event.getTrades().get(4).add(new BasicItemListing(new ItemStack(Items.EMERALD, 3), new ItemStack(ConcoctionModItems.FABRIC.get(), 8), 10, 5, 0.15f));
		}
	}
}
