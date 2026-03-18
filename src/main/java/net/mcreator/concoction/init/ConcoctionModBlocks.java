
/*
*    MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.concoction.init;

import net.mcreator.concoction.block.*;
import net.mcreator.concoction.worldgen.CinnamonTreeGrower;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModBlocks {
	public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(ConcoctionMod.MODID);
	public static final DeferredBlock<Block> MINT = REGISTRY.register("mint", MintBlock::new);
	public static final DeferredBlock<Block> CROP_MINT = REGISTRY.register("crop_mint", CropMintBlock::new);
	public static final DeferredBlock<Block> CROP_COTTON = REGISTRY.register("crop_cotton", CropCottonBlock::new);
	public static final DeferredBlock<Block> PILLOW_BLOCK = REGISTRY.register("pillow_block", PillowBlockBlock::new);
	public static final DeferredBlock<Block> WILD_COTTON = REGISTRY.register("wild_cotton", WildCottonBlock::new);
	public static final DeferredBlock<Block> SUNFLOWER = REGISTRY.register("sunflower", SunflowerBlock::new);
	public static final DeferredBlock<Block> WILD_CARROT = REGISTRY.register("wild_carrot", WildCarrotBlock::new);
	public static final DeferredBlock<Block> CROP_ONION = REGISTRY.register("crop_onion", CropOnionBlock::new);
	public static final DeferredBlock<Block> WILD_ONION = REGISTRY.register("wild_onion", WildOnionBlock::new);
	public static final DeferredBlock<Block> CROP_CORN = REGISTRY.register("crop_corn", CropCornBlock::new);
	public static final DeferredBlock<Block> CROP_SPICY_PEPPER = REGISTRY.register("crop_spicy_pepper", CropSpicyPepperBlock::new);
	public static final DeferredBlock<Block> WILD_SPICY_PEPPER = REGISTRY.register("wild_spicy_pepper", WildSpicyPepperBlock::new);
	public static final DeferredBlock<Block> CROP_RICE = REGISTRY.register("crop_rice", CropRiceBlock::new);
	public static final DeferredBlock<Block> WILD_BEETROOT = REGISTRY.register("wild_beetroot", WildBeetrootBlock::new);
	public static final DeferredBlock<Block> WILD_POTATO = REGISTRY.register("wild_potato", WildPotatoBlock::new);
	public static final DeferredBlock<Block> BUTTER_CHURN = REGISTRY.register("butter_churn", ButterChurnBlock::new);
	public static final DeferredBlock<Block> CROP_CABBAGE = REGISTRY.register("crop_cabbage", CropCabbageBlock::new);
	public static final DeferredBlock<Block> CABBAGE_BLOCK = REGISTRY.register("cabbage_block", CabbageBlockBlock::new);
	public static final DeferredBlock<Block> CABBAGEHEAD = REGISTRY.register("cabbagehead", CabbageheadBlock::new);
	public static final DeferredBlock<Block> WILD_CABBAGE = REGISTRY.register("wild_cabbage", WildCabbageBlock::new);
	public static final DeferredBlock<Block> CROP_TOMATO = REGISTRY.register("crop_tomato", CropTomatoBlock::new);
	public static final DeferredBlock<Block> WILD_TOMATO = REGISTRY.register("wild_tomato", WildTomatoBlock::new);
	public static final DeferredBlock<Block> CORN_BLOCK = REGISTRY.register("corn_block", CornBlockBlock::new);
	public static final DeferredBlock<Block> SPICY_PEPPER_BLOCK = REGISTRY.register("spicy_pepper_block", SpicyPepperBlockBlock::new);
	public static final DeferredBlock<Block> ONION_BLOCK = REGISTRY.register("onion_block", OnionBlockBlock::new);
	public static final DeferredBlock<Block> GREEN_ONION_BLOCK = REGISTRY.register("green_onion_block", GreenOnionBlockBlock::new);
	public static final DeferredBlock<Block> COTTON_BLOCK = REGISTRY.register("cotton_block", CottonBlockBlock::new);
	public static final DeferredBlock<Block> NETHER_PEPPER_CROP = REGISTRY.register("nether_pepper_crop", NetherPepperCropBlock::new);
	public static final DeferredBlock<Block> SOULLAND = REGISTRY.register("soulland", SoullandBlock::new);
	public static final DeferredBlock<Block> WEIGHTED_SOULS = REGISTRY.register("weighted_souls", WeightedSoulsBlock::new);
	public static final DeferredBlock<Block> SOUL_ICE = REGISTRY.register("soul_ice", SoulIceBlock::new);
	public static final DeferredBlock<Block> WANDERING_TRADER_CARPET = REGISTRY.register("wandering_trader_carpet", WanderingTraderCarpetBlock::new);
	public static final DeferredBlock<Block> WHITE_WOVEN_CARPET = REGISTRY.register("white_woven_carpet", WhiteWovenCarpetBlock::new);
	public static final DeferredBlock<Block> LIGHT_GRAY_WOVEN_CARPET = REGISTRY.register("light_gray_woven_carpet", LightGrayWovenCarpetBlock::new);
	public static final DeferredBlock<Block> GRAY_WOVEN_CARPET = REGISTRY.register("gray_woven_carpet", GrayWovenCarpetBlock::new);
	public static final DeferredBlock<Block> BLACK_WOVEN_CARPET = REGISTRY.register("black_woven_carpet", BlackWovenCarpetBlock::new);
	public static final DeferredBlock<Block> BROWN_WOVEN_CARPET = REGISTRY.register("brown_woven_carpet", BrownWovenCarpetBlock::new);
	public static final DeferredBlock<Block> RED_WOVEN_CARPET = REGISTRY.register("red_woven_carpet", RedWovenCarpetBlock::new);
	public static final DeferredBlock<Block> ORANGE_WOVEN_CARPET = REGISTRY.register("orange_woven_carpet", OrangeWovenCarpetBlock::new);
	public static final DeferredBlock<Block> YELLOW_WOVEN_CARPET = REGISTRY.register("yellow_woven_carpet", YellowWovenCarpetBlock::new);
	public static final DeferredBlock<Block> LIME_WOVEN_CARPET = REGISTRY.register("lime_woven_carpet", LimeWovenCarpetBlock::new);
	public static final DeferredBlock<Block> GREEN_WOVEN_CARPET = REGISTRY.register("green_woven_carpet", GreenWovenCarpetBlock::new);
	public static final DeferredBlock<Block> CYAN_WOVEN_CARPET = REGISTRY.register("cyan_woven_carpet", CyanWovenCarpetBlock::new);
	public static final DeferredBlock<Block> LIGHT_BLUE_WOVEN_CARPET = REGISTRY.register("light_blue_woven_carpet", LightBlueWovenCarpetBlock::new);
	public static final DeferredBlock<Block> BLUE_WOVEN_CARPET = REGISTRY.register("blue_woven_carpet", BlueWovenCarpetBlock::new);
	public static final DeferredBlock<Block> PURPLE_WOVEN_CARPET = REGISTRY.register("purple_woven_carpet", PurpleWovenCarpetBlock::new);
	public static final DeferredBlock<Block> MAGENTA_WOVEN_CARPET = REGISTRY.register("magenta_woven_carpet", MagentaWovenCarpetBlock::new);
	public static final DeferredBlock<Block> PINK_WOVEN_CARPET = REGISTRY.register("pink_woven_carpet", PinkWovenCarpetBlock::new);
	public static final DeferredBlock<Block> CABBAGE_LEAVES_BLOCK = REGISTRY.register("cabbage_leaves_block", CabbageLeavesBlockBlock::new);
	public static final DeferredBlock<Block> SUNFLOWER_SEED_BLOCK = REGISTRY.register("sunflower_seed_block", SunflowerSeedBlockBlock::new);
	public static final DeferredBlock<Block> TOMATO_BLOCK = REGISTRY.register("tomato_block", TomatoBlockBlock::new);
	public static final DeferredBlock<Block> RICE_BLOCK = REGISTRY.register("rice_block", RiceBlockBlock::new);
	public static final DeferredBlock<Block> SOAKED_RICE_BLOCK = REGISTRY.register("soaked_rice_block", SoakedRiceBlockBlock::new);
	public static final DeferredBlock<Block> MINT_BALE = REGISTRY.register("mint_bale", MintBaleBlock::new);
	public static final DeferredBlock<Block> PINECONE_BLOCK = REGISTRY.register("pinecone_block", PineconeBlockBlock::new);
	public static final DeferredBlock<Block> REAPEPPER_BLOCK = REGISTRY.register("reapepper_block", ReapepperBlockBlock::new);
	public static final DeferredBlock<Block> CHERRY_BLOCK = REGISTRY.register("cherry_block", CherryBlockBlock::new);
	public static final DeferredBlock<Block> SWEET_BERRIES_BLOCK = REGISTRY.register("sweet_berries_block", SweetBerriesBlockBlock::new);
	public static final DeferredBlock<Block> BEETROOT_BLOCK = REGISTRY.register("beetroot_block", BeetrootBlockBlock::new);
	public static final DeferredBlock<Block> CARROT_BLOCK = REGISTRY.register("carrot_block", CarrotBlockBlock::new);
	public static final DeferredBlock<Block> POTATO_BLOCK = REGISTRY.register("potato_block", PotatoBlockBlock::new);
	public static final DeferredBlock<Block> GLOW_BERRIES_BLOCK = REGISTRY.register("glow_berries_block", GlowBerriesBlockBlock::new);
	public static final DeferredBlock<Block> CHORUS_BLOCK = REGISTRY.register("chorus_block", ChorusBlockBlock::new);
	public static final DeferredBlock<Block> OVEN = REGISTRY.register("oven", OvenBlock::new);
	public static final DeferredBlock<Block> SEA_SALT_BLOCK = REGISTRY.register("sea_salt_block", SeaSaltBlockBlock::new);
	public static final DeferredBlock<Block> ROCK_SALT_BLOCK = REGISTRY.register("rock_salt_block", RockSaltBlockBlock::new);
	public static final DeferredBlock<Block> CROP_PUFFBALL = REGISTRY.register("crop_puffball", CropPuffballBlock::new);
	public static final DeferredBlock<Block> OAK_KITCHEN_CABINET = REGISTRY.register("oak_kitchen_cabinet", OakKitchenCabinetBlock::new);
	public static final DeferredBlock<Block> SPRUCE_KITCHEN_CABINET = REGISTRY.register("spruce_kitchen_cabinet", SpruceKitchenCabinetBlock::new);
	public static final DeferredBlock<Block> BIRCH_KITCHEN_CABINET = REGISTRY.register("birch_kitchen_cabinet", BirchKitchenCabinetBlock::new);
	public static final DeferredBlock<Block> ACACIA_KITCHEN_CABINET = REGISTRY.register("acacia_kitchen_cabinet", AcaciaKitchenCabinetBlock::new);
	public static final DeferredBlock<Block> JUNGLE_KITCHEN_CABINET = REGISTRY.register("jungle_kitchen_cabinet", JungleKitchenCabinetBlock::new);
	public static final DeferredBlock<Block> DARK_OAK_KITCHEN_CABINET = REGISTRY.register("dark_oak_kitchen_cabinet", DarkOakKitchenCabinetBlock::new);
	public static final DeferredBlock<Block> MANGROVE_KITCHEN_CABINET = REGISTRY.register("mangrove_kitchen_cabinet", MangroveKitchenCabinetBlock::new);
	public static final DeferredBlock<Block> CHERRY_KITCHEN_CABINET = REGISTRY.register("cherry_kitchen_cabinet", CherryKitchenCabinetBlock::new);
	public static final DeferredBlock<Block> BAMBOO_KITCHEN_CABINET = REGISTRY.register("bamboo_kitchen_cabinet", BambooKitchenCabinetBlock::new);
	public static final DeferredBlock<Block> CRIMSON_KITCHEN_CABINET = REGISTRY.register("crimson_kitchen_cabinet", CrimsonKitchenCabinetBlock::new);
	public static final DeferredBlock<Block> WARPED_KITCHEN_CABINET = REGISTRY.register("warped_kitchen_cabinet", WarpedKitchenCabinetBlock::new);
	public static final DeferredBlock<Block> CINNAMON_KITCHEN_CABINET = REGISTRY.register("cinnamon_kitchen_cabinet", CinnamonKitchenCabinetBlock::new);
	public static final DeferredBlock<Block> PUFFBALL_BLOCK = REGISTRY.register("puffball_block", PuffballBlockBlock::new);
	public static final DeferredBlock<Block> SOAP_BLOCK = REGISTRY.register("soap_block", SoapBlockBlock::new);
    public static final DeferredBlock<Block> APPLE_BLOCK = REGISTRY.register("apple_block", AppleBlockBlock::new);
    public static final DeferredBlock<Block> CROP_WEEPING_ONION = REGISTRY.register("crop_weeping_onion", WeepingOnionsCropBlock::new);
    public static final DeferredBlock<Block> CINNAMON_LOG = REGISTRY.register("cinnamon_log", CinnamonLogBlock::new);
    public static final DeferredBlock<Block> CINNAMON_PLANKS = REGISTRY.register("cinnamon_planks", CinnamonLogBlock::new);
    public static final DeferredBlock<Block> CINNAMON_WOOD = REGISTRY.register("cinnamon_wood", CinnamonWoodBlock::new);
    public static final DeferredBlock<Block> CINNAMON_BARK_LOG = REGISTRY.register("cinnamon_bark_log", CinnamonBarkLogBlock::new);
    public static final DeferredBlock<Block> CINNAMON_BARK_WOOD = REGISTRY.register("cinnamon_bark_wood", CinnamonBarkWoodBlock::new);
    public static final DeferredBlock<Block> STRIPPED_CINNAMON_LOG = REGISTRY.register("stripped_cinnamon_log", StrippedCinnamonLogBlock::new);
    public static final DeferredBlock<Block> STRIPPED_CINNAMON_WOOD = REGISTRY.register("stripped_cinnamon_wood", StrippedCinnamonWoodBlock::new);
    public static final DeferredBlock<Block> CINNAMON_LEAVES = REGISTRY.register("cinnamon_leaves", CinnamonLeavesBlock::new);
    public static final DeferredBlock<Block> CINNAMON_SAPLING =
            REGISTRY.register("cinnamon_sapling",
                    () -> new SaplingBlock(
                            CinnamonTreeGrower.CINNAMON,
                            BlockBehaviour.Properties.of()
                                    .noCollission()
                                    .instabreak()
                                    .randomTicks()
                                    .noOcclusion()
                                    .sound(SoundType.GRASS)
                    ));
    public static final DeferredBlock<Block> POTTED_CINNAMON_SAPLING =
            REGISTRY.register("potted_cinnamon_sapling",
                    () -> new FlowerPotBlock(
                            () -> (FlowerPotBlock) Blocks.FLOWER_POT,
                            ConcoctionModBlocks.CINNAMON_SAPLING,
                            BlockBehaviour.Properties.of()
                                    .instabreak()
                                    .noOcclusion()
                                    .sound(SoundType.STONE)
                    ));

    public static final DeferredBlock<Block> CINNAMON_SLAB = REGISTRY.register("cinnamon_slab", CinnamonSlabBlock::new);
    public static final DeferredBlock<Block> CINNAMON_STAIRS = REGISTRY.register("cinnamon_stairs", CinnamonStairsBlock::new);
    public static final DeferredBlock<Block> CINNAMON_FENCE = REGISTRY.register("cinnamon_fence", CinnamonFenceBlock::new);
    public static final DeferredBlock<Block> CINNAMON_FENCE_GATE = REGISTRY.register("cinnamon_fence_gate", CinnamonFenceGateBlock::new);
    public static final DeferredBlock<Block> CINNAMON_DOOR = REGISTRY.register("cinnamon_door", CinnamonDoorBlock::new);
    public static final DeferredBlock<Block> CINNAMON_TRAPDOOR = REGISTRY.register("cinnamon_trapdoor", CinnamonTrapdoorBlock::new);
    public static final DeferredBlock<Block> CINNAMON_PRESSURE_PLATE = REGISTRY.register("cinnamon_pressure_plate", CinnamonPressurePlateBlock::new);
    public static final DeferredBlock<Block> CINNAMON_BUTTON = REGISTRY.register("cinnamon_button", CinnamonButtonBlock::new);
    public static final DeferredBlock<Block> CINNAMON_SIGN =
            REGISTRY.register("cinnamon_sign", CinnamonSignBlock::new);

    public static final DeferredBlock<Block> CINNAMON_WALL_SIGN =
            REGISTRY.register("cinnamon_wall_sign", CinnamonWallSignBlock::new);


    // Start of user code block custom blocks
	public static final DeferredBlock<SaponariaBlock> SAPONARIA = REGISTRY.register("saponaria", () -> new SaponariaBlock());
	public static final DeferredBlock<Block> SOAP_LAYER = REGISTRY.register("soap_layer", SoapLayerBlock::new);
	public static final DeferredBlock<Block> MINT_CHOCOLATE_CAKE = REGISTRY.register("mint_chocolate_cake", MintChocolateCakeBlock::new);
	public static final DeferredBlock<Block> CARROT_CAKE = REGISTRY.register("carrot_cake", CarrotCakeBlock::new);
	public static final DeferredBlock<Block> CHERRY_CAKE = REGISTRY.register("cherry_cake", CherryCakeBlock::new);
	public static final DeferredBlock<Block> GLOWBERRY_CAKE = REGISTRY.register("glowberry_cake", GlowberryCakeBlock::new);
	public static final DeferredBlock<Block> LINGONBERRY_CAKE = REGISTRY.register("lingonberry_cake", LingonberryCakeBlock::new);
	public static final DeferredBlock<Block> CHOCOLATE_CAKE = REGISTRY.register("chocolate_cake", ChocolateCakeBlock::new);
	public static final DeferredBlock<Block> RED_PILLOW_BLOCK = REGISTRY.register("red_pillow_block", PillowBlockBlock::new);
	public static final DeferredBlock<Block> ORANGE_PILLOW_BLOCK = REGISTRY.register("orange_pillow_block", PillowBlockBlock::new);
	public static final DeferredBlock<Block> BROWN_PILLOW_BLOCK = REGISTRY.register("brown_pillow_block", PillowBlockBlock::new);
	public static final DeferredBlock<Block> YELLOW_PILLOW_BLOCK = REGISTRY.register("yellow_pillow_block", PillowBlockBlock::new);
	public static final DeferredBlock<Block> LIME_PILLOW_BLOCK = REGISTRY.register("lime_pillow_block", PillowBlockBlock::new);
	public static final DeferredBlock<Block> GREEN_PILLOW_BLOCK = REGISTRY.register("green_pillow_block", PillowBlockBlock::new);
	public static final DeferredBlock<Block> CYAN_PILLOW_BLOCK = REGISTRY.register("cyan_pillow_block", PillowBlockBlock::new);
	public static final DeferredBlock<Block> LIGHT_BLUE_PILLOW_BLOCK = REGISTRY.register("light_blue_pillow_block", PillowBlockBlock::new);
	public static final DeferredBlock<Block> BLUE_PILLOW_BLOCK = REGISTRY.register("blue_pillow_block", PillowBlockBlock::new);
	public static final DeferredBlock<Block> PURPLE_PILLOW_BLOCK = REGISTRY.register("purple_pillow_block", PillowBlockBlock::new);
	public static final DeferredBlock<Block> MAGENTA_PILLOW_BLOCK = REGISTRY.register("magenta_pillow_block", PillowBlockBlock::new);
	public static final DeferredBlock<Block> PINK_PILLOW_BLOCK = REGISTRY.register("pink_pillow_block", PillowBlockBlock::new);
	public static final DeferredBlock<Block> LIGHT_GRAY_PILLOW_BLOCK = REGISTRY.register("light_gray_pillow_block", PillowBlockBlock::new);
	public static final DeferredBlock<Block> GRAY_PILLOW_BLOCK = REGISTRY.register("gray_pillow_block", PillowBlockBlock::new);
	public static final DeferredBlock<Block> BLACK_PILLOW_BLOCK = REGISTRY.register("black_pillow_block", PillowBlockBlock::new);
	public static final DeferredBlock<Block> SMALL_WHITE_PILLOW_BLOCK = REGISTRY.register("small_white_pillow_block", SmallPillowBlock::new);
	public static final DeferredBlock<Block> SMALL_RED_PILLOW_BLOCK = REGISTRY.register("small_red_pillow_block", SmallPillowBlock::new);
	public static final DeferredBlock<Block> SMALL_ORANGE_PILLOW_BLOCK = REGISTRY.register("small_orange_pillow_block", SmallPillowBlock::new);
	public static final DeferredBlock<Block> SMALL_BROWN_PILLOW_BLOCK = REGISTRY.register("small_brown_pillow_block", SmallPillowBlock::new);
	public static final DeferredBlock<Block> SMALL_YELLOW_PILLOW_BLOCK = REGISTRY.register("small_yellow_pillow_block", SmallPillowBlock::new);
	public static final DeferredBlock<Block> SMALL_LIME_PILLOW_BLOCK = REGISTRY.register("small_lime_pillow_block", SmallPillowBlock::new);
	public static final DeferredBlock<Block> SMALL_GREEN_PILLOW_BLOCK = REGISTRY.register("small_green_pillow_block", SmallPillowBlock::new);
	public static final DeferredBlock<Block> SMALL_CYAN_PILLOW_BLOCK = REGISTRY.register("small_cyan_pillow_block", SmallPillowBlock::new);
	public static final DeferredBlock<Block> SMALL_LIGHT_BLUE_PILLOW_BLOCK = REGISTRY.register("small_light_blue_pillow_block", SmallPillowBlock::new);
	public static final DeferredBlock<Block> SMALL_BLUE_PILLOW_BLOCK = REGISTRY.register("small_blue_pillow_block", SmallPillowBlock::new);
	public static final DeferredBlock<Block> SMALL_PURPLE_PILLOW_BLOCK = REGISTRY.register("small_purple_pillow_block", SmallPillowBlock::new);
	public static final DeferredBlock<Block> SMALL_MAGENTA_PILLOW_BLOCK = REGISTRY.register("small_magenta_pillow_block", SmallPillowBlock::new);
	public static final DeferredBlock<Block> SMALL_PINK_PILLOW_BLOCK = REGISTRY.register("small_pink_pillow_block", SmallPillowBlock::new);
	public static final DeferredBlock<Block> SMALL_LIGHT_GRAY_PILLOW_BLOCK = REGISTRY.register("small_light_gray_pillow_block", SmallPillowBlock::new);
	public static final DeferredBlock<Block> SMALL_GRAY_PILLOW_BLOCK = REGISTRY.register("small_gray_pillow_block", SmallPillowBlock::new);
	public static final DeferredBlock<Block> SMALL_BLACK_PILLOW_BLOCK = REGISTRY.register("small_black_pillow_block", SmallPillowBlock::new);
	// End of user code block custom blocks
}
