package net.mcreator.concoction.procedures;

import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.concoction.init.ConcoctionModMobEffects;

@EventBusSubscriber
public class HealingBonusMintyProcedure {
	@SubscribeEvent
	public static void onEntityHealed(LivingHealEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity.hasEffect(ConcoctionModMobEffects.MINTY_BREATH)) {
			MobEffectInstance effect = entity.getEffect(ConcoctionModMobEffects.MINTY_BREATH);
			int level = effect.getAmplifier(); // 0 = level 1
			float multiplier = 1.5f + 0.25f * level;
			float originalAmount = event.getAmount();
			float bonusAmount = originalAmount * (multiplier - 1); // Only add the bonus
			entity.setHealth(Math.min(entity.getHealth() + bonusAmount, entity.getMaxHealth()));
		}
	}
}
