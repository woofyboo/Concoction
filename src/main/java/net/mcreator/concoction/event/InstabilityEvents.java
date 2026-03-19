package net.mcreator.concoction.event;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class InstabilityEvents {
    private static final ResourceKey<DamageType> RIFT_DAMAGE =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("concoction:rift"));

    private InstabilityEvents() {
    }

    @SubscribeEvent
    public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity living)
                || entity.level().isClientSide()
                || !living.hasEffect(ConcoctionModMobEffects.INSTABILITY)) {
            return;
        }

        ServerLevel level = (ServerLevel) entity.level();
        BlockPos pos = BlockPos.containing(entity.getX(), entity.getY(), entity.getZ());
        BlockState state = level.getBlockState(pos);
        if (!state.is(BlockTags.create(ResourceLocation.withDefaultNamespace("portals")))) {
            return;
        }

        level.sendParticles(ParticleTypes.PORTAL, entity.getX() + 0.5D, entity.getY() + 0.5D, entity.getZ() + 0.5D, 5, 3, 3, 3, 0.5D);
        level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
        entity.hurt(new DamageSource(level.holderOrThrow(RIFT_DAMAGE)), 6.0F);

        ((ICancellableEvent) event).setCanceled(true);

        Direction.Axis axis = state.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)
                ? state.getValue(BlockStateProperties.HORIZONTAL_AXIS)
                : Direction.Axis.X;
        if (axis == Direction.Axis.Z) {
            entity.push(-1.5D, 2.0D, 1.5D);
        } else {
            entity.push(1.5D, 2.0D, -1.5D);
        }

        if (entity instanceof ServerPlayer serverPlayer) {
            Utils.grantAdvancement(serverPlayer, "concoction:cry_about_it");
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }

        MobEffectInstance effect = entity.getEffect(ConcoctionModMobEffects.INSTABILITY);
        if (effect == null || event.getSource().is(RIFT_DAMAGE)) {
            return;
        }

        teleportUnstably(entity);
    }

    private static void teleportUnstably(LivingEntity entity) {
        Level level = entity.level();
        RandomSource random = level.getRandom();
        double targetX = entity.getX() + Mth.nextInt(random, -16, 16);
        double targetY = Math.max(entity.getY() + Mth.nextInt(random, -3, 3), level.getMinBuildHeight() + 1);
        double targetZ = entity.getZ() + Mth.nextInt(random, -16, 16);

        entity.teleportTo(targetX, targetY, targetZ);
        if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.teleport(targetX, targetY, targetZ, entity.getYRot(), entity.getXRot());
        }
    }
}
