package net.mcreator.concoction.init;

import net.mcreator.concoction.ConcoctionMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ConcoctionModCustomTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ConcoctionMod.MODID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CONCOCTION = REGISTRY.register("concoction",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.concoction.concoction")).icon(() -> new ItemStack(ConcoctionModBlocks.MINT.get())).displayItems((parameters, tabData) -> {
				tabData.accept(ConcoctionModBlocks.MINT.get().asItem());
				tabData.accept(ConcoctionModItems.MINT_SEEDS.get());
				tabData.accept(ConcoctionModBlocks.MINT_CHOCOLATE_CAKE.get().asItem());
				tabData.accept(ConcoctionModItems.MINT_COOKIE.get());
				tabData.accept(ConcoctionModItems.MINT_BREW.get());
				tabData.accept(ConcoctionModItems.CHERRY.get());
				tabData.accept(ConcoctionModItems.COTTON.get());
				tabData.accept(ConcoctionModItems.FABRIC.get());
				
			})

					.build());
}
