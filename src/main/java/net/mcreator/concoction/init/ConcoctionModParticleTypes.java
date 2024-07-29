
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;

import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModParticleTypes {
	public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(Registries.PARTICLE_TYPE, ConcoctionMod.MODID);
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MINT_LEAF_PARTICLE_VARIANT_1 = REGISTRY.register("mint_leaf_particle_variant_1", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MINT_LEAF_PARTICLE_VARIANT_2 = REGISTRY.register("mint_leaf_particle_variant_2", () -> new SimpleParticleType(false));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FEATHER_PARTICLE = REGISTRY.register("feather_particle", () -> new SimpleParticleType(false));
}
