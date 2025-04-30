package net.mcreator.concoction.procedures;

import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.component.DataComponents;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.minecraft.core.component.DataComponentType;


import javax.annotation.Nullable;

import static com.google.common.primitives.Floats.min;
import static net.minecraft.util.Mth.ceil;

@EventBusSubscriber
public class SweetnessWorkProcedure {
	@SubscribeEvent
	public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
		if (event.getEntity() != null) {
			execute(event, event.getEntity(), event.getItem());
		}
	}

	public static void execute(Entity entity, ItemStack itemstack) {
		execute(null, entity, itemstack);
	}

	private static void execute(@Nullable Event event, Entity entity, ItemStack itemstack) {
	if (!(entity instanceof Player _player)) return;
	if (!_player.hasEffect(ConcoctionModMobEffects.SWEETNESS)) return;
	if (!itemstack.has(DataComponents.FOOD)) return;

	boolean isSweetFood =
		isSweetFlavor(itemstack, ConcoctionModDataComponents.FOOD_EFFECT.value()) ||
		isSweetFlavor(itemstack, ConcoctionModDataComponents.FOOD_EFFECT_2.value()) ||
		isSweetFlavor(itemstack, ConcoctionModDataComponents.FOOD_EFFECT_3.value()) ||
		isSweetFlavor(itemstack, ConcoctionModDataComponents.FOOD_EFFECT_4.value()) ||
		isSweetFlavor(itemstack, ConcoctionModDataComponents.FOOD_EFFECT_5.value());

	int foodLevel = _player.getFoodData().getFoodLevel();

	if (isSweetFood) {
		int hungerMissing = 20 - foodLevel;
		int effectLevel = _player.getEffect(ConcoctionModMobEffects.SWEETNESS).getAmplifier();
		float bonusPercent = min(0.25f + 0.15f * effectLevel, 1f);
		int bonusHunger = ceil(hungerMissing * bonusPercent);
		_player.getFoodData().setFoodLevel(foodLevel + bonusHunger);
	} else {
	int foodLevelAfter = _player.getFoodData().getFoodLevel();
	int originalHunger = itemstack.get(DataComponents.FOOD).nutrition();
	int foodLevelBefore = foodLevelAfter - originalHunger;
	int reducedHunger = ceil(originalHunger * 0.5f);
	_player.getFoodData().setFoodLevel(foodLevelBefore + reducedHunger);
}

}
 

	private static boolean isSweetFlavor(ItemStack stack, DataComponentType<FoodEffectComponent> type) {
		if (!stack.has(type)) return false;
		FoodEffectComponent comp = stack.get(type);
		return comp != null && comp.type() == FoodEffectType.SWEET;
	}
}
