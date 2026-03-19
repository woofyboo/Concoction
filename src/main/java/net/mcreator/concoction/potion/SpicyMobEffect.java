package net.mcreator.concoction.potion;

import com.mojang.datafixers.util.Pair;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.List;

public class SpicyMobEffect extends MobEffect {

    private static final ResourceLocation SPICY_ATTACK_SPEED = ResourceLocation.fromNamespaceAndPath("concoction", "spicy_attack_speed");
    private static final String NBT_TICK_KEY = "concoction_spicy_tick";

    public SpicyMobEffect() {
        super(MobEffectCategory.NEUTRAL, -46336);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void addAttributeModifiers(AttributeMap attributeMap, int amplifier) {
        AttributeInstance attackSpeedAttribute = attributeMap.getInstance(Attributes.ATTACK_SPEED);
        if (attackSpeedAttribute != null) {
            double increaseValue = (amplifier + 1) * 0.15;
            AttributeModifier attackSpeedModifier = new AttributeModifier(
                SPICY_ATTACK_SPEED,
                increaseValue,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            );
            attackSpeedAttribute.addTransientModifier(attackSpeedModifier);
        }
        super.addAttributeModifiers(attributeMap, amplifier);
    }

    @Override
    public void removeAttributeModifiers(AttributeMap attributeMap) {
        AttributeInstance attackSpeedAttribute = attributeMap.getInstance(Attributes.ATTACK_SPEED);
        if (attackSpeedAttribute != null) {
            attackSpeedAttribute.removeModifier(SPICY_ATTACK_SPEED);
        }
        super.removeAttributeModifiers(attributeMap);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        // Если есть CREAMY — «молочность» гасит остроту: снимаем SPICY и выходим
        if (entity.hasEffect(ConcoctionModMobEffects.CREAMY)) {
            entity.removeEffect(ConcoctionModMobEffects.SPICY);
            return false;
        }

        LevelAccessor world = entity.level();

        if (entity.hasEffect(ConcoctionModMobEffects.SPICY)) {
            if (world instanceof Level level && !level.isClientSide() && entity instanceof ServerPlayer player) {
                // Индивидуальный счётчик тиков на сущности (чтобы не был общим для всех)
                int tickCounter = player.getPersistentData().getInt(NBT_TICK_KEY);

                int tickInterval = Math.max(20, 60 - amplifier * 10); // минимум 1 секунда
                if (tickCounter >= tickInterval && player.getHealth() > 1.0F) {
                    // Наносим урон «жгучестью»
                    DamageSource src = new DamageSource(
                        world.holderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("concoction:spicy_damage")))
                    );
                    entity.hurt(src, 1.0F);
                    tickCounter = 0;
                } else {
                    tickCounter++;
                }
                player.getPersistentData().putInt(NBT_TICK_KEY, tickCounter);

                // Собираем все активные вредные эффекты, чтобы выдать достижение и очистить
                List<Pair<Holder<net.minecraft.world.effect.MobEffect>, MobEffectCategory>> harmful = entity.getActiveEffects().stream()
                    .map(eff -> new Pair<>(eff.getEffect(), eff.getEffect().value().getCategory()))
                    .filter(pair -> pair.getSecond() == MobEffectCategory.HARMFUL)
                    .distinct()
                    .toList();

                if (harmful.size() >= 5) {
                    Utils.grantAdvancement(player, "concoction:spicy_remove_many_debuffs");
                }

                // Снимаем вредные эффекты (эффект «острое очищает»)
                harmful.forEach(pair -> entity.removeEffect(pair.getFirst()));

                if (player.isOnFire()) {
                    Utils.grantAdvancement(player, "concoction:spicy_on_fire");
                }
            }
        }

        return super.applyEffectTick(entity, amplifier);
    }
}
