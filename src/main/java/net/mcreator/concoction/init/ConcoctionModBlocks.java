
/*
*    MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredBlock;

import net.minecraft.world.level.block.Block;

import net.mcreator.concoction.block.WildCottonBlock;
import net.mcreator.concoction.block.PillowBlockBlock;
import net.mcreator.concoction.block.MintChocolateCakeBlock;
import net.mcreator.concoction.block.MintBlock;
import net.mcreator.concoction.block.CropMintBlock;
import net.mcreator.concoction.block.CropCottonBlock;
import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModBlocks {
	public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(ConcoctionMod.MODID);
	public static final DeferredBlock<Block> MINT = REGISTRY.register("mint", MintBlock::new);
	public static final DeferredBlock<Block> CROP_MINT = REGISTRY.register("crop_mint", CropMintBlock::new);
	public static final DeferredBlock<Block> MINT_CHOCOLATE_CAKE = REGISTRY.register("mint_chocolate_cake", MintChocolateCakeBlock::new);
	public static final DeferredBlock<Block> CROP_COTTON = REGISTRY.register("crop_cotton", CropCottonBlock::new);
	public static final DeferredBlock<Block> PILLOW_BLOCK = REGISTRY.register("pillow_block", PillowBlockBlock::new);
	public static final DeferredBlock<Block> WILD_COTTON = REGISTRY.register("wild_cotton", WildCottonBlock::new);
	// Start of user code block custom blocks
	public static final DeferredHolder<Block, Block> RED_PILLOW_BLOCK = REGISTRY.register("red_pillow_block", PillowBlockBlock::new);
	public static final DeferredHolder<Block, Block> ORANGE_PILLOW_BLOCK = REGISTRY.register("orange_pillow_block", PillowBlockBlock::new);
	public static final DeferredHolder<Block, Block> BROWN_PILLOW_BLOCK = REGISTRY.register("brown_pillow_block", PillowBlockBlock::new);
	public static final DeferredHolder<Block, Block> YELLOW_PILLOW_BLOCK = REGISTRY.register("yellow_pillow_block", PillowBlockBlock::new);
	public static final DeferredHolder<Block, Block> LIME_PILLOW_BLOCK = REGISTRY.register("lime_pillow_block", PillowBlockBlock::new);
	public static final DeferredHolder<Block, Block> GREEN_PILLOW_BLOCK = REGISTRY.register("green_pillow_block", PillowBlockBlock::new);
	public static final DeferredHolder<Block, Block> CYAN_PILLOW_BLOCK = REGISTRY.register("cyan_pillow_block", PillowBlockBlock::new);
	public static final DeferredHolder<Block, Block> LIGHT_BLUE_PILLOW_BLOCK = REGISTRY.register("light_blue_pillow_block", PillowBlockBlock::new);
	public static final DeferredHolder<Block, Block> BLUE_PILLOW_BLOCK = REGISTRY.register("blue_pillow_block", PillowBlockBlock::new);
	public static final DeferredHolder<Block, Block> PURPLE_PILLOW_BLOCK = REGISTRY.register("purple_pillow_block", PillowBlockBlock::new);
	public static final DeferredHolder<Block, Block> MAGENTA_PILLOW_BLOCK = REGISTRY.register("magenta_pillow_block", PillowBlockBlock::new);
	public static final DeferredHolder<Block, Block> PINK_PILLOW_BLOCK = REGISTRY.register("pink_pillow_block", PillowBlockBlock::new);
	public static final DeferredHolder<Block, Block> LIGHT_GRAY_PILLOW_BLOCK = REGISTRY.register("light_gray_pillow_block", PillowBlockBlock::new);
	public static final DeferredHolder<Block, Block> GRAY_PILLOW_BLOCK = REGISTRY.register("gray_pillow_block", PillowBlockBlock::new);
	public static final DeferredHolder<Block, Block> BLACK_PILLOW_BLOCK = REGISTRY.register("black_pillow_block", PillowBlockBlock::new);
	// End of user code block custom blocks
}
