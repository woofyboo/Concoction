package net.mcreator.concoction.event;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.item.NetherSlopItem;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class ConcoctionFoodEvents {
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

        if (living instanceof Player player && used.getFoodProperties(living) != null) {
            updateNetherSlopProgress(player, used);
        }
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
}
