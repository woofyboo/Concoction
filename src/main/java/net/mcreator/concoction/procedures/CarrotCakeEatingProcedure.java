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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.mcreator.concoction.init.ConcoctionModGameRules;

import javax.annotation.Nullable;

@EventBusSubscriber
public class CarrotCakeEatingProcedure {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != event.getEntity().getUsedItemHand())
            return;

        // Проверяем, стоит ли вообще перехватывать (и на клиенте, и на сервере)
        if (!shouldEat(event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(),
                event.getLevel().getBlockState(event.getPos()), event.getEntity())) {
            return;
        }

        // Отменяем стандартный обработчик, чтобы не было попытки поставить торт/другой блок
        if (event instanceof ICancellableEvent cancellable) {
            cancellable.setCanceled(true);
        }

        // Логику еды выполняем только на сервере
        if (!event.getLevel().isClientSide()) {
            execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(),
                    event.getLevel().getBlockState(event.getPos()), event.getEntity());
        }
    }

    private static boolean shouldEat(LevelAccessor world, double x, double y, double z,
                                     BlockState blockstate, Entity entity) {
        if (entity == null)
            return false;
        if (!(entity instanceof Player player))
            return false;

        if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() != ConcoctionModBlocks.CARROT_CAKE.get())
            return false;

        if (entity.isShiftKeyDown())
            return false;

        boolean canAlwaysEat = false;
        if (world instanceof Level level) {
            canAlwaysEat = level.getGameRules().getBoolean(ConcoctionModGameRules.CAN_ALWAYS_EAT);
        }

        boolean isCreative = player instanceof ServerPlayer sp && sp.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
        // Используем canEat, но учитываем наш геймрул
        boolean canEatNow = player.getFoodData().getFoodLevel() < 20 || canAlwaysEat || isCreative;

        return canEatNow;
    }

    public static void execute(LevelAccessor world, double x, double y, double z,
                               BlockState blockstate, Entity entity) {
        execute(null, world, x, y, z, blockstate, entity);
    }

    private static void execute(@Nullable Event event,
                                LevelAccessor world,
                                double x, double y, double z,
                                BlockState blockstate,
                                Entity entity) {
        if (entity == null)
            return;
        if (!(entity instanceof Player player))
            return;

        if (world.getBlockState(BlockPos.containing(x, y, z)).getBlock() != ConcoctionModBlocks.CARROT_CAKE.get())
            return;

        int bites = blockstate.getBlock().getStateDefinition().getProperty("bites") instanceof IntegerProperty prop
                ? blockstate.getValue(prop)
                : -1;

        boolean isLastBite = (bites == 6);

        if (!isLastBite) {
            if (blockstate.getBlock().getStateDefinition().getProperty("bites") instanceof IntegerProperty prop) {
                int newVal = bites + 1;
                BlockPos pos = BlockPos.containing(x, y, z);
                BlockState bs = world.getBlockState(pos);
                if (bs.getBlock().getStateDefinition().getProperty("bites") instanceof IntegerProperty p
                        && p.getPossibleValues().contains(newVal)) {
                    world.setBlock(pos, bs.setValue(p, newVal), 3);
                }
            }
        } else {
            world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
        }

        if (entity instanceof LivingEntity living) {
            living.swing(InteractionHand.MAIN_HAND, true);
        }

        // Нормальная ванильная логика еды: тут уже нет переполнения > 20
        player.getFoodData().eat(4, 1.2F);
    }
}
