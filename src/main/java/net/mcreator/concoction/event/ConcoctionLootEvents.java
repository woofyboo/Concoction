package net.mcreator.concoction.event;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class ConcoctionLootEvents {
    private ConcoctionLootEvents() {
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity victim = event.getEntity();
        Entity source = event.getSource().getEntity();
        if (!(victim instanceof Stray) || !(source instanceof Creeper) || !(victim.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        ItemEntity drop = new ItemEntity(
                serverLevel,
                victim.getX() + 0.5D,
                victim.getY() + 0.5D,
                victim.getZ() + 0.5D,
                new ItemStack(ConcoctionModItems.MUSIC_DISC_HOT_ICE.get())
        );
        drop.setPickUpDelay(10);
        serverLevel.addFreshEntity(drop);
    }
}
