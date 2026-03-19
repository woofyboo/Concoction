package net.mcreator.concoction.event;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class BlockInteractionEvents {
    private BlockInteractionEvents() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide() || event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)
                || !event.getLevel().getBlockState(event.getPos()).is(Blocks.CRYING_OBSIDIAN)) {
            return;
        }

        ItemStack heldItem = player.getMainHandItem();
        if (!heldItem.is(Items.GLASS_BOTTLE)) {
            return;
        }

        ItemStack filledBottle = new ItemStack(ConcoctionModItems.OBSIDIAN_TEARS_BOTTLE.get());
        if (heldItem.getCount() > 1) {
            if (!player.isCreative()) {
                heldItem.shrink(1);
            }
            ItemHandlerHelper.giveItemToPlayer(player, filledBottle);
        } else if (player.isCreative()) {
            ItemHandlerHelper.giveItemToPlayer(player, filledBottle);
        } else {
            player.setItemInHand(InteractionHand.MAIN_HAND, filledBottle);
            player.getInventory().setChanged();
        }

        player.swing(InteractionHand.MAIN_HAND, true);
        event.getLevel().playSound(null, event.getPos(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
        event.getLevel().setBlock(event.getPos(), Blocks.OBSIDIAN.defaultBlockState(), 3);

        ((ICancellableEvent) event).setCanceled(true);
    }
}
