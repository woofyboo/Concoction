
/*
*    MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredBlock;

import net.minecraft.world.level.block.Block;

import net.mcreator.concoction.block.WildSpicyPepperBlock;
import net.mcreator.concoction.block.WildPotatoBlock;
import net.mcreator.concoction.block.WildOnionBlock;
import net.mcreator.concoction.block.WildCottonBlock;
import net.mcreator.concoction.block.WildCarrotBlock;
import net.mcreator.concoction.block.WildBeetrootBlock;
import net.mcreator.concoction.block.SunflowerBlock;
import net.mcreator.concoction.block.SmallPillowBlock;
import net.mcreator.concoction.block.PillowBlockBlock;
import net.mcreator.concoction.block.MintChocolateCakeBlock;
import net.mcreator.concoction.block.MintBlock;
import net.mcreator.concoction.block.CropSpicyPepperBlock;
import net.mcreator.concoction.block.CropRiceBlock;
import net.mcreator.concoction.block.CropOnionBlock;
import net.mcreator.concoction.block.CropMintBlock;
import net.mcreator.concoction.block.CropCottonBlock;
import net.mcreator.concoction.block.CropCornBlock;
import net.mcreator.concoction.block.CropCabbageBlock;
import net.mcreator.concoction.block.CabbageBlockBlock;
import net.mcreator.concoction.block.ButterChurnBlock;
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
	// Start of user code block custom blocks
	public static final DeferredBlock<Block> MINT_CHOCOLATE_CAKE = REGISTRY.register("mint_chocolate_cake", MintChocolateCakeBlock::new);
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
