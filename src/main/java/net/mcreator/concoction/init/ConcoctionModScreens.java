
package net.mcreator.concoction.init;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;

import net.mcreator.concoction.client.gui.OvenScreen;
import net.mcreator.concoction.client.gui.KitchenCabinetScreen;
import net.mcreator.concoction.client.gui.BoilingCauldronScreen;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ConcoctionModScreens {
	@SubscribeEvent
	public static void clientLoad(RegisterMenuScreensEvent event) {
		event.register(ConcoctionModMenus.BOILING_CAULDRON_INTERFACE.get(), BoilingCauldronScreen::new);
		event.register(ConcoctionModMenus.KITCHEN_CABINET_INTERFACE.get(), KitchenCabinetScreen::new);
		event.register(ConcoctionModMenus.OVEN_GUI.get(), OvenScreen::new);
	}
}
