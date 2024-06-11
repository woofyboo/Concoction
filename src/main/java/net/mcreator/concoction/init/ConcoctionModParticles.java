
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.concoction.init;

import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.concoction.client.particle.MintLeafParticleVariant2Particle;
import net.mcreator.concoction.client.particle.MintLeafParticleVariant1Particle;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ConcoctionModParticles {
	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ConcoctionModParticleTypes.MINT_LEAF_PARTICLE_VARIANT_1.get(), MintLeafParticleVariant1Particle::provider);
		event.registerSpriteSet(ConcoctionModParticleTypes.MINT_LEAF_PARTICLE_VARIANT_2.get(), MintLeafParticleVariant2Particle::provider);
	}
}
