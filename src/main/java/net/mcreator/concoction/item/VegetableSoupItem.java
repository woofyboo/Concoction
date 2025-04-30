package net.mcreator.concoction.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.*;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;

public class VegetableSoupItem extends Item {
	public VegetableSoupItem() {
		super(new Item.Properties().stacksTo(16)
				.component(FOOD_EFFECT.value(), new FoodEffectComponent(FoodEffectType.SWEET, 1, 8, true))
				.component(FOOD_EFFECT_2.value(), new FoodEffectComponent(FoodEffectType.SPICY, 2, 10, true))
				.component(FOOD_EFFECT_3.value(), new FoodEffectComponent(FoodEffectType.MINTY, 1, 12, true))
				.component(FOOD_EFFECT_4.value(), new FoodEffectComponent(FoodEffectType.GLOW, 1, 10, true))
				.component(FOOD_EFFECT_5.value(), new FoodEffectComponent(FoodEffectType.INSTABILITY, 1, 6, true))
				.rarity(Rarity.COMMON)
				.food((new FoodProperties.Builder()).nutrition(6).saturationModifier(1.0f).build()));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = new ItemStack(Items.BOWL);
		super.finishUsingItem(itemstack, world, entity);
		if (itemstack.isEmpty()) {
			return retval;
		} else {
			if (entity instanceof Player player && !player.getAbilities().instabuild) {
				if (!player.getInventory().add(retval))
					player.drop(retval, false);
			}
			return itemstack;
		}
	}
}
