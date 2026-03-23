
package net.mcreator.concoction.item;

import net.mcreator.concoction.handlers.BaconAndEggsHabitHandler;
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
import net.mcreator.concoction.item.TastefulItem;

public class BaconAndEggsItem extends TastefulItem {
	public BaconAndEggsItem() {
		super(new Item.Properties().stacksTo(16)
		.component(FOOD_EFFECT.value(), new FoodEffectComponent(FoodEffectType.HEAL, 1, 10, true))
		.component(FOOD_EFFECT_2.value(), new FoodEffectComponent(FoodEffectType.SALTY, 1, 90, true))
		.rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(6).saturationModifier(1.2f).build()));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = new ItemStack(Items.BOWL);
		super.finishUsingItem(itemstack, world, entity);
		if (!world.isClientSide() && entity instanceof Player player) {
			BaconAndEggsHabitHandler.onDishConsumed(player);
		}
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
