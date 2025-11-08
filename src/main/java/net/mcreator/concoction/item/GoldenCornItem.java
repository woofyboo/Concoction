
package net.mcreator.concoction.item;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.LivingEntity;


import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT;

import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;

public class GoldenCornItem extends TastefulItem {
	public GoldenCornItem() {
		super(new Item.Properties().stacksTo(64)
		.component(FOOD_EFFECT.value(), new FoodEffectComponent(FoodEffectType.HEAL, 1, 30, true))
		.rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(6).saturationModifier(0.8f).alwaysEdible().build()));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide()) {
			_entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 600, 0));
			_entity.addEffect(new MobEffectInstance(ConcoctionModMobEffects.PHOTOSYNTHESIS, 1200, 1));
			_entity.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 600, 1));
		}
		return retval;
	}
}
