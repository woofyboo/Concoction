package net.mcreator.concoction.event;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.handlers.FoodAftertasteHandler;
import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectComponent;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class ConcoctionFoodEvents {
    private static final Map<UUID, ConsumeSnapshot> CONSUME_SNAPSHOTS = new HashMap<>();
    private static final Map<UUID, ConsumeSnapshot> FORCED_CONSUME_SNAPSHOTS = new HashMap<>();

    private ConcoctionFoodEvents() {
    }

    @SubscribeEvent
    public static void onLivingUseItemStart(LivingEntityUseItemEvent.Start event) {
        LivingEntity living = event.getEntity();
        ItemStack stack = event.getItem();

        if (living.hasEffect(MobEffects.CONFUSION) && stack.has(DataComponents.FOOD)) {
            if (living instanceof Player player) {
                player.getCooldowns().addCooldown(stack.getItem(), 20);
            }
            ((ICancellableEvent) event).setCanceled(true);
            return;
        }

        if (!living.level().isClientSide() && shouldSuppressDefaultConsumeEffects(living, stack)) {
            CONSUME_SNAPSHOTS.put(living.getUUID(), ConsumeSnapshot.capture(living));
        }
    }

    @SubscribeEvent
    public static void onLivingUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide()) {
            return;
        }

        ItemStack used = event.getItem();
        restoreDefaultConsumeState(living, used);
        applyBrainFreeze(living, used);
        applyPassiveFoodEffects(living, used);
        FoodAftertasteHandler.recordConsumedFood(living, used);
    }

    private static boolean shouldSuppressDefaultConsumeEffects(LivingEntity living, ItemStack used) {
        if (used.is(Items.OMINOUS_BOTTLE) || used.is(Items.SUSPICIOUS_STEW) || used.is(Items.CHORUS_FRUIT)) {
            return false;
        }
        return used.getFoodProperties(living) != null;
    }

    public static void captureForcedConsumeSnapshot(LivingEntity living) {
        if (living.level().isClientSide()) {
            return;
        }
        FORCED_CONSUME_SNAPSHOTS.put(living.getUUID(), ConsumeSnapshot.capture(living));
    }

    public static void restoreForcedConsumeSnapshot(LivingEntity living, ItemStack used) {
        if (living.level().isClientSide()) {
            return;
        }
        restoreConsumeSnapshot(FORCED_CONSUME_SNAPSHOTS.remove(living.getUUID()), living, used, true);
    }

    private static void restoreDefaultConsumeState(LivingEntity living, ItemStack used) {
        restoreConsumeSnapshot(CONSUME_SNAPSHOTS.remove(living.getUUID()), living, used, false);
    }

    private static void restoreConsumeSnapshot(ConsumeSnapshot snapshot, LivingEntity living, ItemStack used, boolean forced) {
        if (snapshot == null || (!forced && !shouldSuppressDefaultConsumeEffects(living, used))) {
            return;
        }

        restoreEffectState(living, snapshot.effects());
    }

    private static void restoreEffectState(LivingEntity living, List<MobEffectInstance> snapshotEffects) {
        Map<MobEffect, MobEffectInstance> before = toEffectMap(snapshotEffects);
        Map<MobEffect, MobEffectInstance> after = toEffectMap(List.copyOf(living.getActiveEffects()));

        for (Map.Entry<MobEffect, MobEffectInstance> entry : before.entrySet()) {
            MobEffect effect = entry.getKey();
            MobEffectInstance previous = entry.getValue();
            MobEffectInstance current = after.get(effect);

            if (current == null) {
                living.addEffect(new MobEffectInstance(previous));
                continue;
            }

            if (!matchesNaturalProgression(previous, current)) {
                living.removeEffect(current.getEffect());
                living.addEffect(new MobEffectInstance(previous));
            }
        }

        for (Map.Entry<MobEffect, MobEffectInstance> entry : after.entrySet()) {
            if (!before.containsKey(entry.getKey())) {
                living.removeEffect(entry.getValue().getEffect());
            }
        }
    }

    private static Map<MobEffect, MobEffectInstance> toEffectMap(List<MobEffectInstance> effects) {
        Map<MobEffect, MobEffectInstance> map = new HashMap<>();
        for (MobEffectInstance effect : effects) {
            map.put(effect.getEffect().value(), effect);
        }
        return map;
    }

    private static boolean matchesNaturalProgression(MobEffectInstance previous, MobEffectInstance current) {
        return previous.getAmplifier() == current.getAmplifier()
                && previous.isAmbient() == current.isAmbient()
                && previous.isVisible() == current.isVisible()
                && previous.showIcon() == current.showIcon()
                && current.getDuration() <= previous.getDuration();
    }

    private static void applyBrainFreeze(LivingEntity living, ItemStack used) {
        if (!living.hasEffect(ConcoctionModMobEffects.MINTY_BREATH) || !used.is(Items.POTION)) {
            return;
        }

        living.setTicksFrozen(200);

        if (living instanceof ServerPlayer serverPlayer) {
            Utils.grantAdvancement(serverPlayer, "concoction:brain_freeze_obtain_achievement");
        }

        if (living.level() instanceof ServerLevel serverLevel) {
            BlockHitResult hitResult = living.level().clip(new ClipContext(
                    living.getEyePosition(1.0F),
                    living.getEyePosition(1.0F).add(living.getViewVector(1.0F).scale(2.0D)),
                    ClipContext.Block.VISUAL,
                    ClipContext.Fluid.NONE,
                    living
            ));
            BlockPos hitPos = hitResult.getBlockPos();
            serverLevel.sendParticles(ParticleTypes.CLOUD, hitPos.getX(), hitPos.getY(), hitPos.getZ(), 12, 0.3D, 0.3D, 0.3D, 0.0001D);
        }
    }

    private static void applyPassiveFoodEffects(LivingEntity living, ItemStack used) {
        if (used.getFoodProperties(living) == null) {
            return;
        }

        List<FoodPassiveEffectComponent> passiveEffects = used.getOrDefault(
                ConcoctionModDataComponents.FOOD_PASSIVE_EFFECTS.get(),
                List.of()
        );
        for (FoodPassiveEffectComponent passiveEffect : passiveEffects) {
            passiveEffect.applyOnConsume(living);
        }
    }

    private record ConsumeSnapshot(List<MobEffectInstance> effects) {
        private static ConsumeSnapshot capture(LivingEntity living) {
            return new ConsumeSnapshot(living.getActiveEffects().stream().map(MobEffectInstance::new).toList());
        }
    }
}
