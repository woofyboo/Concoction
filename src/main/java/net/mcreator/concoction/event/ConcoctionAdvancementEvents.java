package net.mcreator.concoction.event;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class ConcoctionAdvancementEvents {
    private ConcoctionAdvancementEvents() {
    }

    @SubscribeEvent
    public static void onAdvancementEarn(AdvancementEvent.AdvancementEarnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Utils.grantAdvancement(player, "concoction:core");
        }
    }
}
