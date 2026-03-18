
/*
*    MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.concoction.init;

import net.mcreator.concoction.item.*;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.food.FoodProperties;

import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(ConcoctionMod.MODID);
	public static final DeferredItem<Item> MINT = doubleBlock(ConcoctionModBlocks.MINT);
	public static final DeferredItem<Item> MINT_BREW = REGISTRY.register("mint_brew", MintBrewItem::new);
	public static final DeferredItem<Item> CHERRY = REGISTRY.register("cherry", CherryItem::new);
	public static final DeferredItem<Item> MINT_COOKIE = REGISTRY.register("mint_cookie", MintCookieItem::new);
	public static final DeferredItem<Item> FABRIC = REGISTRY.register("fabric", FabricItem::new);
	public static final DeferredItem<Item> PILLOW_BLOCK = block(ConcoctionModBlocks.PILLOW_BLOCK);
	public static final DeferredItem<Item> WILD_COTTON = block(ConcoctionModBlocks.WILD_COTTON);
	public static final DeferredItem<Item> CHERRY_COOKIE = REGISTRY.register("cherry_cookie", CherryCookieItem::new);
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
	public static final DeferredItem<Item> WILD_ONION = block(ConcoctionModBlocks.WILD_ONION);
	public static final DeferredItem<Item> CORN = REGISTRY.register("corn", CornItem::new);
	public static final DeferredItem<Item> POPCORN = REGISTRY.register("popcorn", PopcornItem::new);
	public static final DeferredItem<Item> COOKED_CORN = REGISTRY.register("cooked_corn", CookedCornItem::new);
	public static final DeferredItem<Item> CORN_SOUP = REGISTRY.register("corn_soup", CornSoupItem::new);
	public static final DeferredItem<Item> SPICY_PEPPER = REGISTRY.register("spicy_pepper", SpicyPepperItem::new);
	public static final DeferredItem<Item> GOLDEN_CORN = REGISTRY.register("golden_corn", GoldenCornItem::new);
	public static final DeferredItem<Item> HOT_SAUCE_BOTTLE = REGISTRY.register("hot_sauce_bottle", HotSauceBottleItem::new);
	public static final DeferredItem<Item> FUNGUS_STEW = REGISTRY.register("fungus_stew", FungusStewItem::new);
	public static final DeferredItem<Item> WILD_SPICY_PEPPER = block(ConcoctionModBlocks.WILD_SPICY_PEPPER);
	public static final DeferredItem<Item> ONION_SOUP = REGISTRY.register("onion_soup", OnionSoupItem::new);
	public static final DeferredItem<Item> BAMBOO_PORKCHOP_SOUP = REGISTRY.register("bamboo_porkchop_soup", BambooPorkchopSoupItem::new);
	public static final DeferredItem<Item> MUSIC_DISC_HOT_ICE = REGISTRY.register("music_disc_hot_ice", MusicDiscHotIceItem::new);
	public static final DeferredItem<Item> COOKED_RICE = REGISTRY.register("cooked_rice", CookedRiceItem::new);
	public static final DeferredItem<Item> HANAMI_DANGO = REGISTRY.register("hanami_dango", HanamiDangoItem::new);
	public static final DeferredItem<Item> SALMON_SUSHI = REGISTRY.register("salmon_sushi", SalmonSushiItem::new);
	public static final DeferredItem<Item> COD_SUSHI = REGISTRY.register("cod_sushi", CodSushiItem::new);
	public static final DeferredItem<Item> TROPICAL_SUSHI = REGISTRY.register("tropical_sushi", TropicalSushiItem::new);
	public static final DeferredItem<Item> GREEN_ONION = REGISTRY.register("green_onion", GreenOnionItem::new);
	public static final DeferredItem<Item> WILD_BEETROOT = block(ConcoctionModBlocks.WILD_BEETROOT);
	public static final DeferredItem<Item> CHICKEN_CONFIT = REGISTRY.register("chicken_confit", ChickenConfitItem::new);
	public static final DeferredItem<Item> ANIMAL_FAT = REGISTRY.register("animal_fat", AnimalFatItem::new);
	public static final DeferredItem<Item> WILD_POTATO = block(ConcoctionModBlocks.WILD_POTATO);
	public static final DeferredItem<Item> CORN_OIL = REGISTRY.register("corn_oil", CornOilItem::new);
	public static final DeferredItem<Item> BUTTER_CHURN = block(ConcoctionModBlocks.BUTTER_CHURN);
	public static final DeferredItem<Item> OMURICE = REGISTRY.register("omurice", OmuriceItem::new);
	public static final DeferredItem<Item> MUSHROOM_CREAM_SOUP = REGISTRY.register("mushroom_cream_soup", MushroomCreamSoupItem::new);
	public static final DeferredItem<Item> VEGETABLE_SOUP = REGISTRY.register("vegetable_soup", VegetableSoupItem::new);
	public static final DeferredItem<Item> DOUGH = REGISTRY.register("dough", DoughItem::new);
	public static final DeferredItem<Item> RAW_NOODLES = REGISTRY.register("raw_noodles", RawNoodlesItem::new);
	public static final DeferredItem<Item> BUTTER_SANDWICH = REGISTRY.register("butter_sandwich", ButterSandwichItem::new);
	public static final DeferredItem<Item> CABBAGE_LEAF = REGISTRY.register("cabbage_leaf", CabbageLeafItem::new);
	public static final DeferredItem<Item> CABBAGE_BLOCK = block(ConcoctionModBlocks.CABBAGE_BLOCK);
	public static final DeferredItem<Item> CABBAGEHEAD = block(ConcoctionModBlocks.CABBAGEHEAD);
	public static final DeferredItem<Item> WILD_CABBAGE = block(ConcoctionModBlocks.WILD_CABBAGE);
	public static final DeferredItem<Item> TOMATO = REGISTRY.register("tomato", TomatoItem::new);
	public static final DeferredItem<Item> WILD_TOMATO = block(ConcoctionModBlocks.WILD_TOMATO);
	public static final DeferredItem<Item> TOMATO_SOUP = REGISTRY.register("tomato_soup", TomatoSoupItem::new);
	public static final DeferredItem<Item> CORN_BLOCK = block(ConcoctionModBlocks.CORN_BLOCK);
	public static final DeferredItem<Item> SPICY_PEPPER_BLOCK = block(ConcoctionModBlocks.SPICY_PEPPER_BLOCK);
	public static final DeferredItem<Item> ONION_BLOCK = block(ConcoctionModBlocks.ONION_BLOCK);
	public static final DeferredItem<Item> GREEN_ONION_BLOCK = block(ConcoctionModBlocks.GREEN_ONION_BLOCK);
	public static final DeferredItem<Item> COTTON_BLOCK = block(ConcoctionModBlocks.COTTON_BLOCK);
	public static final DeferredItem<Item> REAPPER = REGISTRY.register("reapper", ReapperItem::new);
	public static final DeferredItem<Item> SOULLAND = block(ConcoctionModBlocks.SOULLAND);
	public static final DeferredItem<Item> WEIGHTED_SOULS_BUCKET = REGISTRY.register("weighted_souls_bucket", WeightedSoulsItem::new);
	public static final DeferredItem<Item> SOUL_ICE = block(ConcoctionModBlocks.SOUL_ICE);
	public static final DeferredItem<Item> WANDERING_TRADER_CARPET = block(ConcoctionModBlocks.WANDERING_TRADER_CARPET);
	public static final DeferredItem<Item> WHITE_WOVEN_CARPET = block(ConcoctionModBlocks.WHITE_WOVEN_CARPET);
	public static final DeferredItem<Item> LIGHT_GRAY_WOVEN_CARPET = block(ConcoctionModBlocks.LIGHT_GRAY_WOVEN_CARPET);
	public static final DeferredItem<Item> GRAY_WOVEN_CARPET = block(ConcoctionModBlocks.GRAY_WOVEN_CARPET);
	public static final DeferredItem<Item> BLACK_WOVEN_CARPET = block(ConcoctionModBlocks.BLACK_WOVEN_CARPET);
	public static final DeferredItem<Item> BROWN_WOVEN_CARPET = block(ConcoctionModBlocks.BROWN_WOVEN_CARPET);
	public static final DeferredItem<Item> RED_WOVEN_CARPET = block(ConcoctionModBlocks.RED_WOVEN_CARPET);
	public static final DeferredItem<Item> ORANGE_WOVEN_CARPET = block(ConcoctionModBlocks.ORANGE_WOVEN_CARPET);
	public static final DeferredItem<Item> YELLOW_WOVEN_CARPET = block(ConcoctionModBlocks.YELLOW_WOVEN_CARPET);
	public static final DeferredItem<Item> LIME_WOVEN_CARPET = block(ConcoctionModBlocks.LIME_WOVEN_CARPET);
	public static final DeferredItem<Item> GREEN_WOVEN_CARPET = block(ConcoctionModBlocks.GREEN_WOVEN_CARPET);
	public static final DeferredItem<Item> CYAN_WOVEN_CARPET = block(ConcoctionModBlocks.CYAN_WOVEN_CARPET);
	public static final DeferredItem<Item> LIGHT_BLUE_WOVEN_CARPET = block(ConcoctionModBlocks.LIGHT_BLUE_WOVEN_CARPET);
	public static final DeferredItem<Item> BLUE_WOVEN_CARPET = block(ConcoctionModBlocks.BLUE_WOVEN_CARPET);
	public static final DeferredItem<Item> PURPLE_WOVEN_CARPET = block(ConcoctionModBlocks.PURPLE_WOVEN_CARPET);
	public static final DeferredItem<Item> MAGENTA_WOVEN_CARPET = block(ConcoctionModBlocks.MAGENTA_WOVEN_CARPET);
	public static final DeferredItem<Item> PINK_WOVEN_CARPET = block(ConcoctionModBlocks.PINK_WOVEN_CARPET);
	public static final DeferredItem<Item> CABBAGE_LEAVES_BLOCK = block(ConcoctionModBlocks.CABBAGE_LEAVES_BLOCK);
	public static final DeferredItem<Item> SUNSTRUCK_SPAWN_EGG = REGISTRY.register("sunstruck_spawn_egg", () -> new DeferredSpawnEggItem(ConcoctionModEntities.SUNSTRUCK, -10458315, -4739989, new Item.Properties()));
	public static final DeferredItem<Item> SUNFLOWER_CROWN_HELMET = REGISTRY.register("sunflower_crown_helmet", SunflowerCrownItem.Helmet::new);
	public static final DeferredItem<Item> OVERGROWN_PICKAXE = REGISTRY.register("overgrown_pickaxe", OvergrownPickaxeItem::new);
	public static final DeferredItem<Item> OVERGROWN_AXE = REGISTRY.register("overgrown_axe", OvergrownAxeItem::new);
	public static final DeferredItem<Item> OVERGROWN_SHOVEL = REGISTRY.register("overgrown_shovel", OvergrownShovelItem::new);
	public static final DeferredItem<Item> OVERGROWN_HOE = REGISTRY.register("overgrown_hoe", OvergrownHoeItem::new);
	public static final DeferredItem<Item> OVERGROWN_SWORD = REGISTRY.register("overgrown_sword", OvergrownSwordItem::new);
	public static final DeferredItem<Item> SUNFLOWER_SEED_BLOCK = block(ConcoctionModBlocks.SUNFLOWER_SEED_BLOCK);
	public static final DeferredItem<Item> TOMATO_BLOCK = block(ConcoctionModBlocks.TOMATO_BLOCK);
	public static final DeferredItem<Item> RICE_BLOCK = block(ConcoctionModBlocks.RICE_BLOCK);
	public static final DeferredItem<Item> SOAKED_RICE_BLOCK = block(ConcoctionModBlocks.SOAKED_RICE_BLOCK);
	public static final DeferredItem<Item> MINT_BALE = block(ConcoctionModBlocks.MINT_BALE);
	public static final DeferredItem<Item> PINECONE_BLOCK = block(ConcoctionModBlocks.PINECONE_BLOCK);
	public static final DeferredItem<Item> REAPEPPER_BLOCK = block(ConcoctionModBlocks.REAPEPPER_BLOCK);
	public static final DeferredItem<Item> CHERRY_BLOCK = block(ConcoctionModBlocks.CHERRY_BLOCK);
	public static final DeferredItem<Item> SEA_SALT = REGISTRY.register("sea_salt", SeaSaltItem::new);
	public static final DeferredItem<Item> SWEET_BERRIES_BLOCK = block(ConcoctionModBlocks.SWEET_BERRIES_BLOCK);
	public static final DeferredItem<Item> BEETROOT_BLOCK = block(ConcoctionModBlocks.BEETROOT_BLOCK);
	public static final DeferredItem<Item> CARROT_BLOCK = block(ConcoctionModBlocks.CARROT_BLOCK);
	public static final DeferredItem<Item> POTATO_BLOCK = block(ConcoctionModBlocks.POTATO_BLOCK);
	public static final DeferredItem<Item> GLOW_BERRIES_BLOCK = block(ConcoctionModBlocks.GLOW_BERRIES_BLOCK);
	public static final DeferredItem<Item> CHORUS_BLOCK = block(ConcoctionModBlocks.CHORUS_BLOCK);
	public static final DeferredItem<Item> CAKE_SLICE = REGISTRY.register("cake_slice", CakeSliceItem::new);
	public static final DeferredItem<Item> MINT_CHOCOLATE_CAKE_SLICE = REGISTRY.register("mint_chocolate_cake_slice", MintChocolateCakeSliceItem::new);
	public static final DeferredItem<Item> CHERRY_CAKE_SLICE = REGISTRY.register("cherry_cake_slice", CherryCakeSliceItem::new);
	public static final DeferredItem<Item> LINGONBERRY_CAKE_SLICE = REGISTRY.register("lingonberry_cake_slice", LingonberryCakeSliceItem::new);
	public static final DeferredItem<Item> CHOCOLATE_CAKE_SLICE = REGISTRY.register("chocolate_cake_slice", ChocolateCakeSliceItem::new);
	public static final DeferredItem<Item> GLOWBERRY_CAKE_SLICE = REGISTRY.register("glowberry_cake_slice", GlowberryCakeSliceItem::new);
	public static final DeferredItem<Item> CARROT_CAKE_SLICE = REGISTRY.register("carrot_cake_slice", CarrotCakeSliceItem::new);
	public static final DeferredItem<Item> PUMPKIN_PIE_SLICE = REGISTRY.register("pumpkin_pie_slice", PumpkinPieSliceItem::new);
	public static final DeferredItem<Item> OVEN = block(ConcoctionModBlocks.OVEN);
	public static final DeferredItem<Item> SEA_SALT_BLOCK = block(ConcoctionModBlocks.SEA_SALT_BLOCK);
	public static final DeferredItem<Item> ROCK_SALT_BLOCK = block(ConcoctionModBlocks.ROCK_SALT_BLOCK);
	public static final DeferredItem<Item> ROCK_SALT = REGISTRY.register("rock_salt", RockSaltItem::new);
	public static final DeferredItem<Item> MILK_BOTTLE = REGISTRY.register("milk_bottle", MilkBottleItem::new);
	public static final DeferredItem<Item> CREAM_BOTTLE = REGISTRY.register("cream_bottle", CreamBottleItem::new);
	public static final DeferredItem<Item> FLOUR = REGISTRY.register("flour", FlourItem::new);
	public static final DeferredItem<Item> BOILED_EGG = REGISTRY.register("boiled_egg", BoiledEggItem::new);
	public static final DeferredItem<Item> PUFFBALL = REGISTRY.register("puffball", PuffballItem::new);
	public static final DeferredItem<Item> OAK_KITCHEN_CABINET = block(ConcoctionModBlocks.OAK_KITCHEN_CABINET);
	public static final DeferredItem<Item> SPRUCE_KITCHEN_CABINET = block(ConcoctionModBlocks.SPRUCE_KITCHEN_CABINET);
	public static final DeferredItem<Item> BIRCH_KITCHEN_CABINET = block(ConcoctionModBlocks.BIRCH_KITCHEN_CABINET);
	public static final DeferredItem<Item> ACACIA_KITCHEN_CABINET = block(ConcoctionModBlocks.ACACIA_KITCHEN_CABINET);
	public static final DeferredItem<Item> JUNGLE_KITCHEN_CABINET = block(ConcoctionModBlocks.JUNGLE_KITCHEN_CABINET);
	public static final DeferredItem<Item> COOKED_PUFFBALL = REGISTRY.register("cooked_puffball", CookedPuffballItem::new);
	public static final DeferredItem<Item> FRIED_PUFFBALLS = REGISTRY.register("fried_puffballs", FriedPuffballsItem::new);
	public static final DeferredItem<Item> PUFFBALL_SOUP = REGISTRY.register("puffball_soup", PuffballSoupItem::new);
	public static final DeferredItem<Item> BOILED_NOODLES = REGISTRY.register("boiled_noodles", BoiledNoodlesItem::new);
	public static final DeferredItem<Item> NOODLES_WITH_MEATBALLS = REGISTRY.register("noodles_with_meatballs", NoodlesWithMeatballsItem::new);
    public static final DeferredItem<Item> CINNAMON_BARK = REGISTRY.register("cinnamon_bark", CinnamonBarkItem::new);
    public static final DeferredItem<Item> CINNAMON = REGISTRY.register("cinnamon", CinnamonItem::new);
	public static final DeferredItem<Item> DARK_OAK_KITCHEN_CABINET = block(ConcoctionModBlocks.DARK_OAK_KITCHEN_CABINET);
	public static final DeferredItem<Item> MANGROVE_KITCHEN_CABINET = block(ConcoctionModBlocks.MANGROVE_KITCHEN_CABINET);
	public static final DeferredItem<Item> CHERRY_KITCHEN_CABINET = block(ConcoctionModBlocks.CHERRY_KITCHEN_CABINET);
	public static final DeferredItem<Item> BAMBOO_KITCHEN_CABINET = block(ConcoctionModBlocks.BAMBOO_KITCHEN_CABINET);
	public static final DeferredItem<Item> CRIMSON_KITCHEN_CABINET = block(ConcoctionModBlocks.CRIMSON_KITCHEN_CABINET);
	public static final DeferredItem<Item> WARPED_KITCHEN_CABINET = block(ConcoctionModBlocks.WARPED_KITCHEN_CABINET);
	public static final DeferredItem<Item> CINNAMON_KITCHEN_CABINET = block(ConcoctionModBlocks.CINNAMON_KITCHEN_CABINET);
    public static final DeferredItem<Item> CINNAMON_SAPLING = block(ConcoctionModBlocks.CINNAMON_SAPLING);
    public static final DeferredItem<Item> CINNAMON_LEAVES = block(ConcoctionModBlocks.CINNAMON_LEAVES);
    public static final DeferredItem<Item> CINNAMON_LOG = block(ConcoctionModBlocks.CINNAMON_LOG);
    public static final DeferredItem<Item> CINNAMON_PLANKS = block(ConcoctionModBlocks.CINNAMON_PLANKS);
    public static final DeferredItem<Item> CINNAMON_WOOD = block(ConcoctionModBlocks.CINNAMON_WOOD);
    public static final DeferredItem<Item> CINNAMON_BARK_LOG = block(ConcoctionModBlocks.CINNAMON_BARK_LOG);
    public static final DeferredItem<Item> CINNAMON_BARK_WOOD = block(ConcoctionModBlocks.CINNAMON_BARK_WOOD);
    public static final DeferredItem<Item> STRIPPED_CINNAMON_WOOD = block(ConcoctionModBlocks.STRIPPED_CINNAMON_WOOD);
    public static final DeferredItem<Item> STRIPPED_CINNAMON_LOG = block(ConcoctionModBlocks.STRIPPED_CINNAMON_LOG);
	public static final DeferredItem<Item> CORDYCEPS_SPIDER_SPAWN_EGG = REGISTRY.register("cordyceps_spider_spawn_egg", () -> new DeferredSpawnEggItem(ConcoctionModEntities.CORDYCEPS_SPIDER, -14280166, -7322299, new Item.Properties()));
	public static final DeferredItem<Item> CORDYCEPS_CAVE_SPIDER_SPAWN_EGG = REGISTRY.register("cordyceps_cave_spider_spawn_egg",
			() -> new DeferredSpawnEggItem(ConcoctionModEntities.CORDYCEPS_CAVE_SPIDER, -15063520, -7978944, new Item.Properties()));
    public static final DeferredItem<Item> CINNAMON_STAIRS = block(ConcoctionModBlocks.CINNAMON_STAIRS);
    public static final DeferredItem<Item> CINNAMON_SLAB = block(ConcoctionModBlocks.CINNAMON_SLAB);
    public static final DeferredItem<Item> CINNAMON_FENCE = block(ConcoctionModBlocks.CINNAMON_FENCE);
    public static final DeferredItem<Item> CINNAMON_FENCE_GATE = block(ConcoctionModBlocks.CINNAMON_FENCE_GATE);
    public static final DeferredItem<Item> CINNAMON_DOOR = block(ConcoctionModBlocks.CINNAMON_DOOR);
    public static final DeferredItem<Item> CINNAMON_TRAPDOOR = block(ConcoctionModBlocks.CINNAMON_TRAPDOOR);
    public static final DeferredItem<Item> CINNAMON_PRESSURE_PLATE = block(ConcoctionModBlocks.CINNAMON_PRESSURE_PLATE);
    public static final DeferredItem<Item> CINNAMON_BUTTON = block(ConcoctionModBlocks.CINNAMON_BUTTON);
    public static final DeferredHolder<Item, Item> CINNAMON_SIGN = REGISTRY.register("cinnamon_sign",
            () -> new SignItem(
                    new Item.Properties().stacksTo(16),               // как у ванильной таблички
                    ConcoctionModBlocks.CINNAMON_SIGN.get(),          // стоячая
                    ConcoctionModBlocks.CINNAMON_WALL_SIGN.get()      // настенная
            ));
    public static final DeferredHolder<Item, Item> CINNAMON_HANGING_SIGN = REGISTRY.register("cinnamon_hanging_sign",
            () -> new HangingSignItem(
                    ConcoctionModBlocks.CINNAMON_HANGING_SIGN.get(),
                    ConcoctionModBlocks.CINNAMON_WALL_HANGING_SIGN.get(),
                    new Item.Properties().stacksTo(16)
            ));
    public static final DeferredItem<Item> NETHER_SLOP = REGISTRY.register("nether_slop", NetherSlopItem::new);
	public static final DeferredItem<Item> NOODLES_WITH_CREAM_SAUCE = REGISTRY.register("noodles_with_cream_sauce", NoodlesWithCreamSauceItem::new);
	public static final DeferredItem<Item> BOILED_POTATO = REGISTRY.register("boiled_potato", BoiledPotatoItem::new);
	public static final DeferredItem<Item> DANDELION_TEA = REGISTRY.register("dandelion_tea", DandelionTeaItem::new);
	public static final DeferredItem<Item> PUFFBALL_BLOCK = block(ConcoctionModBlocks.PUFFBALL_BLOCK);
	public static final DeferredItem<Item> SAPONARIA_ROOT = REGISTRY.register("saponaria_root", SaponariaRootItem::new);
	public static final DeferredItem<Item> SUNFLOWER_SEEDS_BREW = REGISTRY.register("sunflower_seeds_brew", SunflowerSeedsBrewItem::new);
	public static final DeferredItem<Item> HONEY_COB = REGISTRY.register("honey_cob", HoneyCobItem::new);
	public static final DeferredItem<Item> OVERGROWN_ARROW = REGISTRY.register("overgrown_arrow", OvergrownArrowItem::new);
	public static final DeferredItem<Item> KOZINAK = REGISTRY.register("kozinak", KozinakItem::new);
	public static final DeferredItem<Item> BACON_AND_EGGS = REGISTRY.register("bacon_and_eggs", BaconAndEggsItem::new);
	public static final DeferredItem<Item> STIR_FRIED_FILET = REGISTRY.register("stir_fried_filet", StirFriedFiletItem::new);
    public static final DeferredItem<Item> NISHOLDA = REGISTRY.register("nisholda", NisholdaItem::new);
	public static final DeferredItem<Item> SOAP_BLOCK = block(ConcoctionModBlocks.SOAP_BLOCK);
    public static final DeferredItem<Item> APPLE_BLOCK = block(ConcoctionModBlocks.APPLE_BLOCK);
    public static final DeferredItem<Item> MAGMA_EGG = REGISTRY.register("magma_egg", MagmaEggItem::new);
    public static final DeferredItem<Item> BUCKET_CASSEROLE = REGISTRY.register("bucket_casserole", BucketCasseroleItem::new);
    public static final DeferredItem<Item> HOT_CHOCOLATE = REGISTRY.register("hot_chocolate", HotChocolateItem::new);
    public static final DeferredItem<Item> TAHCHIN_BUCKET = REGISTRY.register("tahchin_bucket", TahchinBucketItem::new);
    public static final DeferredItem<Item> TAHCHIN_SLICE = REGISTRY.register("tahchin_slice", TahchinSliceItem::new);
	// Start of user code block custom items
	public static final DeferredItem<BlockItem> SAPONARIA_ITEM = REGISTRY.register("saponaria", () -> new BlockItem(ConcoctionModBlocks.SAPONARIA.get(), new Item.Properties()));
	public static final DeferredItem<Item> SOAP = REGISTRY.register("soap", () -> new ItemNameBlockItem(ConcoctionModBlocks.SOAP_LAYER.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> CHERRY_CAKE = block(ConcoctionModBlocks.CHERRY_CAKE, 1);
	public static final DeferredItem<Item> GLOWBERRY_CAKE = block(ConcoctionModBlocks.GLOWBERRY_CAKE, 1);
	public static final DeferredItem<Item> CHOCOLATE_CAKE = block(ConcoctionModBlocks.CHOCOLATE_CAKE, 1);
	public static final DeferredItem<Item> CARROT_CAKE = block(ConcoctionModBlocks.CARROT_CAKE, 1);
	public static final DeferredItem<Item> LINGONBERRY_CAKE = block(ConcoctionModBlocks.LINGONBERRY_CAKE, 1);
	public static final DeferredItem<Item> REAPPER_SEEDS = REGISTRY.register("reapper_seeds", () -> new ItemNameBlockItem(ConcoctionModBlocks.NETHER_PEPPER_CROP.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> RICE = REGISTRY.register("rice", () -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_RICE.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> MINT_CHOCOLATE_CAKE = block(ConcoctionModBlocks.MINT_CHOCOLATE_CAKE, 1);
    public static final DeferredItem<Item> WEEPING_ONION =
            REGISTRY.register("weeping_onion", WeepingOnionItem::new);
    public static final DeferredItem<Item> TOMATO_SEEDS = REGISTRY.register("tomato_seeds", () -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_TOMATO.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> CABBAGE_SEEDS = REGISTRY.register("cabbage_seeds", () -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_CABBAGE.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> SPICY_PEPPER_SEEDS = REGISTRY.register("spicy_pepper_seeds", () -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_SPICY_PEPPER.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> CORN_SEEDS = REGISTRY.register("corn_seeds", () -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_CORN.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> SUNFLOWER_SEEDS = REGISTRY.register("sunflower_seeds", () -> new ItemNameBlockItem(ConcoctionModBlocks.SUNFLOWER.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> MINT_SEEDS = REGISTRY.register("mint_seeds", () -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_MINT.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> COTTON = REGISTRY.register("cotton", () -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_COTTON.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
	public static final DeferredItem<Item> ONION = REGISTRY.register("onion",
			() -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_ONION.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(3).saturationModifier(0.3f).build())));
	public static final DeferredItem<Item> PUFFBALL_SPORES = REGISTRY.register("puffball_spores", () -> new ItemNameBlockItem(ConcoctionModBlocks.CROP_PUFFBALL.get(), new Item.Properties().stacksTo(64).rarity(Rarity.COMMON)));
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
