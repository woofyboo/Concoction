package net.mcreator.concoction.compat;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber
public class AppleSkinCompat {
    @SubscribeEvent
    public static void onRenderGuiOverlayPre(RenderGuiLayerEvent.Pre event) {
        if (event.getName().toString().equals("appleskin:saturation_level")) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null && mc.player.hasEffect(ConcoctionModMobEffects.PHOTOSYNTHESIS)) {
                event.setCanceled(true);
            }
        }
    }
}
