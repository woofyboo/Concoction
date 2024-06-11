
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ConcoctionMod.MODID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CONCOCTION = REGISTRY.register("concoction",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.concoction.concoction")).icon(() -> new ItemStack(ConcoctionModBlocks.MINT.get())).displayItems((parameters, tabData) -> {
				tabData.accept(ConcoctionModBlocks.MINT.get().asItem());
				tabData.accept(ConcoctionModItems.MINT_SEEDS.get());
				tabData.accept(ConcoctionModItems.MINT_BREW.get());
				tabData.accept(ConcoctionModItems.CHERRY.get());
			})

					.build());
}
