
package net.mcreator.concoction.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.*;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT;

public class HashbrownsItem extends Item {
	public HashbrownsItem() {
		super(new Item.Properties().stacksTo(64)
		.rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(6).saturationModifier(1.2f).build()));
	}
}

