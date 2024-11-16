package net.mcreator.concoction.procedures;

import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;

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
		double rY = 0;
		if (entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(ConcoctionModMobEffects.INSTABILITY)) {
			if (!damagesource.is(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("concoction:rift")))) {
				positionX = x;
				positionY = y;
				positionZ = z;
				rY = Mth.nextInt(RandomSource.create(), -3, 3);
				if ((entity.level().dimension()) == Level.OVERWORLD) {
					if (positionY + rY > -64) {
						{
							Entity _ent = entity;
							_ent.teleportTo((positionX + Mth.nextInt(RandomSource.create(), -16, 16)), (positionY + rY), (positionZ + Mth.nextInt(RandomSource.create(), -16, 16)));
							if (_ent instanceof ServerPlayer _serverPlayer)
								_serverPlayer.connection.teleport((positionX + Mth.nextInt(RandomSource.create(), -16, 16)), (positionY + rY), (positionZ + Mth.nextInt(RandomSource.create(), -16, 16)), _ent.getYRot(), _ent.getXRot());
						}
					} else {
						rY = 1;
						{
							Entity _ent = entity;
							_ent.teleportTo((positionX + Mth.nextInt(RandomSource.create(), -16, 16)), (positionY + rY), (positionZ + Mth.nextInt(RandomSource.create(), -16, 16)));
							if (_ent instanceof ServerPlayer _serverPlayer)
								_serverPlayer.connection.teleport((positionX + Mth.nextInt(RandomSource.create(), -16, 16)), (positionY + rY), (positionZ + Mth.nextInt(RandomSource.create(), -16, 16)), _ent.getYRot(), _ent.getXRot());
						}
					}
				} else {
					if (positionY + rY > 0) {
						{
							Entity _ent = entity;
							_ent.teleportTo((positionX + Mth.nextInt(RandomSource.create(), -16, 16)), (positionY + rY), (positionZ + Mth.nextInt(RandomSource.create(), -16, 16)));
							if (_ent instanceof ServerPlayer _serverPlayer)
								_serverPlayer.connection.teleport((positionX + Mth.nextInt(RandomSource.create(), -16, 16)), (positionY + rY), (positionZ + Mth.nextInt(RandomSource.create(), -16, 16)), _ent.getYRot(), _ent.getXRot());
						}
					} else {
						rY = 1;
						{
							Entity _ent = entity;
							_ent.teleportTo((positionX + Mth.nextInt(RandomSource.create(), -16, 16)), (positionY + rY), (positionZ + Mth.nextInt(RandomSource.create(), -16, 16)));
							if (_ent instanceof ServerPlayer _serverPlayer)
								_serverPlayer.connection.teleport((positionX + Mth.nextInt(RandomSource.create(), -16, 16)), (positionY + rY), (positionZ + Mth.nextInt(RandomSource.create(), -16, 16)), _ent.getYRot(), _ent.getXRot());
						}
					}
				}
			}
		}
	}
}
