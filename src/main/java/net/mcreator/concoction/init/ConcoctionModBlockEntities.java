
/*
*    MCreator note: This file will be REGENERATED on each build.
*/
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.minecraft.client.color.block.BlockColors;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;


import net.mcreator.concoction.block.entity.CropMintBlockEntity;
import net.mcreator.concoction.block.entity.CookingCauldronEntity;
import net.mcreator.concoction.ConcoctionMod;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ConcoctionModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ConcoctionMod.MODID);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> CROP_MINT = register("crop_mint", ConcoctionModBlocks.CROP_MINT, CropMintBlockEntity::new);
	// Start of user code block custom block entities
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> COOKING_CAULDRON = register("cooking_cauldron", Blocks.WATER_CAULDRON, CookingCauldronEntity::new);

	private static DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> register(String registryname, Block block, BlockEntityType.BlockEntitySupplier<?> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block).build(null));
	}

	// End of user code block custom block entities
	private static DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> register(String registryname, DeferredHolder<Block, Block> block, BlockEntityType.BlockEntitySupplier<?> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CROP_MINT.get(), (blockEntity, side) -> ((CropMintBlockEntity) blockEntity).getItemHandler());
	}
}
