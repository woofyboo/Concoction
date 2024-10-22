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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.init.ConcoctionModBlocks;

import javax.annotation.Nullable;

@EventBusSubscriber
public class ChocolateMintCakeEatingProcedure {
	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.getHand() != event.getEntity().getUsedItemHand())
			return;
		execute(event, event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getLevel().getBlockState(event.getPos()), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, BlockState blockstate, Entity entity) {
		execute(null, world, x, y, z, blockstate, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, BlockState blockstate, Entity entity) {
		if (entity == null)
			return;
		if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == ConcoctionModBlocks.MINT_CHOCOLATE_CAKE.get()) {
			if (!entity.isShiftKeyDown()) {
				if ((blockstate.getBlock().getStateDefinition().getProperty("bites") instanceof IntegerProperty _getip4 ? blockstate.getValue(_getip4) : -1) != 6) {
					if (new Object() {
						public boolean checkGamemode(Entity _ent) {
							if (_ent instanceof ServerPlayer _serverPlayer) {
								return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.CREATIVE;
							} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
								return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null
										&& Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.CREATIVE;
							}
							return false;
						}
					}.checkGamemode(entity) || (entity instanceof Player _plr ? _plr.getFoodData().getFoodLevel() : 0) < 20) {
						{
							int _value = (int) ((blockstate.getBlock().getStateDefinition().getProperty("bites") instanceof IntegerProperty _getip8 ? blockstate.getValue(_getip8) : -1) + 1);
							BlockPos _pos = BlockPos.containing(x, y, z);
							BlockState _bs = world.getBlockState(_pos);
							if (_bs.getBlock().getStateDefinition().getProperty("bites") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
								world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
						}
						if (entity instanceof LivingEntity _entity)
							_entity.swing(InteractionHand.MAIN_HAND, true);
						if (entity instanceof Player _player)
							_player.getFoodData().setFoodLevel((int) ((entity instanceof Player _plr ? _plr.getFoodData().getFoodLevel() : 0) + 2));
						if (entity instanceof Player _player)
							_player.getFoodData().setSaturation((float) ((entity instanceof Player _plr ? _plr.getFoodData().getSaturationLevel() : 0) + 0.4));
						if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
							_entity.addEffect(new MobEffectInstance(ConcoctionModMobEffects.MINTY_BREATH, 600, 0));
						if (event instanceof ICancellableEvent _cancellable) {
							_cancellable.setCanceled(true);
						}
					}
				} else {
					world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
					if (entity instanceof LivingEntity _entity)
						_entity.swing(InteractionHand.MAIN_HAND, true);
					if (entity instanceof Player _player)
						_player.getFoodData().setFoodLevel((int) ((entity instanceof Player _plr ? _plr.getFoodData().getFoodLevel() : 0) + 2));
					if (entity instanceof Player _player)
						_player.getFoodData().setSaturation((float) ((entity instanceof Player _plr ? _plr.getFoodData().getSaturationLevel() : 0) + 0.4));
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(ConcoctionModMobEffects.MINTY_BREATH, 600, 0));
					if (event instanceof ICancellableEvent _cancellable) {
						_cancellable.setCanceled(true);
					}
				}
			}
		}
	}
}
