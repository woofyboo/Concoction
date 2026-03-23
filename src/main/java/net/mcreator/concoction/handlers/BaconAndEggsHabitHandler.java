package net.mcreator.concoction.handlers;

import net.mcreator.concoction.ConcoctionMod;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class BaconAndEggsHabitHandler {
    private static final String HABIT_LEVEL_KEY = "concoction_bacon_and_eggs_habit_level";
    private static final String WAKE_WINDOW_KEY = "concoction_bacon_and_eggs_wake_window";
    private static final String ATE_THIS_WAKE_KEY = "concoction_bacon_and_eggs_ate_this_wake";
    private static final String AWAKE_TICKS_KEY = "concoction_bacon_and_eggs_awake_ticks";

    private static final int MAX_HABIT_LEVEL = 4;
    private static final int WAKE_WINDOW_DURATION = 5 * 60 * 20;
    private static final int MAX_AWAKE_DURATION = 60 * 60 * 20;

    private BaconAndEggsHabitHandler() {
    }

    public static void onDishConsumed(Player player) {
        if (player.level().isClientSide()) {
            return;
        }

        CompoundTag data = player.getPersistentData();
        int wakeWindow = data.getInt(WAKE_WINDOW_KEY);
        if (wakeWindow <= 0 || data.getBoolean(ATE_THIS_WAKE_KEY)) {
            return;
        }

        int currentLevel = data.getInt(HABIT_LEVEL_KEY);
        int newLevel = Math.min(currentLevel + 1, MAX_HABIT_LEVEL);
        data.putInt(HABIT_LEVEL_KEY, newLevel);
        data.putBoolean(ATE_THIS_WAKE_KEY, true);
        data.putInt(WAKE_WINDOW_KEY, 0);

        if (newLevel > currentLevel) {
            playLevelGainEffects(player);
        }
    }

    @SubscribeEvent
    public static void onWakeUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        CompoundTag data = player.getPersistentData();
        data.putInt(WAKE_WINDOW_KEY, WAKE_WINDOW_DURATION);
        data.putBoolean(ATE_THIS_WAKE_KEY, false);
        data.putInt(AWAKE_TICKS_KEY, 0);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        CompoundTag data = player.getPersistentData();
        if (!player.isSleeping()) {
            int awakeTicks = data.getInt(AWAKE_TICKS_KEY) + 1;
            data.putInt(AWAKE_TICKS_KEY, awakeTicks);

            if (awakeTicks > MAX_AWAKE_DURATION) {
                resetHabitProgress(player);
                data.putInt(AWAKE_TICKS_KEY, 0);
                data.putInt(WAKE_WINDOW_KEY, 0);
                data.putBoolean(ATE_THIS_WAKE_KEY, false);
                return;
            }
        }

        int wakeWindow = data.getInt(WAKE_WINDOW_KEY);
        if (wakeWindow <= 0) {
            return;
        }

        wakeWindow--;
        data.putInt(WAKE_WINDOW_KEY, wakeWindow);

        if (wakeWindow == 0 && !data.getBoolean(ATE_THIS_WAKE_KEY)) {
            resetHabitProgress(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerDamaged(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }

        int habitLevel = player.getPersistentData().getInt(HABIT_LEVEL_KEY);
        if (habitLevel <= 0 || event.getNewDamage() <= 0.0F) {
            return;
        }

        float chance = switch (habitLevel) {
            case 1 -> 0.35F;
            case 2 -> 0.45F;
            case 3 -> 0.55F;
            default -> 0.65F;
        };

        float healAmount = switch (habitLevel) {
            case 1 -> 1.0F;
            case 2 -> 1.5F;
            case 3 -> 2.0F;
            default -> 2.5F;
        };

        if (player.getRandom().nextFloat() >= chance) {
            return;
        }

        ConcoctionMod.queueServerWork(1, () -> {
            if (!player.isAlive() || player.isRemoved()) {
                return;
            }
            player.heal(healAmount);
            playHealingProcEffects(player);
        });
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        CompoundTag oldData = oldPlayer.getPersistentData();
        CompoundTag newData = newPlayer.getPersistentData();

        newData.putInt(HABIT_LEVEL_KEY, oldData.getInt(HABIT_LEVEL_KEY));
        newData.putInt(AWAKE_TICKS_KEY, oldData.getInt(AWAKE_TICKS_KEY));

        if (!event.isWasDeath()) {
            newData.putInt(WAKE_WINDOW_KEY, oldData.getInt(WAKE_WINDOW_KEY));
            newData.putBoolean(ATE_THIS_WAKE_KEY, oldData.getBoolean(ATE_THIS_WAKE_KEY));
        } else {
            newData.putInt(WAKE_WINDOW_KEY, 0);
            newData.putBoolean(ATE_THIS_WAKE_KEY, false);
        }
    }

    private static void resetHabitProgress(Player player) {
        CompoundTag data = player.getPersistentData();
        int oldLevel = data.getInt(HABIT_LEVEL_KEY);
        data.putInt(HABIT_LEVEL_KEY, 0);
        data.putInt(WAKE_WINDOW_KEY, 0);
        data.putBoolean(ATE_THIS_WAKE_KEY, false);

        if (oldLevel > 0) {
            playProgressLostEffects(player);
        }
    }

    private static void playLevelGainEffects(Player player) {
        player.level().playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.9F, 1.15F);
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1.0D, player.getZ(), 8, 0.35D, 0.4D, 0.35D, 0.02D);
        }
    }

    private static void playProgressLostEffects(Player player) {
        player.level().playSound(null, player.blockPosition(), SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.PLAYERS, 1.0F, 0.8F);
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + 1.0D, player.getZ(), 10, 0.35D, 0.4D, 0.35D, 0.02D);
        }
    }

    private static void playHealingProcEffects(Player player) {
        player.level().playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.8F, 0.8F);
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HEART, player.getX(), player.getY() + 1.0D, player.getZ(), 4, 0.25D, 0.35D, 0.25D, 0.01D);
        }
    }
}
