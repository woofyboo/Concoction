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
//Семена, Растения, Дикие вариации, Плоды
				tabData.accept(ConcoctionModBlocks.MINT.get().asItem());
				tabData.accept(ConcoctionModItems.MINT_SEEDS.get());
				tabData.accept(ConcoctionModItems.CHERRY.get());
				tabData.accept(ConcoctionModItems.WILD_COTTON.get());
				tabData.accept(ConcoctionModItems.COTTON.get());
				tabData.accept(ConcoctionModItems.SUNFLOWER_SEEDS.get());
// Материалы
				tabData.accept(ConcoctionModItems.FABRIC.get());
//Еда и напитки
				tabData.accept(ConcoctionModBlocks.MINT_CHOCOLATE_CAKE.get().asItem());
				tabData.accept(ConcoctionModItems.MINT_COOKIE.get());
				tabData.accept(ConcoctionModItems.MINT_BREW.get());
				tabData.accept(ConcoctionModItems.CHERRY_COOKIE.get());
				tabData.accept(ConcoctionModItems.ROASTED_SUNFLOWER_SEEDS.get());
//Cтроительные блоки
				tabData.accept(ConcoctionModItems.PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.LIGHT_GRAY_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.GRAY_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.BLACK_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.BROWN_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.RED_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.ORANGE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.YELLOW_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.LIME_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.GREEN_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.CYAN_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.LIGHT_BLUE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.BLUE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.PURPLE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.MAGENTA_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.PINK_PILLOW_BLOCK.get());
//Особое
				tabData.accept(ConcoctionModItems.MUSIC_DISC_HOT_ICE.get());
				
			})

					.build());
}
