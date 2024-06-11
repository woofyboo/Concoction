
package net.mcreator.concoction.recipes.brewing;

import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModBlocks;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MintyBrewCraftBrewingRecipe implements IBrewingRecipe {
	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> BrewingRecipeRegistry.addRecipe(new MintyBrewCraftBrewingRecipe()));
	}

	@Override
	public boolean isInput(ItemStack input) {
		Item inputItem = input.getItem();
		return (inputItem == Items.POTION || inputItem == Items.SPLASH_POTION || inputItem == Items.LINGERING_POTION) && PotionUtils.getPotion(input) == Potions.WATER;
	}

	@Override
	public boolean isIngredient(ItemStack ingredient) {
		return Ingredient.of(new ItemStack(ConcoctionModBlocks.MINT.get())).test(ingredient);
	}

	@Override
	public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
		if (isInput(input) && isIngredient(ingredient)) {
			return new ItemStack(ConcoctionModItems.MINT_BREW.get());
		}
		return ItemStack.EMPTY;
	}
}
