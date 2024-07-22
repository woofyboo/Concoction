
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.level.block.Block;

import net.mcreator.concoction.block.PillowBlockBlock;
import net.mcreator.concoction.block.MintChocolateCakeBlock;
import net.mcreator.concoction.block.MintBlock;
import net.mcreator.concoction.block.CropMintBlock;
import net.mcreator.concoction.block.CropCottonBlock;
import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModBlocks {
	public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(ConcoctionMod.MODID);
	public static final DeferredHolder<Block, Block> MINT = REGISTRY.register("mint", MintBlock::new);
	public static final DeferredHolder<Block, Block> CROP_MINT = REGISTRY.register("crop_mint", CropMintBlock::new);
	public static final DeferredHolder<Block, Block> MINT_CHOCOLATE_CAKE = REGISTRY.register("mint_chocolate_cake", MintChocolateCakeBlock::new);
	public static final DeferredHolder<Block, Block> CROP_COTTON = REGISTRY.register("crop_cotton", CropCottonBlock::new);
	public static final DeferredHolder<Block, Block> PILLOW_BLOCK = REGISTRY.register("pillow_block", PillowBlockBlock::new);
	// Start of user code block custom blocks
	// End of user code block custom blocks
}
