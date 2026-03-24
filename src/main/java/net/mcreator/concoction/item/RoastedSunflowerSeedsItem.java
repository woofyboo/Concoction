package net.mcreator.concoction.item;

import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectComponent;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;

public class RoastedSunflowerSeedsItem extends Item {
	public RoastedSunflowerSeedsItem() {
		super(new Item.Properties()
				.stacksTo(64)
				.rarity(Rarity.COMMON)
				.food((new FoodProperties.Builder()).nutrition(1).saturationModifier(0.3f).build())
				.component(
						ConcoctionModDataComponents.FOOD_PASSIVE_EFFECTS.get(),
						List.of(FoodPassiveEffectComponent.of(FoodPassiveEffectType.LIGHT_SNACK))
				));
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
		return 16;
	}
}
