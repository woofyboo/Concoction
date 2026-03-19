package net.mcreator.concoction.client;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.handlers.OvergrownToolHandler;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(
        modid = ConcoctionMod.MODID,
        bus = EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public final class OvergrownToolClientProperties {
    private static final ResourceLocation OVERGROWN_STATE = ResourceLocation.fromNamespaceAndPath(
            ConcoctionMod.MODID,
            "overgrown_state"
    );

    private OvergrownToolClientProperties() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> register(
                ConcoctionModItems.OVERGROWN_PICKAXE.get(),
                ConcoctionModItems.OVERGROWN_AXE.get(),
                ConcoctionModItems.OVERGROWN_SHOVEL.get(),
                ConcoctionModItems.OVERGROWN_HOE.get(),
                ConcoctionModItems.OVERGROWN_SWORD.get()
        ));
    }

    private static void register(Item... items) {
        for (Item item : items) {
            ItemProperties.register(item, OVERGROWN_STATE, (stack, level, entity, seed) -> getState(stack, entity));
        }
    }

    private static float getState(ItemStack stack, @Nullable LivingEntity entity) {
        boolean broken = isNearlyBroken(stack);
        boolean charging = isCharging(stack, entity);

        if (broken && charging) {
            return 1.0F;
        }

        if (broken) {
            return 0.67F;
        }

        return charging ? 0.34F : 0.0F;
    }

    private static boolean isCharging(ItemStack stack, @Nullable LivingEntity entity) {
        Player player = resolvePlayer(stack, entity);
        return player != null && OvergrownToolHandler.canChargeFromSun(player, stack);
    }

    private static boolean isNearlyBroken(ItemStack stack) {
        return !stack.isEmpty() && stack.getMaxDamage() - stack.getDamageValue() <= 1;
    }

    @Nullable
    private static Player resolvePlayer(ItemStack stack, @Nullable LivingEntity entity) {
        if (entity instanceof Player player) {
            return player;
        }

        Player localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) {
            return null;
        }

        for (ItemStack inventoryStack : localPlayer.getInventory().items) {
            if (inventoryStack == stack) {
                return localPlayer;
            }
        }

        for (ItemStack inventoryStack : localPlayer.getInventory().offhand) {
            if (inventoryStack == stack) {
                return localPlayer;
            }
        }

        return null;
    }
}
