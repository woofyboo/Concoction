package net.mcreator.concoction.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;                // === pickup rule ===
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import net.mcreator.concoction.init.ConcoctionModEntities;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModMobEffects; // (если нужно для эффекта при попадании)
import net.minecraft.world.effect.MobEffectInstance;

import javax.annotation.Nullable;

public class SunstruckArrowEntity extends AbstractArrow implements ItemSupplier {

    public static final ItemStack PROJECTILE_ITEM = new ItemStack(ConcoctionModItems.OVERGROWN_ARROW.get());

    private int knockback = 0;

    public SunstruckArrowEntity(EntityType<? extends SunstruckArrowEntity> type, Level world) {
        super(type, world);
        // без стрелка оставляем ванильный дефолт (обычно ALLOWED)
    }

    public SunstruckArrowEntity(EntityType<? extends SunstruckArrowEntity> type, double x, double y, double z, Level world, @Nullable ItemStack firedFromWeapon) {
        super(type, x, y, z, world, PROJECTILE_ITEM, firedFromWeapon);
        // без стрелка — оставляем дефолт
    }

    public SunstruckArrowEntity(EntityType<? extends SunstruckArrowEntity> type, LivingEntity shooter, Level world, @Nullable ItemStack firedFromWeapon) {
        super(type, shooter, world, PROJECTILE_ITEM, firedFromWeapon);
        setPickupByShooter(shooter); // === pickup rule ===
    }

    // === pickup rule ===
    private void setPickupByShooter(@Nullable LivingEntity shooter) {
        if (shooter instanceof Player p) {
            // стрелял игрок:
            // - если был в креативе → CREATIVE_ONLY (выживание не подберёт; креатив подберёт без выдачи предмета)
            // - если был в выживании → ALLOWED
            this.pickup = p.getAbilities().instabuild ? Pickup.CREATIVE_ONLY : Pickup.ALLOWED;
        } else {
            // стрелял не игрок (диспенсер/моб) — как у ванили: обычно ALLOWED
            this.pickup = Pickup.ALLOWED;
        }
    }
    // === /pickup rule ===

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack getItem() {
        return PROJECTILE_ITEM;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        // что выдастся при подборе (если правило ALLOWED)
        return new ItemStack(ConcoctionModItems.OVERGROWN_ARROW.get());
    }

    @Override
    protected void doPostHurtEffects(LivingEntity entity) {
        super.doPostHurtEffects(entity);
        entity.setArrowCount(entity.getArrowCount() - 1);

        // (опционально) SUNSTRUCK: 20s, уровень растёт до V (амплифаер 0..4), длительность обновляется
        if (!this.level().isClientSide) {
            final int DURATION = 20 * 20;
            final int MAX_AMP = 4;
            var effect = ConcoctionModMobEffects.SUNSTRUCK_EFFECT;
            var cur = entity.getEffect(effect);
            int amp = (cur == null) ? 0 : Math.min(cur.getAmplifier() + 1, MAX_AMP);
            entity.addEffect(new MobEffectInstance(effect, DURATION, amp, false, true, true));
        }
    }

    public void setKnockback(int knockback) {
        this.knockback = Math.max(0, knockback);
    }

    @Override
    protected void doKnockback(LivingEntity livingEntity, DamageSource damageSource) {
        if (this.knockback > 0) {
            double resist = Math.max(0.0, 1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            Vec3 flatDir = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize();
            Vec3 force = flatDir.scale(this.knockback * 0.6 * resist);
            if (force.lengthSqr() > 0.0) {
                livingEntity.push(force.x, 0.1, force.z);
            }
        }
    }

    // ================== Фабрики выстрела ==================

    public static SunstruckArrowEntity shoot(Level world, LivingEntity shooter, RandomSource random) {
        return shoot(world, shooter, random, 1f, 5.0, 5);
    }

    public static SunstruckArrowEntity shoot(Level world, LivingEntity shooter, RandomSource random, float pullingPower) {
        return shoot(world, shooter, random, pullingPower * 1f, 5.0, 5);
    }

    public static SunstruckArrowEntity shoot(Level world, LivingEntity shooter, RandomSource random, float power, double baseDamage, int knockback) {
        ItemStack firedFrom = shooter.getUseItem();
        if (firedFrom.isEmpty()) firedFrom = shooter.getMainHandItem();
        if (firedFrom.isEmpty()) firedFrom = shooter.getOffhandItem();

        SunstruckArrowEntity arrow = new SunstruckArrowEntity(ConcoctionModEntities.SUNSTRUCK_ARROW.get(), shooter, world, firedFrom);
        arrow.setPickupByShooter(shooter); // === pickup rule ===

        arrow.shoot(shooter.getViewVector(1.0f).x, shooter.getViewVector(1.0f).y, shooter.getViewVector(1.0f).z, power * 2.0f, 0.0f);
        arrow.setSilent(true);
        arrow.setCritArrow(false);
        arrow.setBaseDamage(baseDamage);
        arrow.setKnockback(knockback);

        world.addFreshEntity(arrow);

        world.playSound(
                null,
                shooter.getX(), shooter.getY(), shooter.getZ(),
                BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.arrow.shoot")),
                SoundSource.PLAYERS,
                1.0f,
                1.0f / (random.nextFloat() * 0.5f + 1.0f) + (power / 2.0f)
        );

        return arrow;
    }

    public static SunstruckArrowEntity shoot(LivingEntity shooter, LivingEntity target) {
        ItemStack firedFrom = shooter.getUseItem();
        if (firedFrom.isEmpty()) firedFrom = shooter.getMainHandItem();
        if (firedFrom.isEmpty()) firedFrom = shooter.getOffhandItem();

        SunstruckArrowEntity arrow = new SunstruckArrowEntity(ConcoctionModEntities.SUNSTRUCK_ARROW.get(), shooter, shooter.level(), firedFrom);
        arrow.setPickupByShooter(shooter); // === pickup rule ===

        double dx = target.getX() - shooter.getX();
        double dz = target.getZ() - shooter.getZ();
        double dy = target.getY() + target.getEyeHeight() - 1.1 - arrow.getY();
        arrow.shoot(dx, dy + Math.hypot(dx, dz) * 0.2F, dz, 2.0f, 12.0F);

        arrow.setSilent(true);
        arrow.setBaseDamage(5.0);
        arrow.setKnockback(5);
        arrow.setCritArrow(false);

        shooter.level().addFreshEntity(arrow);
        shooter.level().playSound(
                null, shooter.getX(), shooter.getY(), shooter.getZ(),
                BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.arrow.shoot")),
                SoundSource.PLAYERS, 1.0f,
                1.0f / (RandomSource.create().nextFloat() * 0.5f + 1.0f)
        );

        return arrow;
    }
}
