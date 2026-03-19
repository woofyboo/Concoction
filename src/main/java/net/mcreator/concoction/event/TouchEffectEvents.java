package net.mcreator.concoction.event;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class TouchEffectEvents {
    private TouchEffectEvents() {
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        Entity source = event.getSource().getEntity();
        if (!(source instanceof LivingEntity attacker)) {
            return;
        }

        if (attacker.hasEffect(ConcoctionModMobEffects.FIERY_TOUCH)) {
            event.getEntity().igniteForSeconds(6);
        }

        if (attacker.hasEffect(ConcoctionModMobEffects.FROST_TOUCH)) {
            event.getEntity().setTicksFrozen(Math.max(event.getEntity().getTicksFrozen(), 240));
        }
    }
}
