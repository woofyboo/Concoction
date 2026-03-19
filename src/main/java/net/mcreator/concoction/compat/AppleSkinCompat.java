package net.mcreator.concoction.compat;

import net.mcreator.concoction.utils.Utils;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;


@EventBusSubscriber(Dist.CLIENT)
public class AppleSkinCompat {
    @SubscribeEvent
    public static void onRenderGuiOverlayPre(RenderGuiLayerEvent.Pre event) {
        if (event.getName().toString().equals("appleskin:saturation_level")) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && Utils.isPhotosynthesisActive(mc.player)) {
                event.setCanceled(true);
            }
        }
    }
}
