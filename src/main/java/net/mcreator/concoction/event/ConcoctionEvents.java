// src/main/java/net/mcreator/concoction/event/ConcoctionEvents.java
package net.mcreator.concoction.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;

@EventBusSubscriber(modid = "concoction", bus = EventBusSubscriber.Bus.GAME)
public class ConcoctionEvents {

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent e) {
        // работаем только на сервере
        if (!(e.getPlayer().level() instanceof ServerLevel)) return;

        // ✅ NeoForge API: ItemTossEvent#getEntity() -> ItemEntity
        ItemEntity item = e.getEntity();

        var nbt = item.getPersistentData();
        nbt.putUUID("concoction_owner", e.getPlayer().getUUID());
        nbt.putInt("concoction_slide_ticks", 0);

        // отладка — увидишь один раз при броске (можно удалить)
        // ((ServerLevel)e.getPlayer().level()).getServer().sendSystemMessage(net.minecraft.network.chat.Component.literal("[Concoction] Toss tagged"));
    }
}
