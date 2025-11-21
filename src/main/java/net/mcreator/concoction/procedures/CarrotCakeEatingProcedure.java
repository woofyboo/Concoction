package net.mcreator.concoction.procedures;

import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

import net.mcreator.concoction.init.ConcoctionModBlocks;

import javax.annotation.Nullable;

@EventBusSubscriber
public class CarrotCakeEatingProcedure {
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != event.getEntity().getUsedItemHand())
            return;
        if (event.getLevel().isClientSide())
            return;

        execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(),
                event.getLevel().getBlockState(event.getPos()), event.getEntity());
    }

    public static void execute(LevelAccessor world, double x, double y, double z, BlockState blockstate, Entity entity) {
        execute(null, world, x, y, z, blockstate, entity);
    }

    private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, BlockState blockstate, Entity entity) {
        if (entity == null)
            return;

        if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() == ConcoctionModBlocks.CARROT_CAKE.get()) {
            if (!entity.isShiftKeyDown()) {
                int bites = (blockstate.getBlock().getStateDefinition().getProperty("bites") instanceof IntegerProperty ip)
                        ? blockstate.getValue(ip)
                        : -1;

                if (bites != 6) {
                    boolean isCreative = entity instanceof ServerPlayer sp && sp.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
                    int food = (entity instanceof Player p) ? p.getFoodData().getFoodLevel() : 0;

                    if (isCreative || food < 20) {
                        int newBites = bites + 1;
                        BlockPos pos = BlockPos.containing(x, y, z);
                        BlockState state = world.getBlockState(pos);
                        if (state.getBlock().getStateDefinition().getProperty("bites") instanceof IntegerProperty ip &&
                                ip.getPossibleValues().contains(newBites)) {
                            world.setBlock(pos, state.setValue(ip, newBites), 3);
                        }

                        if (entity instanceof LivingEntity living)
                            living.swing(InteractionHand.MAIN_HAND, true);
                        if (entity instanceof Player player) {
                            player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() + 4);
                            player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel() + 1.2F);
                        }

                        if (event instanceof ICancellableEvent cancellable) {
                            cancellable.setCanceled(true);
                        }
                    }
                } else {
                    world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);

                    if (entity instanceof LivingEntity living)
                        living.swing(InteractionHand.MAIN_HAND, true);
                    if (entity instanceof Player player) {
                        player.getFoodData().setFoodLevel(player.getFoodData().getFoodLevel() + 4);
                        player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel() + 1.2F);
                    }

                    if (event instanceof ICancellableEvent cancellable) {
                        cancellable.setCanceled(true);
                    }
                }
            }
        }
    }
}
