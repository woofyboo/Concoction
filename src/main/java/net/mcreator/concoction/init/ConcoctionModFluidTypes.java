
/*
 * MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.fluids.FluidType;

import net.mcreator.concoction.fluid.types.WeightedSoulsFluidType;
import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModFluidTypes {
	public static final DeferredRegister<FluidType> REGISTRY = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, ConcoctionMod.MODID);
	public static final DeferredHolder<FluidType, FluidType> WEIGHTED_SOULS_TYPE = REGISTRY.register("weighted_souls", () -> new WeightedSoulsFluidType());
}
