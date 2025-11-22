package net.mcreator.concoction.event;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = "concoction", bus = Bus.GAME)
public class WeepingEvents {

    private static boolean hasWeeping(LivingEntity entity) {
        return entity.hasEffect(ConcoctionModMobEffects.WEEPING);
    }

    /**
     * Пункт 2: моб НЕ МОЖЕТ РЕГЕНЕРИРОВАТЬ ВООБЩЕ под эффектом.
     */
    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (!hasWeeping(entity)) {
            return;
        }

        // Режем любую регенку в ноль
        event.setAmount(0.0F);
    }

    /**
     * Пункт 3: любой входящий урон превращаем в ровно 1.0F.
     * Делаем это на LivingIncomingDamageEvent.
     */
    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (!hasWeeping(entity)) {
            return;
        }

        // финальный базовый урон (до всех эффектов дальше) = 1
        event.setAmount(1.0F);
    }
}
