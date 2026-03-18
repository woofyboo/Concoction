package net.mcreator.concoction.event;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.init.ConcoctionModParticleTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.living.EffectParticleModificationEvent;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = Bus.GAME)
public class WeepingParticleEvents {

	@SubscribeEvent
	public static void onEffectParticleModification(EffectParticleModificationEvent event) {
		if (event.getEffect().is(ConcoctionModMobEffects.WEEPING)) {
			event.setParticleOptions(ConcoctionModParticleTypes.WEEPING_PARTICLE.get());
		}
	}
}
