
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
import net.minecraft.world.food.FoodProperties;

import net.mcreator.concoction.item.SweetSlimeJellyItem;
import net.mcreator.concoction.item.SunflowerOilItem;
import net.mcreator.concoction.item.SpicyPepperItem;
import net.mcreator.concoction.item.RoastedSunflowerSeedsItem;
import net.mcreator.concoction.item.RoastedPineconeItem;
import net.mcreator.concoction.item.PopcornItem;
import net.mcreator.concoction.item.PineconeItem;
import net.mcreator.concoction.item.OnionSoupItem;
import net.mcreator.concoction.item.ObsidianTearsBottleItem;
import net.mcreator.concoction.item.MusicDiscHotIceItem;
import net.mcreator.concoction.item.MintySlimeJellyItem;
import net.mcreator.concoction.item.MintCookieItem;
import net.mcreator.concoction.item.MintBrewItem;
import net.mcreator.concoction.item.MeatGoulashItem;
import net.mcreator.concoction.item.MashedPotatoesItem;
import net.mcreator.concoction.item.HotSauceBottleItem;
import net.mcreator.concoction.item.HashbrownsItem;
import net.mcreator.concoction.item.GoldenCornItem;
import net.mcreator.concoction.item.FungusStewItem;
import net.mcreator.concoction.item.FriedEggItem;
import net.mcreator.concoction.item.FishAndChipsItem;
import net.mcreator.concoction.item.FabricItem;
import net.mcreator.concoction.item.CottonOilItem;
import net.mcreator.concoction.item.CornSoupItem;
import net.mcreator.concoction.item.CornItem;
import net.mcreator.concoction.item.CookedCornItem;
import net.mcreator.concoction.item.ChocolateItem;
import net.mcreator.concoction.item.CherryItem;
import net.mcreator.concoction.item.CherryCookieItem;
import net.mcreator.concoction.item.ButterItem;
import net.mcreator.concoction.item.BambooPorkchopSoupItem;
import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(ConcoctionMod.MODID);
	public static final DeferredItem<Item> MINT = doubleBlock(ConcoctionModBlocks.MINT);
	public static final DeferredItem<Item> MINT_BREW = REGISTRY.register("mint_brew", MintBrewItem::new);
	public static final DeferredItem<Item> CHERRY = REGISTRY.register("cherry", CherryItem::new);
	public static final DeferredItem<Item> CROP_MINT = block(ConcoctionModBlocks.CROP_MINT);
	public static final DeferredItem<Item> MINT_COOKIE = REGISTRY.register("mint_cookie", MintCookieItem::new);
	public static final DeferredItem<Item> FABRIC = REGISTRY.register("fabric", FabricItem::new);
	public static final DeferredItem<Item> CROP_COTTON = block(ConcoctionModBlocks.CROP_COTTON);
	public static final DeferredItem<Item> PILLOW_BLOCK = block(ConcoctionModBlocks.PILLOW_BLOCK);
	public static final DeferredItem<Item> WILD_COTTON = block(ConcoctionModBlocks.WILD_COTTON);
	public static final DeferredItem<Item> CHERRY_COOKIE = REGISTRY.register("cherry_cookie", CherryCookieItem::new);
	public static final DeferredItem<Item> SUNFLOWER = block(ConcoctionModBlocks.SUNFLOWER);
	public static final DeferredItem<Item> MUSIC_DISC_HOT_ICE = REGISTRY.register("music_disc_hot_ice", MusicDiscHotIceItem::new);
	public static final DeferredItem<Item> ROASTED_SUNFLOWER_SEEDS = REGISTRY.register("roasted_sunflower_seeds", RoastedSunflowerSeedsItem::new);
	public static final DeferredItem<Item> SWEET_SLIME_JELLY = REGISTRY.register("sweet_slime_jelly", SweetSlimeJellyItem::new);
	public static final DeferredItem<Item> MINTY_SLIME_JELLY = REGISTRY.register("minty_slime_jelly", MintySlimeJellyItem::new);
	public static final DeferredItem<Item> WILD_CARROT = doubleBlock(ConcoctionModBlocks.WILD_CARROT);
	public static final DeferredItem<Item> OBSIDIAN_TEARS_BOTTLE = REGISTRY.register("obsidian_tears_bottle", ObsidianTearsBottleItem::new);
	public static final DeferredItem<Item> MEAT_GOULASH = REGISTRY.register("meat_goulash", MeatGoulashItem::new);
	public static final DeferredItem<Item> SUNFLOWER_OIL = REGISTRY.register("sunflower_oil", SunflowerOilItem::new);
	public static final DeferredItem<Item> COTTON_OIL = REGISTRY.register("cotton_oil", CottonOilItem::new);
	public static final DeferredItem<Item> BUTTER = REGISTRY.register("butter", ButterItem::new);
	public static final DeferredItem<Item> PINECONE = REGISTRY.register("pinecone", PineconeItem::new);
	public static final DeferredItem<Item> ROASTED_PINECONE = REGISTRY.register("roasted_pinecone", RoastedPineconeItem::new);
	public static final DeferredItem<Item> HASHBROWNS = REGISTRY.register("hashbrowns", HashbrownsItem::new);
	public static final DeferredItem<Item> FRIED_EGG = REGISTRY.register("fried_egg", FriedEggItem::new);
	public static final DeferredItem<Item> FISH_AND_CHIPS = REGISTRY.register("fish_and_chips", FishAndChipsItem::new);
	public static final DeferredItem<Item> CHOCOLATE = REGISTRY.register("chocolate", ChocolateItem::new);
	public static final DeferredItem<Item> MASHED_POTATOES = REGISTRY.register("mashed_potatoes", MashedPotatoesItem::new);
	public static final DeferredItem<Item> CROP_ONION = block(ConcoctionModBlocks.CROP_ONION);
	public static final DeferredItem<Item> WILD_ONION = block(ConcoctionModBlocks.WILD_ONION);
	public static final DeferredItem<Item> CROP_CORN = block(ConcoctionModBlocks.CROP_CORN);
	public static final DeferredItem<Item> CORN = REGISTRY.register("corn", CornItem::new);
	public static final DeferredItem<Item> POPCORN = REGISTRY.register("popcorn", PopcornItem::new);
	public static final DeferredItem<Item> COOKED_CORN = REGISTRY.register("cooked_corn", CookedCornItem::new);
	public static final DeferredItem<Item> CORN_SOUP = REGISTRY.register("corn_soup", CornSoupItem::new);
	public static final DeferredItem<Item> CROP_SPICY_PEPPER = block(ConcoctionModBlocks.CROP_SPICY_PEPPER);
	public static final DeferredItem<Item> SPICY_PEPPER = REGISTRY.register("spicy_pepper", SpicyPepperItem::new);
	public static final DeferredItem<Item> GOLDEN_CORN = REGISTRY.register("golden_corn", GoldenCornItem::new);
	public static final DeferredItem<Item> HOT_SAUCE_BOTTLE = REGISTRY.register("hot_sauce_bottle", HotSauceBottleItem::new);
	public static final DeferredItem<Item> FUNGUS_STEW = REGISTRY.register("fungus_stew", FungusStewItem::new);
	public static final DeferredItem<Item> WILD_SPICY_PEPPER = block(ConcoctionModBlocks.WILD_SPICY_PEPPER);
	public static final DeferredItem<Item> ONION_SOUP = REGISTRY.register("onion_soup", OnionSoupItem::new);
	public static final DeferredItem<Item> BAMBOO_PORKCHOP_SOUP = REGISTRY.register("bamboo_porkchop_soup", BambooPorkchopSoupItem::new);
	public static final DeferredItem<Item> CROP_RICE = block(ConcoctionModBlocks.CROP_RICE);
	public static final DeferredItem<Item> RICE = REGISTRY.register("rice", RiceItem::new);
	// Start of user code block custom items
	public static final DeferredItem<Item> MINT_CHOCOLATE_CAKE = block(ConcoctionModBlocks.MINT_CHOCOLATE_CAKE, 1);
	public static final DeferredItem<Item> SPICY_PEPPER_SEEDS = REGISTRY.register("spicy_pepper_seeds", () -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_SPICY_PEPPER.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> CORN_SEEDS = REGISTRY.register("corn_seeds", () -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_CORN.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> SUNFLOWER_SEEDS = REGISTRY.register("sunflower_seeds", () -> new ItemNameBlockItem(ConcoctionModBlocks.SUNFLOWER.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> MINT_SEEDS = REGISTRY.register("mint_seeds", () -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_MINT.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> COTTON = REGISTRY.register("cotton", () -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_COTTON.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> ONION = REGISTRY.register("onion",
			() -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_ONION.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(4).saturationModifier(0.3f).build())));
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

	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block, int stackSize) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties().stacksTo(stackSize)));
	}

	// End of user code block custom items
	private static DeferredItem<Item> block(DeferredHolder<Block, Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}

	private static DeferredItem<Item> doubleBlock(DeferredHolder<Block, Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new DoubleHighBlockItem(block.get(), new Item.Properties()));
	}
}
