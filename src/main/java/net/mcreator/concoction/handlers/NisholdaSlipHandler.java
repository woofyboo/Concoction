package net.mcreator.concoction.handlers;

import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class NisholdaSlipHandler {
    private static final String ACTIVE_KEY = "concoction_nisholda_normalized_slip";
    private static final TagKey<Item> DISH_TAG =
            TagKey.create(Registries.ITEM, ResourceLocation.parse("c:foods/dish"));

    private NisholdaSlipHandler() {
    }

    public static void grantSlipNormalization(Player player) {
        player.getPersistentData().putBoolean(ACTIVE_KEY, true);
    }

    public static boolean hasSlipNormalization(Player player) {
        return player.getPersistentData().getBoolean(ACTIVE_KEY);
    }

    @SubscribeEvent
    public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ItemStack used = event.getItem();
        if (used.is(ConcoctionModItems.NISHOLDA.get())) {
            grantSlipNormalization(player);
            return;
        }

        if (used.is(DISH_TAG)) {
            clearSlipNormalization(player);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        Player oldPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        if (event.isWasDeath()) {
            clearSlipNormalization(newPlayer);
            return;
        }

        CompoundTag oldData = oldPlayer.getPersistentData();
        if (oldData.getBoolean(ACTIVE_KEY)) {
            newPlayer.getPersistentData().putBoolean(ACTIVE_KEY, true);
        }
    }

    private static void clearSlipNormalization(Player player) {
        player.getPersistentData().remove(ACTIVE_KEY);
    }
}
