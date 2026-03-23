package net.mcreator.concoction.handlers;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class PuffballSoupBlindnessHandler {
    private static final String NBT_KEY = "concoction_puffball_soup_blindness_guard";

    private PuffballSoupBlindnessHandler() {
    }

    public static void grantBlindnessGuard(LivingEntity entity) {
        if (entity instanceof Player player) {
            player.getPersistentData().putBoolean(NBT_KEY, true);
        }
    }

    @SubscribeEvent
    public static void onMobEffectApplicable(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide()) {
            return;
        }
        if (!player.getPersistentData().getBoolean(NBT_KEY)) {
            return;
        }
        if (event.getEffectInstance() == null || !event.getEffectInstance().is(MobEffects.BLINDNESS)) {
            return;
        }

        event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);

        boolean removeGuard = player.getRandom().nextFloat() < 0.5F;
        if (removeGuard) {
            player.getPersistentData().remove(NBT_KEY);
            spawnSmokeParticles(player);
        } else {
            spawnHappyVillagerParticles(player);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        if (event.isWasDeath()) {
            newPlayer.getPersistentData().remove(NBT_KEY);
            return;
        }

        CompoundTag oldTag = oldPlayer.getPersistentData();
        if (oldTag.getBoolean(NBT_KEY)) {
            newPlayer.getPersistentData().putBoolean(NBT_KEY, true);
        }
    }

    private static void spawnHappyVillagerParticles(Player player) {
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    player.getX(),
                    player.getY() + 1.0D,
                    player.getZ(),
                    8,
                    0.35D,
                    0.4D,
                    0.35D,
                    0.02D
            );
        }
    }

    private static void spawnSmokeParticles(Player player) {
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.SMOKE,
                    player.getX(),
                    player.getY() + 1.0D,
                    player.getZ(),
                    10,
                    0.3D,
                    0.4D,
                    0.3D,
                    0.02D
            );
        }
    }
}
