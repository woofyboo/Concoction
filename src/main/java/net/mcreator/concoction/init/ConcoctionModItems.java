
/*
*    MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.BlockItem;

import net.mcreator.concoction.item.MintCookieItem;
import net.mcreator.concoction.item.MintBrewItem;
import net.mcreator.concoction.item.FabricItem;
import net.mcreator.concoction.item.CherryItem;
import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(ConcoctionMod.MODID);
	public static final DeferredHolder<Item, Item> MINT = doubleBlock(ConcoctionModBlocks.MINT);
	public static final DeferredHolder<Item, Item> MINT_BREW = REGISTRY.register("mint_brew", MintBrewItem::new);
	public static final DeferredHolder<Item, Item> CHERRY = REGISTRY.register("cherry", CherryItem::new);
	public static final DeferredHolder<Item, Item> CROP_MINT = block(ConcoctionModBlocks.CROP_MINT);
	public static final DeferredHolder<Item, Item> MINT_COOKIE = REGISTRY.register("mint_cookie", MintCookieItem::new);
	public static final DeferredHolder<Item, Item> MINT_CHOCOLATE_CAKE = block(ConcoctionModBlocks.MINT_CHOCOLATE_CAKE);
	public static final DeferredHolder<Item, Item> FABRIC = REGISTRY.register("fabric", FabricItem::new);
	public static final DeferredHolder<Item, Item> CROP_COTTON = block(ConcoctionModBlocks.CROP_COTTON);
	public static final DeferredHolder<Item, Item> PILLOW_BLOCK = block(ConcoctionModBlocks.PILLOW_BLOCK);
	public static final DeferredHolder<Item, Item> RED_PILLOW_BLOCK = block(ConcoctionModBlocks.RED_PILLOW_BLOCK);
	public static final DeferredHolder<Item, Item> ORANGE_PILLOW_BLOCK = block(ConcoctionModBlocks.ORANGE_PILLOW_BLOCK);
	public static final DeferredHolder<Item, Item> BROWN_PILLOW_BLOCK = block(ConcoctionModBlocks.BROWN_PILLOW_BLOCK);
	public static final DeferredHolder<Item, Item> YELLOW_PILLOW_BLOCK = block(ConcoctionModBlocks.YELLOW_PILLOW_BLOCK);
	public static final DeferredHolder<Item, Item> LIME_PILLOW_BLOCK = block(ConcoctionModBlocks.LIME_PILLOW_BLOCK);
	public static final DeferredHolder<Item, Item> GREEN_PILLOW_BLOCK = block(ConcoctionModBlocks.GREEN_PILLOW_BLOCK);
	public static final DeferredHolder<Item, Item> CYAN_PILLOW_BLOCK = block(ConcoctionModBlocks.CYAN_PILLOW_BLOCK);
	public static final DeferredHolder<Item, Item> LIGHT_BLUE_PILLOW_BLOCK = block(ConcoctionModBlocks.LIGHT_BLUE_PILLOW_BLOCK);
	public static final DeferredHolder<Item, Item> BLUE_PILLOW_BLOCK = block(ConcoctionModBlocks.BLUE_PILLOW_BLOCK);
	public static final DeferredHolder<Item, Item> PURPLE_PILLOW_BLOCK = block(ConcoctionModBlocks.PURPLE_PILLOW_BLOCK);
	public static final DeferredHolder<Item, Item> MAGENTA_PILLOW_BLOCK = block(ConcoctionModBlocks.MAGENTA_PILLOW_BLOCK);
	public static final DeferredHolder<Item, Item> PINK_PILLOW_BLOCK = block(ConcoctionModBlocks.PINK_PILLOW_BLOCK);
	public static final DeferredHolder<Item, Item> LIGHT_GRAY_PILLOW_BLOCK = block(ConcoctionModBlocks.LIGHT_GRAY_PILLOW_BLOCK);
	public static final DeferredHolder<Item, Item> GRAY_PILLOW_BLOCK = block(ConcoctionModBlocks.GRAY_PILLOW_BLOCK);
	public static final DeferredHolder<Item, Item> BLACK_PILLOW_BLOCK = block(ConcoctionModBlocks.BLACK_PILLOW_BLOCK);
	public static final DeferredHolder<Item, Item> WILD_COTTON = block(ConcoctionModBlocks.WILD_COTTON);
	// Start of user code block custom items
	public static final DeferredHolder<Item, Item> MINT_SEEDS = REGISTRY.register("mint_seeds", () -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_MINT.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredHolder<Item, Item> COTTON = REGISTRY.register("cotton", () -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_COTTON.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));

	// End of user code block custom items
	private static DeferredHolder<Item, Item> block(DeferredHolder<Block, Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}

	private static DeferredHolder<Item, Item> doubleBlock(DeferredHolder<Block, Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new DoubleHighBlockItem(block.get(), new Item.Properties()));
	}
}
