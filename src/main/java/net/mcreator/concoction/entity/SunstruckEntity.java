package net.mcreator.concoction.entity;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;

import net.mcreator.concoction.init.ConcoctionModEntities;
import org.jetbrains.annotations.NotNull;

public class SunstruckEntity extends Zombie {
	private static final double DAY_SPEED_MULTIPLIER = 1.15;
	private static final double DAY_DAMAGE_MULTIPLIER = 2.0;
	private static final double NIGHT_DAMAGE_MULTIPLIER = 0.5;
	private static final double NIGHT_SPEED_MULTIPLIER = 1.0;
	
	private double baseSpeed;
	private double baseDamage;
	private boolean wasDay;

	public SunstruckEntity(EntityType<? extends Zombie> type, Level world) {
		super(type, world);
		xpReward = 0;
		setNoAi(false);
		setCanBreakDoors(true);
		
		// Сохраняем базовые значения атрибутов
		this.baseSpeed = this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
		this.baseDamage = this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
		this.wasDay = this.level().isDay();
		updateAttributes();
	}

	@Override
	public void tick() {
		super.tick();
		boolean isDay = this.level().isDay();
		if (isDay != wasDay) {
			wasDay = isDay;
			updateAttributes();
		}
	}

	private void updateAttributes() {
		boolean isDay = this.level().isDay();
		double speedMultiplier = isDay ? DAY_SPEED_MULTIPLIER : NIGHT_SPEED_MULTIPLIER;
		double damageMultiplier = isDay ? DAY_DAMAGE_MULTIPLIER : NIGHT_DAMAGE_MULTIPLIER;
		this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(baseSpeed * speedMultiplier);
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(baseDamage * damageMultiplier);
	}

	@Override
	public boolean hurt(@NotNull DamageSource source, float amount) {
		if (!this.level().isDay() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return super.hurt(source, amount * 2.0f);
		}
		return super.hurt(source, amount);
	}
	@Override
	public boolean doHurtTarget(Entity p_32892_) {
		boolean flag = super.doHurtTarget(p_32892_);
		if (flag && this.getMainHandItem().isEmpty() && p_32892_ instanceof LivingEntity) {

			float rand = this.level().random.nextFloat();
			if (rand <= 0.25f) {
				float f = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
				((LivingEntity)p_32892_).addEffect(new MobEffectInstance(ConcoctionModMobEffects.SUNSTRUCK_EFFECT, 140 * (int)f), this);
			}
		}

		return flag;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
	}

	@Override
	public boolean isSunSensitive() {
		return false; // Не горит на солнце
	}

	@Override
	public Vec3 getPassengerRidingPosition(Entity entity) {
		return super.getPassengerRidingPosition(entity).add(0, -0.35F, 0);
	}

	@Override
	public SoundEvent getAmbientSound() {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.ambient"));
	}

	@Override
	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.step")), 0.15f, 1);
	}

	@Override
	public SoundEvent getHurtSound(DamageSource ds) {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.hurt"));
	}

	@Override
	public SoundEvent getDeathSound() {
		return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.death"));
	}

	public static void init(RegisterSpawnPlacementsEvent event) {
		event.register(ConcoctionModEntities.SUNSTRUCK.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				(entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(world, pos, random) && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)),
				RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	public static AttributeSupplier.@NotNull Builder createAttributes() {
		return Zombie.createAttributes()
			.add(Attributes.MAX_HEALTH, 30.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.25D)
			.add(Attributes.ATTACK_DAMAGE, 3.0D)
			.add(Attributes.FOLLOW_RANGE, 16.0D)
			.add(Attributes.STEP_HEIGHT, 0.6D);
	}
}
