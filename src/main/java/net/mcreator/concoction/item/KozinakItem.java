
package net.mcreator.concoction.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT;
import static net.mcreator.concoction.init.ConcoctionModDataComponents.*;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;

public class KozinakItem extends TastefulItem {
	public KozinakItem() {
		super(new Item.Properties().stacksTo(64)
		.component(FOOD_EFFECT.value(), new FoodEffectComponent(FoodEffectType.PHOTOSYNTHESIS, 1, 180, true))
		.component(FOOD_EFFECT_2.value(), new FoodEffectComponent(FoodEffectType.SWEET, 2, 30, true))
		.rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(6).saturationModifier(0.6f).build()));
	}
}
