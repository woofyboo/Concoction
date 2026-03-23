
package net.mcreator.concoction.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class SpicyPepperItem extends Item {
	public SpicyPepperItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(3).saturationModifier(0.3f).build()));
	}
}

