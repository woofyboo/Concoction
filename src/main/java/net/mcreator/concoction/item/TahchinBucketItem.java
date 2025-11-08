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

public class TahchinBucketItem extends TastefulItem {
	public TahchinBucketItem() {
		super(new Item.Properties().stacksTo(16)
				.rarity(Rarity.COMMON)
				.component(FOOD_EFFECT.value(), new FoodEffectComponent(FoodEffectType.HEAL, 4, 300, true))
                .component(FOOD_EFFECT_2.value(), new FoodEffectComponent(FoodEffectType.WARM, 1, 120, true))
				.food((new FoodProperties.Builder()).nutrition(8).saturationModifier(1.2f).build()));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = new ItemStack(Items.BUCKET);
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

    @Override
    public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
        return 48;
    }

    @Override
    public boolean hasCraftingRemainingItem() {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return new ItemStack(Items.BUCKET);
    }
}
