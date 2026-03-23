
package net.mcreator.concoction.item;

import net.mcreator.concoction.handlers.SlimeJellyBounceHandler;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT;

public class SweetSlimeJellyItem extends TastefulItem {
	public SweetSlimeJellyItem() {
		super(new Item.Properties().stacksTo(64).component(FOOD_EFFECT.value(), new FoodEffectComponent(FoodEffectType.SWEET, 1, 30, true)).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(3).saturationModifier(0.6f).build()));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack result = super.finishUsingItem(itemstack, world, entity);
		SlimeJellyBounceHandler.grantBounceBonus(entity);
		return result;
	}
}
