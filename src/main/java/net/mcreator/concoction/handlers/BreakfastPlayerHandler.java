package net.mcreator.concoction.handlers;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public final class BreakfastPlayerHandler {
    public static final String SLEEP_TIMER_KEY = "sleep_timer";
    private static final String BREAKFAST_REGEN_COUNTER_KEY = "breakfast_regen_counter";

    private static final int SLEEP_TIMER_DURATION = 8 * 60 * 20;
    private static final int BASE_REGEN_INTERVAL = 80;
    private static final int MAX_SURVIVAL_HP = 5;

    private BreakfastPlayerHandler() {
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        event.getEntity().getPersistentData().putInt(SLEEP_TIMER_KEY, SLEEP_TIMER_DURATION);
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        int sleepTimer = player.getPersistentData().getInt(SLEEP_TIMER_KEY);
        player.getPersistentData().putInt(SLEEP_TIMER_KEY, 0);

        if (!player.hasEffect(ConcoctionModMobEffects.BREAKFAST) || sleepTimer <= 0) {
            return;
        }

        MobEffectInstance effect = player.getEffect(ConcoctionModMobEffects.BREAKFAST);
        if (effect == null || player.getHealth() < player.getMaxHealth() * 0.9F) {
            return;
        }

        int level = effect.getAmplifier() + 1;
        int minHealth = Math.min(level, MAX_SURVIVAL_HP);
        player.setHealth(minHealth);
        event.setCanceled(true);
        player.level().broadcastEntityEvent(player, (byte) 35);
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)
                || player.getPersistentData().getInt(SLEEP_TIMER_KEY) <= 0
                || !player.hasEffect(ConcoctionModMobEffects.BREAKFAST)) {
            return;
        }

        MobEffectInstance effect = player.getEffect(ConcoctionModMobEffects.BREAKFAST);
        if (effect == null) {
            return;
        }

        int level = effect.getAmplifier() + 1;
        player.heal(1.0F);

        float resultingHealth = player.getHealth() - event.getAmount();
        int minHealth = Math.min(level, MAX_SURVIVAL_HP);
        if (resultingHealth <= 0 && player.getHealth() > player.getMaxHealth() * 0.9F) {
            event.setAmount(player.getHealth() - minHealth);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        tickBreakfastRegeneration(player);
        tickSleepTimer(player);
    }

    private static void tickBreakfastRegeneration(Player player) {
        if (player.getPersistentData().getInt(SLEEP_TIMER_KEY) <= 0
                || !player.hasEffect(ConcoctionModMobEffects.BREAKFAST)) {
            return;
        }

        MobEffectInstance effect = player.getEffect(ConcoctionModMobEffects.BREAKFAST);
        if (effect == null) {
            return;
        }

        int level = effect.getAmplifier() + 1;
        int regenInterval = Math.max(1, BASE_REGEN_INTERVAL - 10 * (level - 1));
        int regenCounter = player.getPersistentData().getInt(BREAKFAST_REGEN_COUNTER_KEY) + 1;

        if (regenCounter >= regenInterval) {
            if (player.getHealth() < player.getMaxHealth()) {
                player.heal(1.0F);
            }
            regenCounter = 0;
        }

        player.getPersistentData().putInt(BREAKFAST_REGEN_COUNTER_KEY, regenCounter);
    }

    private static void tickSleepTimer(Player player) {
        int timer = player.getPersistentData().getInt(SLEEP_TIMER_KEY);
        if (timer <= 0) {
            return;
        }

        timer--;
        player.getPersistentData().putInt(SLEEP_TIMER_KEY, timer);

        if (timer == 1) {
            player.displayClientMessage(Component.translatable("message.concoction.tired"), true);
        } else if (timer == SLEEP_TIMER_DURATION - 1) {
            player.displayClientMessage(Component.translatable("message.concoction.rested"), true);
        }
    }
}
