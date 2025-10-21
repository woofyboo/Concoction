package net.mcreator.concoction.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.mcreator.concoction.init.ConcoctionModEntities;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.world.effect.MobEffectInstance;

import javax.annotation.Nullable;

public class SunstruckArrowEntity extends AbstractArrow implements ItemSupplier {

    public static final ItemStack PROJECTILE_ITEM = new ItemStack(ConcoctionModItems.OVERGROWN_ARROW.get());
    private int knockback = 0;

    private boolean tripleFired = false; // выстрел из Multishot
    private boolean creativeShot = false; // стрелял ли игрок из креатива

    public SunstruckArrowEntity(EntityType<? extends SunstruckArrowEntity> type, Level world) {
        super(type, world);
    }

    public SunstruckArrowEntity(EntityType<? extends SunstruckArrowEntity> type, LivingEntity shooter, Level world, @Nullable ItemStack firedFromWeapon) {
        super(type, shooter, world, PROJECTILE_ITEM, firedFromWeapon);
        this.tripleFired = hasMultiShot(firedFromWeapon);
        if (shooter instanceof Player p) {
            this.creativeShot = p.getAbilities().instabuild;
        }
    }

    // === проверка на Multishot ===
    private static boolean hasMultiShot(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        ItemEnchantments ench = stack.get(DataComponents.ENCHANTMENTS);
        if (ench != null) {
            for (var holder : ench.keySet()) {
                if (holder.is(Enchantments.MULTISHOT) && ench.getLevel(holder) > 0) return true;
            }
        }

        ItemEnchantments stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
        if (stored != null) {
            for (var holder : stored.keySet()) {
                if (holder.is(Enchantments.MULTISHOT) && stored.getLevel(holder) > 0) return true;
            }
        }

        return false;
    }

    // === основная логика подбора при Multishot и креативе ===
    @Override
    public void tick() {
        super.tick();

        if (this.tickCount == 1 && this.getOwner() instanceof LivingEntity shooter) {
            // если стрелял игрок в креативе → CREATIVE_ONLY всегда
            if (creativeShot) {
                this.pickup = Pickup.CREATIVE_ONLY;
                return;
            }

            // если выстрел с Multishot — разрешаем только центральную стрелу
            if (this.tripleFired) {
                Vec3 look = shooter.getViewVector(1.0F);
                double dot = look.normalize().dot(this.getDeltaMovement().normalize());
                boolean isCentral = dot > Math.cos(Math.toRadians(5)); // ~5° допуск

                this.pickup = isCentral ? Pickup.ALLOWED : Pickup.DISALLOWED;
            } else {
                // обычный выстрел
                if (shooter instanceof Player p) {
                    this.pickup = p.getAbilities().instabuild ? Pickup.CREATIVE_ONLY : Pickup.ALLOWED;
                } else {
                    this.pickup = Pickup.ALLOWED;
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack getItem() {
        return PROJECTILE_ITEM;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ConcoctionModItems.OVERGROWN_ARROW.get());
    }

    @Override
    protected void doPostHurtEffects(LivingEntity entity) {
        super.doPostHurtEffects(entity);
        entity.setArrowCount(entity.getArrowCount() - 1);

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
            if (force.lengthSqr() > 0.0) livingEntity.push(force.x, 0.1, force.z);
        }
    }

    // ================== фабрики выстрела ==================
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
        arrow.shoot(shooter.getViewVector(1.0f).x, shooter.getViewVector(1.0f).y, shooter.getViewVector(1.0f).z, power * 2.0f, 0.0f);
        arrow.setSilent(true);
        arrow.setCritArrow(false);
        arrow.setBaseDamage(baseDamage);
        arrow.setKnockback(knockback);

        world.addFreshEntity(arrow);
        world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.arrow.shoot")),
                SoundSource.PLAYERS,
                1.0f,
                1.0f / (random.nextFloat() * 0.5f + 1.0f) + (power / 2.0f));
        return arrow;
    }

    public static SunstruckArrowEntity shoot(LivingEntity shooter, LivingEntity target) {
        ItemStack firedFrom = shooter.getUseItem();
        if (firedFrom.isEmpty()) firedFrom = shooter.getMainHandItem();
        if (firedFrom.isEmpty()) firedFrom = shooter.getOffhandItem();

        SunstruckArrowEntity arrow = new SunstruckArrowEntity(ConcoctionModEntities.SUNSTRUCK_ARROW.get(), shooter, shooter.level(), firedFrom);
        double dx = target.getX() - shooter.getX();
        double dz = target.getZ() - shooter.getZ();
        double dy = target.getY() + target.getEyeHeight() - 1.1 - arrow.getY();
        arrow.shoot(dx, dy + Math.hypot(dx, dz) * 0.2F, dz, 2.0f, 12.0F);
        arrow.setSilent(true);
        arrow.setBaseDamage(5.0);
        arrow.setKnockback(5);
        arrow.setCritArrow(false);

        shooter.level().addFreshEntity(arrow);
        shooter.level().playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.arrow.shoot")),
                SoundSource.PLAYERS,
                1.0f,
                1.0f / (RandomSource.create().nextFloat() * 0.5f + 1.0f));
        return arrow;
    }
}
