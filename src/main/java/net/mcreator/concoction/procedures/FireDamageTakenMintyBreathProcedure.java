package net.mcreator.concoction.procedures;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import javax.annotation.Nullable;

@EventBusSubscriber
public class FireDamageTakenMintyBreathProcedure {
	@SubscribeEvent
	public static void onEntityAttacked(LivingIncomingDamageEvent event) {
		if (event != null && event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getSource(), event.getEntity(), event.getAmount());
		}
	}

	public static void execute(LevelAccessor world, DamageSource damagesource, Entity entity, double amount) {
		execute(null, world, damagesource, entity, amount);
	}

	private static void execute(@Nullable LivingIncomingDamageEvent event, LevelAccessor world, DamageSource damagesource, Entity entity, double amount) {
		if (damagesource == null || entity == null)
			return;
		if (entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(ConcoctionModMobEffects.MINTY_BREATH)) {
			if (damagesource.is(TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("is_fire")))) {
				int ampl = _livEnt0.getActiveEffects().stream().filter(mobEffectInstance -> mobEffectInstance.is(ConcoctionModMobEffects.MINTY_BREATH)).findFirst().get().getAmplifier();
				event.setAmount((float) (amount * (1.0 - ampl/10f)));
//				ConcoctionMod.LOGGER.debug(event.getAmount());
			}
		}
	}
}
