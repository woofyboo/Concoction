package net.mcreator.concoction.procedures;

import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.ConcoctionMod;

import javax.annotation.Nullable;

@EventBusSubscriber
public class FireDamageTakenMintyBreathProcedure {
	@SubscribeEvent
	public static void onEntityAttacked(LivingHurtEvent event) {
		if (event != null && event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getSource(), event.getEntity(), event.getAmount());
		}
	}

	public static void execute(LevelAccessor world, DamageSource damagesource, Entity entity, double amount) {
		execute(null, world, damagesource, entity, amount);
	}

	private static void execute(@Nullable LivingHurtEvent event, LevelAccessor world, DamageSource damagesource, Entity entity, double amount) {
		if (damagesource == null || entity == null)
			return;
		if (entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(ConcoctionModMobEffects.MINTY_BREATH)) {
			if (damagesource.is(TagKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("minecraft:is_fire")))) {
				event.setAmount((float) (amount * 0.9));
			}
		}
	}
}
