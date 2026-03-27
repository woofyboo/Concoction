package net.mcreator.concoction.handlers;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.mixins.LivingEntityEffectAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class WeepingStateHandler {
    private static final String WEEPING_INITIALIZED_TAG = "concoction_weeping_initialized";
    private static final String WEEPING_SAVED_HEALTH_TAG = "concoction_weeping_saved_health";
    private static final String WEEPING_SUSPENDED_EFFECTS_TAG = "concoction_weeping_suspended_effects";

    private WeepingStateHandler() {
    }

    public static void onWeepingAdded(LivingEntity living) {
        if (living.level().isClientSide() || !living.hasEffect(ConcoctionModMobEffects.WEEPING)) {
            return;
        }

        CompoundTag persistentData = living.getPersistentData();
        if (persistentData.getBoolean(WEEPING_INITIALIZED_TAG)) {
            return;
        }

        persistentData.putBoolean(WEEPING_INITIALIZED_TAG, true);
        persistentData.putFloat(WEEPING_SAVED_HEALTH_TAG, living.getHealth());
        suspendActiveEffects(living);
    }

    public static void ensureWeepingState(LivingEntity living) {
        if (living.level().isClientSide() || !living.hasEffect(ConcoctionModMobEffects.WEEPING)) {
            return;
        }

        onWeepingAdded(living);
    }

    public static void onWeepingRemoved(LivingEntity living) {
        if (living.level().isClientSide()) {
            return;
        }

        restoreSuspendedEffects(living);

        CompoundTag persistentData = living.getPersistentData();
        if (persistentData.contains(WEEPING_SAVED_HEALTH_TAG, Tag.TAG_FLOAT) && living.isAlive()) {
            living.setHealth(Mth.clamp(persistentData.getFloat(WEEPING_SAVED_HEALTH_TAG), 0.0F, living.getMaxHealth()));
        }

        clearWeepingState(living);
    }

    public static boolean suspendIncomingEffect(LivingEntity living, MobEffectInstance incomingEffect) {
        if (living.level().isClientSide()
                || incomingEffect.is(ConcoctionModMobEffects.WEEPING)
                || !living.hasEffect(ConcoctionModMobEffects.WEEPING)) {
            return false;
        }

        onWeepingAdded(living);

        List<MobEffectInstance> suspendedEffects = readSuspendedEffects(living);
        for (MobEffectInstance suspendedEffect : suspendedEffects) {
            if (!suspendedEffect.getEffect().equals(incomingEffect.getEffect())) {
                continue;
            }

            suspendedEffect.update(new MobEffectInstance(incomingEffect));
            writeSuspendedEffects(living, suspendedEffects);
            return true;
        }

        suspendedEffects.add(new MobEffectInstance(incomingEffect));
        writeSuspendedEffects(living, suspendedEffects);
        return true;
    }

    private static void suspendActiveEffects(LivingEntity living) {
        List<MobEffectInstance> suspendedEffects = new ArrayList<>();
        boolean removedAny = false;

        Iterator<MobEffectInstance> iterator = living.getActiveEffectsMap().values().iterator();
        while (iterator.hasNext()) {
            MobEffectInstance activeEffect = iterator.next();
            if (activeEffect.is(ConcoctionModMobEffects.WEEPING)) {
                continue;
            }

            suspendedEffects.add(new MobEffectInstance(activeEffect));
            activeEffect.getEffect().value().removeAttributeModifiers(living.getAttributes());
            iterator.remove();
            sendRemovePacket(living, activeEffect);
            removedAny = true;
        }

        writeSuspendedEffects(living, suspendedEffects);
        if (removedAny) {
            markEffectsDirty(living);
            refreshDirtyAttributes(living);
        }
    }

    private static void restoreSuspendedEffects(LivingEntity living) {
        List<MobEffectInstance> suspendedEffects = readSuspendedEffects(living);
        if (suspendedEffects.isEmpty()) {
            return;
        }

        boolean restoredAny = false;
        for (MobEffectInstance suspendedEffect : suspendedEffects) {
            MobEffectInstance restoredEffect = new MobEffectInstance(suspendedEffect);
            MobEffectInstance currentEffect = living.getActiveEffectsMap().get(restoredEffect.getEffect());
            if (currentEffect == null) {
                living.getActiveEffectsMap().put(restoredEffect.getEffect(), restoredEffect);
                restoredEffect.getEffect().value().addAttributeModifiers(living.getAttributes(), restoredEffect.getAmplifier());
                sendUpdatePacket(living, restoredEffect);
                restoredAny = true;
                continue;
            }

            if (currentEffect.update(restoredEffect)) {
                currentEffect.getEffect().value().removeAttributeModifiers(living.getAttributes());
                currentEffect.getEffect().value().addAttributeModifiers(living.getAttributes(), currentEffect.getAmplifier());
                sendUpdatePacket(living, currentEffect);
                restoredAny = true;
            }
        }

        if (restoredAny) {
            markEffectsDirty(living);
            refreshDirtyAttributes(living);
        }
    }

    private static List<MobEffectInstance> readSuspendedEffects(LivingEntity living) {
        List<MobEffectInstance> suspendedEffects = new ArrayList<>();
        ListTag savedEffects = living.getPersistentData().getList(WEEPING_SUSPENDED_EFFECTS_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < savedEffects.size(); i++) {
            MobEffectInstance loaded = MobEffectInstance.load(savedEffects.getCompound(i));
            if (loaded != null && !loaded.is(ConcoctionModMobEffects.WEEPING)) {
                suspendedEffects.add(loaded);
            }
        }
        return suspendedEffects;
    }

    private static void writeSuspendedEffects(LivingEntity living, List<MobEffectInstance> suspendedEffects) {
        if (suspendedEffects.isEmpty()) {
            living.getPersistentData().remove(WEEPING_SUSPENDED_EFFECTS_TAG);
            return;
        }

        ListTag serializedEffects = new ListTag();
        for (MobEffectInstance suspendedEffect : suspendedEffects) {
            Tag saved = suspendedEffect.save();
            if (saved instanceof CompoundTag compoundTag) {
                serializedEffects.add(compoundTag);
            }
        }
        living.getPersistentData().put(WEEPING_SUSPENDED_EFFECTS_TAG, serializedEffects);
    }

    private static void clearWeepingState(LivingEntity living) {
        CompoundTag persistentData = living.getPersistentData();
        persistentData.remove(WEEPING_INITIALIZED_TAG);
        persistentData.remove(WEEPING_SAVED_HEALTH_TAG);
        persistentData.remove(WEEPING_SUSPENDED_EFFECTS_TAG);
    }

    private static void markEffectsDirty(LivingEntity living) {
        ((LivingEntityEffectAccess) living).concoction$setEffectsDirty(true);
    }

    private static void refreshDirtyAttributes(LivingEntity living) {
        ((LivingEntityEffectAccess) living).concoction$invokeRefreshDirtyAttributes();
    }

    private static void sendRemovePacket(LivingEntity living, MobEffectInstance effectInstance) {
        if (living.level() instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().broadcastAndSend(
                    living,
                    new ClientboundRemoveMobEffectPacket(living.getId(), effectInstance.getEffect())
            );
        }
    }

    private static void sendUpdatePacket(LivingEntity living, MobEffectInstance effectInstance) {
        if (living.level() instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().broadcastAndSend(
                    living,
                    new ClientboundUpdateMobEffectPacket(living.getId(), effectInstance, false)
            );
        }
    }
}
