
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.concoction.block.MintBlock;
import net.mcreator.concoction.block.CropMintBlock;
import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK, ConcoctionMod.MODID);
	public static final DeferredHolder<Block, Block> MINT = REGISTRY.register("mint", () -> new MintBlock());
	public static final DeferredHolder<Block, Block> CROP_MINT = REGISTRY.register("crop_mint", () -> new CropMintBlock());
	// Start of user code block custom blocks
	// End of user code block custom blocks
}
