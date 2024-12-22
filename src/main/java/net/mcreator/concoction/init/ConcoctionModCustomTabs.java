package net.mcreator.concoction.init;

import net.mcreator.concoction.ConcoctionMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ConcoctionModCustomTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ConcoctionMod.MODID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CONCOCTION = REGISTRY.register("concoction",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.concoction.concoction")).icon(() -> new ItemStack(ConcoctionModBlocks.MINT.get())).displayItems((parameters, tabData) -> {
//Растения, семена, плоды, еда
				tabData.accept(ConcoctionModItems.CORN.get());
				tabData.accept(ConcoctionModItems.CORN_SEEDS.get());
				tabData.accept(ConcoctionModItems.POPCORN.get());
				tabData.accept(ConcoctionModItems.COOKED_CORN.get());
				tabData.accept(ConcoctionModItems.GOLDEN_CORN.get());
				tabData.accept(ConcoctionModItems.SPICY_PEPPER.get());
				tabData.accept(ConcoctionModItems.SPICY_PEPPER_SEEDS.get());
				tabData.accept(ConcoctionModItems.HOT_SAUCE_BOTTLE.get());
				tabData.accept(ConcoctionModItems.ONION.get());
				tabData.accept(ConcoctionModItems.COTTON.get());
				tabData.accept(ConcoctionModItems.FABRIC.get());
				tabData.accept(ConcoctionModItems.SUNFLOWER_SEEDS.get());
				tabData.accept(ConcoctionModItems.ROASTED_SUNFLOWER_SEEDS.get());
				tabData.accept(ConcoctionModBlocks.MINT.get().asItem());
				tabData.accept(ConcoctionModItems.MINT_SEEDS.get());
				tabData.accept(ConcoctionModBlocks.MINT_CHOCOLATE_CAKE.get().asItem());
				tabData.accept(ConcoctionModItems.MINT_COOKIE.get());
				tabData.accept(ConcoctionModItems.MINT_BREW.get());
				tabData.accept(ConcoctionModItems.MINTY_SLIME_JELLY.get());
				tabData.accept(ConcoctionModItems.SWEET_SLIME_JELLY.get());
				tabData.accept(ConcoctionModItems.CHERRY.get());
				tabData.accept(ConcoctionModItems.CHERRY_COOKIE.get());
				tabData.accept(ConcoctionModItems.PINECONE.get());
				tabData.accept(ConcoctionModItems.ROASTED_PINECONE.get());
				tabData.accept(ConcoctionModItems.RICE.get());

				
// Пищевые Материалы
				tabData.accept(ConcoctionModItems.SUNFLOWER_OIL.get());
				tabData.accept(ConcoctionModItems.COTTON_OIL.get());
				tabData.accept(ConcoctionModItems.BUTTER.get());
				tabData.accept(ConcoctionModItems.CHOCOLATE.get());

//Сложная еда
				tabData.accept(ConcoctionModItems.HASHBROWNS.get());
				tabData.accept(ConcoctionModItems.FRIED_EGG.get());
				tabData.accept(ConcoctionModItems.HANAMI_DANGO.get());
				tabData.accept(ConcoctionModItems.COD_SUSHI.get());
				tabData.accept(ConcoctionModItems.SALMON_SUSHI.get());
				tabData.accept(ConcoctionModItems.TROPICAL_SUSHI.get());
//Супы и блюда
				tabData.accept(ConcoctionModItems.CORN_SOUP.get());
				tabData.accept(ConcoctionModItems.ONION_SOUP.get());
				tabData.accept(ConcoctionModItems.FUNGUS_STEW.get());
				tabData.accept(ConcoctionModItems.MEAT_GOULASH.get());
				tabData.accept(ConcoctionModItems.BAMBOO_PORKCHOP_SOUP.get());
				tabData.accept(ConcoctionModItems.FISH_AND_CHIPS.get());
				tabData.accept(ConcoctionModItems.MASHED_POTATOES.get());
				tabData.accept(ConcoctionModItems.COOKED_RICE.get());
				tabData.accept(ConcoctionModItems.COLD_CUTS.get());
//Другое
				tabData.accept(ConcoctionModItems.OBSIDIAN_TEARS_BOTTLE.get());
//Дикие Растения
				tabData.accept(ConcoctionModItems.WILD_COTTON.get());
				tabData.accept(ConcoctionModBlocks.WILD_CARROT.get().asItem());
				tabData.accept(ConcoctionModBlocks.WILD_ONION.get().asItem());
				tabData.accept(ConcoctionModItems.WILD_SPICY_PEPPER.get());
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

				tabData.accept(ConcoctionModItems.SMALL_WHITE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_LIGHT_GRAY_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_GRAY_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_BLACK_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_BROWN_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_RED_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_ORANGE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_YELLOW_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_LIME_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_GREEN_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_CYAN_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_LIGHT_BLUE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_BLUE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_PURPLE_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_MAGENTA_PILLOW_BLOCK.get());
				tabData.accept(ConcoctionModItems.SMALL_PINK_PILLOW_BLOCK.get());
//Инструменты, оружие, броня



//Особое
		
				for (Item toAdd : List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION, Items.TIPPED_ARROW)) {
					tabData.accept(PotionContents.createItemStack(toAdd, ConcoctionModPotions.FLAME), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					tabData.accept(PotionContents.createItemStack(toAdd, ConcoctionModPotions.FLAME_EXTENDED), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					tabData.accept(PotionContents.createItemStack(toAdd, ConcoctionModPotions.SNOWFLAKE), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
					tabData.accept(PotionContents.createItemStack(toAdd, ConcoctionModPotions.SNOWFLAKE_EXTENDED), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

				}
				tabData.accept(ConcoctionModItems.MUSIC_DISC_HOT_ICE.get());


			}).build());
}
