package net.mcreator.concoction.client;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.mcreator.concoction.init.ConcoctionWoodTypes;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(
        modid = ConcoctionMod.MODID,
        bus = EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ConcoctionClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            FoodTooltipClientSettings.load();
            Sheets.addWoodType(ConcoctionWoodTypes.CINNAMON);

            BlockEntityRenderers.register(
                    ConcoctionModBlockEntities.CINNAMON_SIGN.get(),
                    SignRenderer::new
            );
            BlockEntityRenderers.register(
                    ConcoctionModBlockEntities.CINNAMON_HANGING_SIGN.get(),
                    HangingSignRenderer::new
            );
        });
    }
}
