package net.mcreator.concoction.init;

import net.mcreator.concoction.client.renderer.CinnamonBoatRenderer;
import net.mcreator.concoction.client.renderer.CinnamonChestBoatRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ConcoctionBoatEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ConcoctionBoatEntities.CINNAMON_BOAT.get(), CinnamonBoatRenderer::new);
		event.registerEntityRenderer(ConcoctionBoatEntities.CINNAMON_CHEST_BOAT.get(), CinnamonChestBoatRenderer::new);
	}

	private ConcoctionBoatEntityRenderers() {
	}
}
