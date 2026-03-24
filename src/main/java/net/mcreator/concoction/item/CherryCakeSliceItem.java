
package net.mcreator.concoction.item;

import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectComponent;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectType;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.LivingEntity;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT;
import static net.mcreator.concoction.init.ConcoctionModDataComponents.*;

import java.util.List;

public class CherryCakeSliceItem extends Item {
	public CherryCakeSliceItem() {
		super(new Item.Properties().stacksTo(64)
		.rarity(Rarity.COMMON)
		.food((new FoodProperties.Builder()).nutrition(3).saturationModifier(0.2f).build())
		.component(
				ConcoctionModDataComponents.FOOD_PASSIVE_EFFECTS.get(),
				List.of(
						FoodPassiveEffectComponent.of(FoodPassiveEffectType.CHERRY_JUICE)
				)
		));
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
		return 24;
	}
}

