package net.mcreator.concoction.init;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.item.CinnamonBoatDispenseItemBehavior;
import net.mcreator.concoction.item.CinnamonBoatItem;
import net.mcreator.concoction.item.CinnamonChestBoatItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ConcoctionBoatItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(ConcoctionMod.MODID);

	public static final DeferredItem<Item> CINNAMON_BOAT = REGISTRY.register("cinnamon_boat",
			CinnamonBoatItem::new);

	public static final DeferredItem<Item> CINNAMON_CHEST_BOAT = REGISTRY.register("cinnamon_chest_boat",
			CinnamonChestBoatItem::new);

	public static void registerDispenserBehaviors() {
		DispenserBlock.registerBehavior(CINNAMON_BOAT.get(), new CinnamonBoatDispenseItemBehavior(false));
		DispenserBlock.registerBehavior(CINNAMON_CHEST_BOAT.get(), new CinnamonBoatDispenseItemBehavior(true));
	}

	private ConcoctionBoatItems() {
	}
}
