
package net.mcreator.concoction.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;


import static net.mcreator.concoction.init.ConcoctionModDataComponents.*;


public class ReapperItem extends Item {

	//private final int BURN_TIME = 200;

	public ReapperItem() {
		super(new Item.Properties().stacksTo(64).
				component(FOOD_EFFECT.value(), new FoodEffectComponent(FoodEffectType.SPICY, 2, 30, true))
				.rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(4).saturationModifier(0.3f).build()));
	}

//	public int getBurnTime(ItemStack stack) {
//		return BURN_TIME;
//	}
}
