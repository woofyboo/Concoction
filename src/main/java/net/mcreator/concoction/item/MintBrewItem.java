
package net.mcreator.concoction.item;

import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MintBrewItem extends Item {
	public MintBrewItem() {
		super(new Item.Properties()
				.stacksTo(16)
				.rarity(Rarity.COMMON)
				.food((new FoodProperties.Builder())
						.nutrition(0)
						.saturationModifier(0.3f)
						.alwaysEdible()
						.build())
		);
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.DRINK;
	}

	@Override
	public boolean hasCraftingRemainingItem() {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemstack) {
		return new ItemStack(Items.GLASS_BOTTLE);
	}

	@Override
	public @NotNull SoundEvent getEatingSound() {
		return SoundEvents.GENERIC_DRINK;
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = new ItemStack(Items.GLASS_BOTTLE);
		super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(ConcoctionModMobEffects.MINTY_BREATH, 60*20, 2-1, false, false, true, null));
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

