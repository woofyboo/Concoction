
/*
 * MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;

import net.mcreator.concoction.fluid.WeightedSoulsFluid;
import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModFluids {
	public static final DeferredRegister<Fluid> REGISTRY = DeferredRegister.create(BuiltInRegistries.FLUID, ConcoctionMod.MODID);
	public static final DeferredHolder<Fluid, FlowingFluid> WEIGHTED_SOULS = REGISTRY.register("weighted_souls", () -> new WeightedSoulsFluid.Source());
	public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_WEIGHTED_SOULS = REGISTRY.register("flowing_weighted_souls", () -> new WeightedSoulsFluid.Flowing());

	@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class FluidsClientSideHandler {
		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event) {
			ItemBlockRenderTypes.setRenderLayer(WEIGHTED_SOULS.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(FLOWING_WEIGHTED_SOULS.get(), RenderType.translucent());
		}
	}
}
