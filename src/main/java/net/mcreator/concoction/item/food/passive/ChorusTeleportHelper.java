package net.mcreator.concoction.item.food.passive;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class ChorusTeleportHelper {
    private static final int TELEPORT_ATTEMPTS = 16;
    private static final double HORIZONTAL_RANGE = 16.0D;
    private static final int VERTICAL_RANGE = 8;

    private ChorusTeleportHelper() {
    }

    public static boolean tryTeleportLikeChorusFruit(LivingEntity entity) {
        Level level = entity.level();
        if (level.isClientSide()) {
            return false;
        }

        double startX = entity.getX();
        double startY = entity.getY();
        double startZ = entity.getZ();
        double maxY = level instanceof ServerLevel serverLevel
                ? serverLevel.getLogicalHeight() - 1
                : level.getMaxBuildHeight() - 1;

        if (entity.isPassenger()) {
            entity.stopRiding();
        }

        for (int i = 0; i < TELEPORT_ATTEMPTS; i++) {
            double targetX = startX + (entity.getRandom().nextDouble() - 0.5D) * HORIZONTAL_RANGE;
            double targetY = Mth.clamp(
                    startY + (double) (entity.getRandom().nextInt(VERTICAL_RANGE * 2) - VERTICAL_RANGE),
                    level.getMinBuildHeight(),
                    maxY
            );
            double targetZ = startZ + (entity.getRandom().nextDouble() - 0.5D) * HORIZONTAL_RANGE;

            if (!entity.randomTeleport(targetX, targetY, targetZ, true)) {
                continue;
            }

            level.playSound(null, startX, startY, startZ, SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
            entity.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
            return true;
        }

        return false;
    }
}
