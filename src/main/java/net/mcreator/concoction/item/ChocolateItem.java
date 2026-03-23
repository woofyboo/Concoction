
package net.mcreator.concoction.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;

import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;


import static net.mcreator.concoction.init.ConcoctionModDataComponents.*;


public class ChocolateItem extends Item {
	public ChocolateItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(4).saturationModifier(0.6f).build()));
	}
}

