
package net.mcreator.concoction.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;


import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT;
import net.minecraft.world.entity.LivingEntity;
import static net.mcreator.concoction.init.ConcoctionModDataComponents.*;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;

public class FriedEggItem extends TastefulItem {
	public FriedEggItem() {
		super(new Item.Properties().stacksTo(64)
		.component(FOOD_EFFECT.value(), new FoodEffectComponent(FoodEffectType.BREAKFAST, 1, 90, true))
		.rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(4).saturationModifier(1.2f).build()));
	}
	
	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
		return 32;
	}
}


