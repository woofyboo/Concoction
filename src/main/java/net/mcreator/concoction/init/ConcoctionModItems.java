
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.bus.api.IEventBus;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.concoction.item.MintSeedsItem;
import net.mcreator.concoction.item.MintBrewItem;
import net.mcreator.concoction.item.CherryItem;
import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(BuiltInRegistries.ITEM, ConcoctionMod.MODID);
	public static final DeferredHolder<Item, Item> MINT = doubleBlock(ConcoctionModBlocks.MINT);
	public static final DeferredHolder<Item, Item> MINT_SEEDS = REGISTRY.register("mint_seeds", () -> new MintSeedsItem());
	public static final DeferredHolder<Item, Item> MINT_BREW = REGISTRY.register("mint_brew", () -> new MintBrewItem());
	public static final DeferredHolder<Item, Item> CHERRY = REGISTRY.register("cherry", () -> new CherryItem());
	public static final DeferredHolder<Item, Item> CROP_MINT = block(ConcoctionModBlocks.CROP_MINT);

	// Start of user code block custom items
	// End of user code block custom items
	public static void register(IEventBus bus) {
		REGISTRY.register(bus);
	}

	private static DeferredHolder<Item, Item> block(DeferredHolder<Block, Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}

	private static DeferredHolder<Item, Item> doubleBlock(DeferredHolder<Block, Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new DoubleHighBlockItem(block.get(), new Item.Properties()));
	}
}
