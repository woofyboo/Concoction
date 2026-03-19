
package net.mcreator.concoction.init;

import net.mcreator.concoction.block.entity.*;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.mcreator.concoction.block.entity.CinnamonSignBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.concoction.ConcoctionMod;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ConcoctionModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ConcoctionMod.MODID);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> CROP_MINT = register("crop_mint", ConcoctionModBlocks.CROP_MINT, CropMintBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> OAK_KITCHEN_CABINET = register("oak_kitchen_cabinet", ConcoctionModBlocks.OAK_KITCHEN_CABINET, OakKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> SPRUCE_KITCHEN_CABINET = register("spruce_kitchen_cabinet", ConcoctionModBlocks.SPRUCE_KITCHEN_CABINET, SpruceKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> BIRCH_KITCHEN_CABINET = register("birch_kitchen_cabinet", ConcoctionModBlocks.BIRCH_KITCHEN_CABINET, BirchKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> ACACIA_KITCHEN_CABINET = register("acacia_kitchen_cabinet", ConcoctionModBlocks.ACACIA_KITCHEN_CABINET, AcaciaKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> JUNGLE_KITCHEN_CABINET = register("jungle_kitchen_cabinet", ConcoctionModBlocks.JUNGLE_KITCHEN_CABINET, JungleKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> DARK_OAK_KITCHEN_CABINET = register("dark_oak_kitchen_cabinet", ConcoctionModBlocks.DARK_OAK_KITCHEN_CABINET, DarkOakKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> MANGROVE_KITCHEN_CABINET = register("mangrove_kitchen_cabinet", ConcoctionModBlocks.MANGROVE_KITCHEN_CABINET, MangroveKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> CHERRY_KITCHEN_CABINET = register("cherry_kitchen_cabinet", ConcoctionModBlocks.CHERRY_KITCHEN_CABINET, CherryKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> BAMBOO_KITCHEN_CABINET = register("bamboo_kitchen_cabinet", ConcoctionModBlocks.BAMBOO_KITCHEN_CABINET, BambooKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> CRIMSON_KITCHEN_CABINET = register("crimson_kitchen_cabinet", ConcoctionModBlocks.CRIMSON_KITCHEN_CABINET, CrimsonKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> WARPED_KITCHEN_CABINET = register("warped_kitchen_cabinet", ConcoctionModBlocks.WARPED_KITCHEN_CABINET, WarpedKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> CINNAMON_KITCHEN_CABINET = register("cinnamon_kitchen_cabinet", ConcoctionModBlocks.CINNAMON_KITCHEN_CABINET, CinnamonKitchenCabinetBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> OVEN_BLOCK = register("oven_block", ConcoctionModBlocks.OVEN, OvenBlockEntity::new);
	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> BUTTER_CHURN = register("butter_churn", ConcoctionModBlocks.BUTTER_CHURN, ButterChurnEntity::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CinnamonSignBlockEntity>> CINNAMON_SIGN =
            REGISTRY.register("cinnamon_sign", () ->
                    BlockEntityType.Builder.of(
                            CinnamonSignBlockEntity::new,
                            ConcoctionModBlocks.CINNAMON_SIGN.get(),
                            ConcoctionModBlocks.CINNAMON_WALL_SIGN.get()
                    ).build(null)
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CinnamonHangingSignBlockEntity>> CINNAMON_HANGING_SIGN =
            REGISTRY.register("cinnamon_hanging_sign", () ->
                    BlockEntityType.Builder.of(
                            CinnamonHangingSignBlockEntity::new,
                            ConcoctionModBlocks.CINNAMON_HANGING_SIGN.get(),
                            ConcoctionModBlocks.CINNAMON_WALL_HANGING_SIGN.get()
                    ).build(null)
            );



    private static DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> register(String registryname, Block block, BlockEntityType.BlockEntitySupplier<?> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block).build(null));
	}

	private static DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> register(String registryname, DeferredHolder<Block, Block> block, BlockEntityType.BlockEntitySupplier<?> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, CROP_MINT.get(), (blockEntity, side) -> ((CropMintBlockEntity) blockEntity).getItemHandler());
		registerKitchenCabinetCapability(event, OAK_KITCHEN_CABINET);
		registerKitchenCabinetCapability(event, SPRUCE_KITCHEN_CABINET);
		registerKitchenCabinetCapability(event, BIRCH_KITCHEN_CABINET);
		registerKitchenCabinetCapability(event, ACACIA_KITCHEN_CABINET);
		registerKitchenCabinetCapability(event, JUNGLE_KITCHEN_CABINET);
		registerKitchenCabinetCapability(event, DARK_OAK_KITCHEN_CABINET);
		registerKitchenCabinetCapability(event, MANGROVE_KITCHEN_CABINET);
		registerKitchenCabinetCapability(event, CHERRY_KITCHEN_CABINET);
		registerKitchenCabinetCapability(event, BAMBOO_KITCHEN_CABINET);
		registerKitchenCabinetCapability(event, CRIMSON_KITCHEN_CABINET);
		registerKitchenCabinetCapability(event, WARPED_KITCHEN_CABINET);
		registerKitchenCabinetCapability(event, CINNAMON_KITCHEN_CABINET);
	}

	private static void registerKitchenCabinetCapability(RegisterCapabilitiesEvent event, DeferredHolder<BlockEntityType<?>, BlockEntityType<?>> blockEntityType) {
		event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, blockEntityType.get(), (blockEntity, side) -> ((AbstractKitchenCabinetBlockEntity) blockEntity).getItemHandler());
	}
}
