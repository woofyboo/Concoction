
package net.mcreator.concoction.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Rarity;

public class GoldenCornItem extends Item {
	public GoldenCornItem() {
		super(new Item.Properties().stacksTo(64)
		.rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(6).saturationModifier(0.8f).alwaysEdible().build()));
	}
}

