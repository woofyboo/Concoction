package net.mcreator.concoction.procedures;

import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

import net.mcreator.concoction.init.ConcoctionModMobEffects;

import javax.annotation.Nullable;

@EventBusSubscriber
public class InstabilityTakesDamageProcedure {
	@SubscribeEvent
	public static void onEntityAttacked(LivingDamageEvent.Post event) {
		if (event.getEntity() != null) {
			execute(event, event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getSource(), event.getEntity());
		}
	}

	public static void execute(double x, double y, double z, DamageSource damagesource, Entity entity) {
		execute(null, x, y, z, damagesource, entity);
	}

	private static void execute(@Nullable Event event, double x, double y, double z, DamageSource damagesource, Entity entity) {
		if (damagesource == null || entity == null)
			return;
		double positionX = 0;
		double positionY = 0;
		double positionZ = 0;
		if (entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(ConcoctionModMobEffects.INSTABILITY)) {
			if (!damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("concoction:rift")))) {
				positionX = x;
				positionY = y;
				positionZ = z;
				if ((entity.level().dimension()) == Level.OVERWORLD) {
					if (entity instanceof ServerPlayer _player && !_player.level().isClientSide()) {
						ResourceKey<Level> destinationType = Level.NETHER;
						if (_player.level().dimension() == destinationType)
							return;
						ServerLevel nextLevel = _player.server.getLevel(destinationType);
						if (nextLevel != null) {
							_player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
							_player.teleportTo(nextLevel, _player.getX(), _player.getY(), _player.getZ(), _player.getYRot(), _player.getXRot());
							_player.connection.send(new ClientboundPlayerAbilitiesPacket(_player.getAbilities()));
							for (MobEffectInstance _effectinstance : _player.getActiveEffects())
								_player.connection.send(new ClientboundUpdateMobEffectPacket(_player.getId(), _effectinstance, false));
							_player.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
						}
					}
					if (positionY >= 128) {
						{
							Entity _ent = entity;
							_ent.teleportTo((positionX / 8), 125, (positionX / 8));
							if (_ent instanceof ServerPlayer _serverPlayer)
								_serverPlayer.connection.teleport((positionX / 8), 125, (positionX / 8), _ent.getYRot(), _ent.getXRot());
						}
					} else {
						{
							Entity _ent = entity;
							_ent.teleportTo((positionX / 8), positionY, (positionX / 8));
							if (_ent instanceof ServerPlayer _serverPlayer)
								_serverPlayer.connection.teleport((positionX / 8), positionY, (positionX / 8), _ent.getYRot(), _ent.getXRot());
						}
					}
				} else if ((entity.level().dimension()) == Level.NETHER) {
					if (entity instanceof ServerPlayer _player && !_player.level().isClientSide()) {
						ResourceKey<Level> destinationType = Level.OVERWORLD;
						if (_player.level().dimension() == destinationType)
							return;
						ServerLevel nextLevel = _player.server.getLevel(destinationType);
						if (nextLevel != null) {
							_player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 0));
							_player.teleportTo(nextLevel, _player.getX(), _player.getY(), _player.getZ(), _player.getYRot(), _player.getXRot());
							_player.connection.send(new ClientboundPlayerAbilitiesPacket(_player.getAbilities()));
							for (MobEffectInstance _effectinstance : _player.getActiveEffects())
								_player.connection.send(new ClientboundUpdateMobEffectPacket(_player.getId(), _effectinstance, false));
							_player.connection.send(new ClientboundLevelEventPacket(1032, BlockPos.ZERO, 0, false));
						}
					}
					{
						Entity _ent = entity;
						_ent.teleportTo((positionX * 8), positionY, (positionX * 8));
						if (_ent instanceof ServerPlayer _serverPlayer)
							_serverPlayer.connection.teleport((positionX * 8), positionY, (positionX * 8), _ent.getYRot(), _ent.getXRot());
					}
				}
				if (entity instanceof LivingEntity _entity)
					_entity.removeAllEffects();
			}
		}
	}
}
