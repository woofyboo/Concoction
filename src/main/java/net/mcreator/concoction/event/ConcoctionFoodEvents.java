package net.mcreator.concoction.event;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.item.NetherSlopItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

@EventBusSubscriber(
        modid = ConcoctionMod.MODID,
        bus = EventBusSubscriber.Bus.GAME
)
public class ConcoctionFoodEvents {

    @SubscribeEvent
    public static void onLivingUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        LivingEntity living = event.getEntity();
        if (!(living instanceof Player player)) {
            return;
        }

        // работаем только на сервере
        if (player.level().isClientSide) {
            return;
        }

        ItemStack used = event.getItem();

        // нас интересует только еда
        if (used.getFoodProperties(living) == null) {
            return;
        }

        // если это наше блюдо — ничего не делаем, NetherSlopItem сам всё обрабатывает
        if (used.is(ConcoctionModItems.NETHER_SLOP.get())) {
            return;
        }

        // любая ДРУГАЯ еда: -3 уровня складываний
        int current = NetherSlopItem.getNetherSlopStack(player);
        if (current <= 0) {
            // на всякий случай зафиксируем 0
            NetherSlopItem.setNetherSlopStack(player, 0);
            return;
        }

        int newValue = current - 3;
        NetherSlopItem.setNetherSlopStack(player, newValue);
    }
}
