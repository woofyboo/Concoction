package net.mcreator.concoction.procedures;

import com.mojang.datafixers.util.Pair;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;

import net.mcreator.concoction.init.ConcoctionModMobEffects;

import java.util.concurrent.atomic.AtomicInteger;

public class SpicyOnEffectActiveTickProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity livEnt) {
			if ((livEnt.hasEffect(ConcoctionModMobEffects.SPICY) ? livEnt.getEffect(ConcoctionModMobEffects.SPICY).getDuration() : 0) % 60 == 0) {
				livEnt.hurt(new DamageSource(world.holderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("concoction:spicy_damage")))),
						(float) (((livEnt.hasEffect(ConcoctionModMobEffects.SPICY) ? livEnt.getEffect(ConcoctionModMobEffects.SPICY).getAmplifier() : 0) + 1) * 1));
				if (world instanceof Level pLevel && !pLevel.isClientSide()) {

					long harmfulEffectCount = livEnt.getActiveEffects().stream().map(effect -> new Pair<>(effect.getEffect(), effect.getEffect().value().getCategory())).
							filter(pair -> pair.getSecond().equals(MobEffectCategory.HARMFUL)).count();

					livEnt.getActiveEffects().stream().map(effect -> new Pair<>(effect.getEffect(), effect.getEffect().value().getCategory())).
							filter(pair -> pair.getSecond().equals(MobEffectCategory.HARMFUL)).forEach(pair -> {
								livEnt.removeEffect(pair.getFirst());
							});

					System.out.println(harmfulEffectCount);

					if (livEnt instanceof ServerPlayer player) {
						AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.parse("concoction:spice_remove_many_debuffs"));
						if (adv != null) {
							AdvancementProgress _ap = player.getAdvancements().getOrStartProgress(adv);
							if (!_ap.isDone() && harmfulEffectCount >= 5) {
								for (String criteria : _ap.getRemainingCriteria())
									player.getAdvancements().award(adv, criteria);
							}
						}
					}
				}
			}
		}
	}
}

