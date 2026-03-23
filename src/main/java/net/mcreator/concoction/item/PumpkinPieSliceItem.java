
package net.mcreator.concoction.item;

import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.LivingEntity;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.*;

public class PumpkinPieSliceItem extends Item {
	public PumpkinPieSliceItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(2).saturationModifier(0.6f).build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
		return 24;
	}
}

