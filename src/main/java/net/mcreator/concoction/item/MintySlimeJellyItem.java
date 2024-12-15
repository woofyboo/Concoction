
package net.mcreator.concoction.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;

import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT;

public class MintySlimeJellyItem extends Item {
	public MintySlimeJellyItem() {
		super(new Item.Properties().stacksTo(64).component(FOOD_EFFECT.value(), new FoodEffectComponent("minty", 1, 12)).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(3).saturationModifier(0.6f).build()));
	}
}
