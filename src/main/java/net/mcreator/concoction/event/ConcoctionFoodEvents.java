package net.mcreator.concoction.event;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.item.NetherSlopItem;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
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

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class ConcoctionFoodEvents {
    private static final ConcurrentHashMap<UUID, Integer> PRE_FOOD_LEVELS = new ConcurrentHashMap<>();

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

        if (living instanceof Player player
                && !player.level().isClientSide()
                && player.hasEffect(ConcoctionModMobEffects.SWEETNESS)
                && stack.has(DataComponents.FOOD)) {
            PRE_FOOD_LEVELS.put(player.getUUID(), player.getFoodData().getFoodLevel());
        }
    }

    @SubscribeEvent
    public static void onLivingUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide()) {
            return;
        }

        ItemStack used = event.getItem();
        applyBrainFreeze(living, used);

        if (!(living instanceof Player player) || used.getFoodProperties(living) == null) {
            return;
        }
        applySweetnessModifier(player, used);
        updateNetherSlopProgress(player, used);
    }

    private static void updateNetherSlopProgress(Player player, ItemStack used) {
        if (used.is(ConcoctionModItems.NETHER_SLOP.get())) {
            return;
        }

        int current = NetherSlopItem.getNetherSlopStack(player);
        if (current <= 0) {
            NetherSlopItem.setNetherSlopStack(player, 0);
            return;
        }

        NetherSlopItem.setNetherSlopStack(player, current - 3);
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

    private static void applySweetnessModifier(Player player, ItemStack stack) {
        if (!player.hasEffect(ConcoctionModMobEffects.SWEETNESS) || !stack.has(DataComponents.FOOD)) {
            return;
        }

        Integer beforeBoxed = PRE_FOOD_LEVELS.remove(player.getUUID());
        int before = beforeBoxed != null ? beforeBoxed : player.getFoodData().getFoodLevel();
        int afterVanilla = player.getFoodData().getFoodLevel();
        int originalHunger = stack.get(DataComponents.FOOD).nutrition();

        boolean isSweetFood = isSweetFlavor(stack, ConcoctionModDataComponents.FOOD_EFFECT.value())
                || isSweetFlavor(stack, ConcoctionModDataComponents.FOOD_EFFECT_2.value())
                || isSweetFlavor(stack, ConcoctionModDataComponents.FOOD_EFFECT_3.value())
                || isSweetFlavor(stack, ConcoctionModDataComponents.FOOD_EFFECT_4.value())
                || isSweetFlavor(stack, ConcoctionModDataComponents.FOOD_EFFECT_5.value());

        int target;
        if (isSweetFood) {
            int effectLevel = player.getEffect(ConcoctionModMobEffects.SWEETNESS).getAmplifier();
            float bonusPercent = Math.min(0.25F + 0.15F * effectLevel, 1.0F);
            int hungerMissingBefore = 20 - before;
            int bonusHunger = Mth.ceil(hungerMissingBefore * bonusPercent);
            target = Math.min(20, before + originalHunger + bonusHunger);
        } else {
            int reducedHunger = Mth.ceil(originalHunger * 0.5F);
            target = Math.min(20, before + reducedHunger);
        }

        target = Math.max(target, before);
        if (target != afterVanilla) {
            player.getFoodData().setFoodLevel(target);
        }
    }

    @SubscribeEvent
    public static void onLivingUseItemStop(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntity() instanceof Player player) {
            PRE_FOOD_LEVELS.remove(player.getUUID());
        }
    }

    private static boolean isSweetFlavor(ItemStack stack, DataComponentType<FoodEffectComponent> type) {
        if (!stack.has(type)) {
            return false;
        }

        FoodEffectComponent component = stack.get(type);
        return component != null && component.type() == FoodEffectType.SWEET;
    }
}
