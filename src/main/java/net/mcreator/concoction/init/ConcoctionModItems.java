
/*
*    MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.BlockItem;

import net.mcreator.concoction.item.RoastedSunflowerSeedsItem;
import net.mcreator.concoction.item.MusicDiscHotIceItem;
import net.mcreator.concoction.item.MintCookieItem;
import net.mcreator.concoction.item.MintBrewItem;
import net.mcreator.concoction.item.FabricItem;
import net.mcreator.concoction.item.CherryItem;
import net.mcreator.concoction.item.CherryCookieItem;
import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(ConcoctionMod.MODID);
	public static final DeferredItem<Item> MINT = doubleBlock(ConcoctionModBlocks.MINT);
	public static final DeferredItem<Item> MINT_BREW = REGISTRY.register("mint_brew", MintBrewItem::new);
	public static final DeferredItem<Item> CHERRY = REGISTRY.register("cherry", CherryItem::new);
	public static final DeferredItem<Item> CROP_MINT = block(ConcoctionModBlocks.CROP_MINT);
	public static final DeferredItem<Item> MINT_COOKIE = REGISTRY.register("mint_cookie", MintCookieItem::new);
	public static final DeferredItem<Item> MINT_CHOCOLATE_CAKE = block(ConcoctionModBlocks.MINT_CHOCOLATE_CAKE);
	public static final DeferredItem<Item> FABRIC = REGISTRY.register("fabric", FabricItem::new);
	public static final DeferredItem<Item> CROP_COTTON = block(ConcoctionModBlocks.CROP_COTTON);
	public static final DeferredItem<Item> PILLOW_BLOCK = block(ConcoctionModBlocks.WHITE_PILLOW_BLOCK);
	public static final DeferredItem<Item> WILD_COTTON = block(ConcoctionModBlocks.WILD_COTTON);
	public static final DeferredItem<Item> CHERRY_COOKIE = REGISTRY.register("cherry_cookie", CherryCookieItem::new);
	public static final DeferredItem<Item> SUNFLOWER = block(ConcoctionModBlocks.SUNFLOWER);
	public static final DeferredItem<Item> MUSIC_DISC_HOT_ICE = REGISTRY.register("music_disc_hot_ice", MusicDiscHotIceItem::new);
	public static final DeferredItem<Item> ROASTED_SUNFLOWER_SEEDS = REGISTRY.register("roasted_sunflower_seeds", RoastedSunflowerSeedsItem::new);
	// Start of user code block custom items
	public static final DeferredItem<Item> SUNFLOWER_SEEDS = REGISTRY.register("sunflower_seeds", () -> new ItemNameBlockItem(ConcoctionModBlocks.SUNFLOWER.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> MINT_SEEDS = REGISTRY.register("mint_seeds", () -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_MINT.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> COTTON = REGISTRY.register("cotton", () -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_COTTON.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> RED_PILLOW_BLOCK = block(ConcoctionModBlocks.RED_PILLOW_BLOCK);
	public static final DeferredItem<Item> ORANGE_PILLOW_BLOCK = block(ConcoctionModBlocks.ORANGE_PILLOW_BLOCK);
	public static final DeferredItem<Item> BROWN_PILLOW_BLOCK = block(ConcoctionModBlocks.BROWN_PILLOW_BLOCK);
	public static final DeferredItem<Item> YELLOW_PILLOW_BLOCK = block(ConcoctionModBlocks.YELLOW_PILLOW_BLOCK);
	public static final DeferredItem<Item> LIME_PILLOW_BLOCK = block(ConcoctionModBlocks.LIME_PILLOW_BLOCK);
	public static final DeferredItem<Item> GREEN_PILLOW_BLOCK = block(ConcoctionModBlocks.GREEN_PILLOW_BLOCK);
	public static final DeferredItem<Item> CYAN_PILLOW_BLOCK = block(ConcoctionModBlocks.CYAN_PILLOW_BLOCK);
	public static final DeferredItem<Item> LIGHT_BLUE_PILLOW_BLOCK = block(ConcoctionModBlocks.LIGHT_BLUE_PILLOW_BLOCK);
	public static final DeferredItem<Item> BLUE_PILLOW_BLOCK = block(ConcoctionModBlocks.BLUE_PILLOW_BLOCK);
	public static final DeferredItem<Item> PURPLE_PILLOW_BLOCK = block(ConcoctionModBlocks.PURPLE_PILLOW_BLOCK);
	public static final DeferredItem<Item> MAGENTA_PILLOW_BLOCK = block(ConcoctionModBlocks.MAGENTA_PILLOW_BLOCK);
	public static final DeferredItem<Item> PINK_PILLOW_BLOCK = block(ConcoctionModBlocks.PINK_PILLOW_BLOCK);
	public static final DeferredItem<Item> LIGHT_GRAY_PILLOW_BLOCK = block(ConcoctionModBlocks.LIGHT_GRAY_PILLOW_BLOCK);
	public static final DeferredItem<Item> GRAY_PILLOW_BLOCK = block(ConcoctionModBlocks.GRAY_PILLOW_BLOCK);
	public static final DeferredItem<Item> BLACK_PILLOW_BLOCK = block(ConcoctionModBlocks.BLACK_PILLOW_BLOCK);

	public static final DeferredItem<Item> SMALL_WHITE_PILLOW_BLOCK = block(ConcoctionModBlocks.SMALL_WHITE_PILLOW_BLOCK);
	public static final DeferredItem<Item> SMALL_RED_PILLOW_BLOCK = block(ConcoctionModBlocks.SMALL_RED_PILLOW_BLOCK);
	public static final DeferredItem<Item> SMALL_ORANGE_PILLOW_BLOCK = block(ConcoctionModBlocks.SMALL_ORANGE_PILLOW_BLOCK);
	public static final DeferredItem<Item> SMALL_BROWN_PILLOW_BLOCK = block(ConcoctionModBlocks.SMALL_BROWN_PILLOW_BLOCK);
	public static final DeferredItem<Item> SMALL_YELLOW_PILLOW_BLOCK = block(ConcoctionModBlocks.SMALL_YELLOW_PILLOW_BLOCK);
	public static final DeferredItem<Item> SMALL_LIME_PILLOW_BLOCK = block(ConcoctionModBlocks.SMALL_LIME_PILLOW_BLOCK);
	public static final DeferredItem<Item> SMALL_GREEN_PILLOW_BLOCK = block(ConcoctionModBlocks.SMALL_GREEN_PILLOW_BLOCK);
	public static final DeferredItem<Item> SMALL_CYAN_PILLOW_BLOCK = block(ConcoctionModBlocks.SMALL_CYAN_PILLOW_BLOCK);
	public static final DeferredItem<Item> SMALL_LIGHT_BLUE_PILLOW_BLOCK = block(ConcoctionModBlocks.SMALL_LIGHT_BLUE_PILLOW_BLOCK);
	public static final DeferredItem<Item> SMALL_BLUE_PILLOW_BLOCK = block(ConcoctionModBlocks.SMALL_BLUE_PILLOW_BLOCK);
	public static final DeferredItem<Item> SMALL_PURPLE_PILLOW_BLOCK = block(ConcoctionModBlocks.SMALL_PURPLE_PILLOW_BLOCK);
	public static final DeferredItem<Item> SMALL_MAGENTA_PILLOW_BLOCK = block(ConcoctionModBlocks.SMALL_MAGENTA_PILLOW_BLOCK);
	public static final DeferredItem<Item> SMALL_PINK_PILLOW_BLOCK = block(ConcoctionModBlocks.SMALL_PINK_PILLOW_BLOCK);
	public static final DeferredItem<Item> SMALL_LIGHT_GRAY_PILLOW_BLOCK = block(ConcoctionModBlocks.SMALL_LIGHT_GRAY_PILLOW_BLOCK);
	public static final DeferredItem<Item> SMALL_GRAY_PILLOW_BLOCK = block(ConcoctionModBlocks.SMALL_GRAY_PILLOW_BLOCK);
	public static final DeferredItem<Item> SMALL_BLACK_PILLOW_BLOCK = block(ConcoctionModBlocks.SMALL_BLACK_PILLOW_BLOCK);
	// End of user code block custom items
	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}

	private static DeferredItem<Item> doubleBlock(DeferredHolder<Block, Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new DoubleHighBlockItem(block.get(), new Item.Properties()));
	}
}
