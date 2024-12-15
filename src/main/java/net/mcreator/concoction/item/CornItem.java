
package net.mcreator.concoction.item;

import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT;

public class CornItem extends Item {
	public CornItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).component(FOOD_EFFECT.value(), new FoodEffectComponent("sweet", 1, 30)).
				food((new FoodProperties.Builder()).nutrition(3).saturationModifier(0.3f).build()));
	}
}
