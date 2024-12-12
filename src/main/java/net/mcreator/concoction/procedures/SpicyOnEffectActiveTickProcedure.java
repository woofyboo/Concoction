package net.mcreator.concoction.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;

import net.mcreator.concoction.init.ConcoctionModMobEffects;

public class SpicyOnEffectActiveTickProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(ConcoctionModMobEffects.SPICY) ? _livEnt.getEffect(ConcoctionModMobEffects.SPICY).getDuration() : 0) % 60 == 0) {
			entity.hurt(new DamageSource(world.holderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("concoction:spicy_damage")))),
					(float) (((entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(ConcoctionModMobEffects.SPICY) ? _livEnt.getEffect(ConcoctionModMobEffects.SPICY).getAmplifier() : 0) + 1) * 1));
		}
	}
}
