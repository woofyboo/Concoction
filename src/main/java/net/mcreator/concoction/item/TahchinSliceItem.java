
package net.mcreator.concoction.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class TahchinSliceItem extends Item {
	public TahchinSliceItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(2).saturationModifier(1.2f).build()));
	}

	@Override
	public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
		return 24;
	}
}

