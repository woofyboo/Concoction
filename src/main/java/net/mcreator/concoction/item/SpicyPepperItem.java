
package net.mcreator.concoction.item;

import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectComponent;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class SpicyPepperItem extends Item {
	public SpicyPepperItem() {
		super(new Item.Properties().stacksTo(64)
				.rarity(Rarity.COMMON)
				.food((new FoodProperties.Builder()).nutrition(3).saturationModifier(0.3f).build())
				.component(
						ConcoctionModDataComponents.FOOD_PASSIVE_EFFECTS.get(),
						List.of(FoodPassiveEffectComponent.of(FoodPassiveEffectType.SPICINESS))
				));
	}
}

