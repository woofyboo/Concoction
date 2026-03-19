package net.mcreator.concoction.handlers;

import net.mcreator.concoction.init.ConcoctionModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.Comparator;
import java.util.List;

@EventBusSubscriber
public final class GreenThumbHandler {
    private static final int SEARCH_RADIUS = 6;

    private GreenThumbHandler() {
    }

    @SubscribeEvent
    public static void onBreakCrop(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }

        int greenThumbLevel = getGreenThumbLevel(player);
        if (greenThumbLevel <= 0 || !(player.getMainHandItem().getItem() instanceof HoeItem)) {
            return;
        }

        BlockState state = event.getState();
        if (!(state.getBlock() instanceof CropBlock crop) || !crop.isMaxAge(state)) {
            return;
        }

        getNearestImmatureCrops((ServerLevel) player.level(), event.getPos(), greenThumbLevel).forEach(target ->
                applyBonemealWithoutConsuming((ServerLevel) player.level(), target)
        );
    }

    @SubscribeEvent
    public static void onCropPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel) || !(event.getEntity() instanceof Player player)) {
            return;
        }

        int greenThumbLevel = getGreenThumbLevel(player);
        if (greenThumbLevel <= 0) {
            return;
        }

        BlockPos pos = event.getPos();
        BlockState placedState = serverLevel.getBlockState(pos);
        if (!(placedState.getBlock() instanceof CropBlock crop)) {
            return;
        }

        int boostedAge = Math.min(crop.getMaxAge(), crop.getAge(placedState) + greenThumbLevel);
        if (boostedAge > crop.getAge(placedState)) {
            serverLevel.setBlock(pos, crop.getStateForAge(boostedAge), 2);
        }
    }

    @SubscribeEvent
    public static void onBonemealUse(BonemealEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        int greenThumbLevel = getGreenThumbLevel(player);
        if (greenThumbLevel <= 0 || !event.isValidBonemealTarget()) {
            return;
        }

        boolean shouldSucceed = canApplyBonemeal(event.getLevel(), event.getPos())
                || canApplyBonemeal(event.getLevel(), event.getPos().north())
                || canApplyBonemeal(event.getLevel(), event.getPos().south())
                || canApplyBonemeal(event.getLevel(), event.getPos().east())
                || canApplyBonemeal(event.getLevel(), event.getPos().west());
        if (!shouldSucceed) {
            return;
        }

        event.setSuccessful(true);
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        boolean applied = false;
        applied |= applyBonemealWithoutConsuming(serverLevel, event.getPos());
        applied |= applyBonemealWithoutConsuming(serverLevel, event.getPos().north());
        applied |= applyBonemealWithoutConsuming(serverLevel, event.getPos().south());
        applied |= applyBonemealWithoutConsuming(serverLevel, event.getPos().east());
        applied |= applyBonemealWithoutConsuming(serverLevel, event.getPos().west());

        if (!applied) {
            return;
        }

        event.getStack().consume(1, player);
        serverLevel.gameEvent(player, GameEvent.ITEM_INTERACT_FINISH, event.getPos());
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.awardStat(Stats.ITEM_USED.get(Items.BONE_MEAL));
        }
    }

    @SubscribeEvent
    public static void onFarmlandTrample(BlockEvent.FarmlandTrampleEvent event) {
        if (event.getEntity() instanceof Player player && getGreenThumbLevel(player) > 0) {
            event.setCanceled(true);
        }
    }

    private static int getGreenThumbLevel(Player player) {
        Holder<net.minecraft.world.item.enchantment.Enchantment> enchantment = player.level()
                .registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(ConcoctionModEnchantments.GREEN_THUMB);
        int level = 0;

        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof HoeItem) {
                level = Math.max(level, stack.getEnchantmentLevel(enchantment));
            }
        }

        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.getItem() instanceof HoeItem) {
                level = Math.max(level, stack.getEnchantmentLevel(enchantment));
            }
        }

        return level;
    }

    private static List<BlockPos> getNearestImmatureCrops(ServerLevel level, BlockPos origin, int limit) {
        return BlockPos.betweenClosedStream(
                        origin.offset(-SEARCH_RADIUS, -1, -SEARCH_RADIUS),
                        origin.offset(SEARCH_RADIUS, 1, SEARCH_RADIUS)
                )
                .map(BlockPos::immutable)
                .filter(pos -> !pos.equals(origin))
                .filter(pos -> isImmatureCrop(level.getBlockState(pos)))
                .sorted(Comparator.comparingDouble(origin::distSqr))
                .limit(limit)
                .toList();
    }

    private static boolean isImmatureCrop(BlockState state) {
        return state.getBlock() instanceof CropBlock crop && !crop.isMaxAge(state);
    }

    private static boolean canApplyBonemeal(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof BonemealableBlock bonemealable
                && bonemealable.isValidBonemealTarget(level, pos, state);
    }

    private static boolean applyBonemealWithoutConsuming(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BonemealableBlock bonemealable) || !bonemealable.isValidBonemealTarget(level, pos, state)) {
            return false;
        }

        if (!bonemealable.isBonemealSuccess(level, level.getRandom(), pos, state)) {
            return false;
        }

        bonemealable.performBonemeal(level, level.getRandom(), pos, state);
        level.levelEvent(1505, pos, 15);
        return true;
    }
}
