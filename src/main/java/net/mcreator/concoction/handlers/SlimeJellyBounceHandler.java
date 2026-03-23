package net.mcreator.concoction.handlers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber
public class SlimeJellyBounceHandler {
    private static final String ACTIVE_KEY = "concoction_slime_jelly_bounce";
    private static final String PENDING_KEY = "concoction_slime_jelly_bounce_pending";
    private static final String PENDING_STRENGTH_KEY = "concoction_slime_jelly_bounce_strength";
    private static final float FALL_DAMAGE_MULTIPLIER = 0.15F;
    private static final double BASE_BOUNCE_VELOCITY = 0.55D;
    private static final double MAX_BOUNCE_VELOCITY = 1.6D;

    private SlimeJellyBounceHandler() {
    }

    public static void grantBounceBonus(LivingEntity entity) {
        if (entity instanceof Player player) {
            player.getPersistentData().putBoolean(ACTIVE_KEY, true);
            player.getPersistentData().remove(PENDING_KEY);
            player.getPersistentData().remove(PENDING_STRENGTH_KEY);
        }
    }

    private static boolean hasBounceBonus(Player player) {
        return player.getPersistentData().getBoolean(ACTIVE_KEY);
    }

    private static void consumeBounceBonus(Player player) {
        player.getPersistentData().remove(ACTIVE_KEY);
    }

    private static boolean hasPendingBounce(Player player) {
        return player.getPersistentData().getBoolean(PENDING_KEY);
    }

    private static void markPendingBounce(Player player) {
        player.getPersistentData().putBoolean(PENDING_KEY, true);
    }

    private static void clearPendingBounce(Player player) {
        player.getPersistentData().remove(PENDING_KEY);
        player.getPersistentData().remove(PENDING_STRENGTH_KEY);
    }

    private static void setPendingBounceStrength(Player player, double velocity) {
        player.getPersistentData().putDouble(PENDING_STRENGTH_KEY, velocity);
    }

    private static double getPendingBounceStrength(Player player) {
        return player.getPersistentData().getDouble(PENDING_STRENGTH_KEY);
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!hasBounceBonus(player)) {
            return;
        }
        if (event.getDistance() <= 3.0D || event.getDamageMultiplier() <= 0.0F) {
            return;
        }

        consumeBounceBonus(player);
        setPendingBounceStrength(player, calculateBounceVelocity(event.getDistance(), event.getDamageMultiplier()));
        event.setDamageMultiplier(event.getDamageMultiplier() * FALL_DAMAGE_MULTIPLIER);
        player.fallDistance = 0.0F;
        markPendingBounce(player);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player == null || player.level().isClientSide()) {
            return;
        }
        if (!hasPendingBounce(player) || !player.onGround()) {
            return;
        }

        double bounceVelocity = getPendingBounceStrength(player);
        clearPendingBounce(player);
        triggerBounce(player, bounceVelocity);
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        if (event.isWasDeath()) {
            newPlayer.getPersistentData().remove(ACTIVE_KEY);
            newPlayer.getPersistentData().remove(PENDING_KEY);
            newPlayer.getPersistentData().remove(PENDING_STRENGTH_KEY);
            return;
        }

        CompoundTag oldTag = oldPlayer.getPersistentData();
        if (oldTag.getBoolean(ACTIVE_KEY)) {
            newPlayer.getPersistentData().putBoolean(ACTIVE_KEY, true);
        }
        if (oldTag.getBoolean(PENDING_KEY)) {
            newPlayer.getPersistentData().putBoolean(PENDING_KEY, true);
            newPlayer.getPersistentData().putDouble(PENDING_STRENGTH_KEY, oldTag.getDouble(PENDING_STRENGTH_KEY));
        }
    }

    private static void triggerBounce(Player player, double bounceVelocity) {
        Vec3 velocity = player.getDeltaMovement();
        player.setOnGround(false);
        player.setDeltaMovement(velocity.x, Math.max(bounceVelocity, Math.abs(velocity.y) * 0.66D), velocity.z);
        player.hasImpulse = true;
        player.hurtMarked = true;
        player.fallDistance = 0.0F;

        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.SLIME_BLOCK_PLACE,
                SoundSource.PLAYERS,
                0.9F,
                0.95F + player.getRandom().nextFloat() * 0.1F
        );

        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SLIME_BLOCK.defaultBlockState()),
                    player.getX(),
                    player.getY() + 0.05D,
                    player.getZ(),
                    14,
                    0.28D,
                    0.06D,
                    0.28D,
                    0.02D
            );
        }
    }

    private static double calculateBounceVelocity(double fallDistance, float damageMultiplier) {
        double expectedDamage = Math.max(0.0D, (fallDistance - 3.0D) * damageMultiplier);
        double velocity = BASE_BOUNCE_VELOCITY + expectedDamage * 0.09D;
        return Math.min(velocity, MAX_BOUNCE_VELOCITY);
    }
}
