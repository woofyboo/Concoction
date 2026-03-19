package net.mcreator.concoction.event;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ConcoctionMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public final class MintyBreathEvents {
    private static final TagKey<DamageType> FIRE_DAMAGE = TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.withDefaultNamespace("is_fire"));

    private MintyBreathEvents() {
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance effect = entity.getEffect(ConcoctionModMobEffects.MINTY_BREATH);
        if (effect == null) {
            return;
        }

        float multiplier = 1.5F + 0.25F * effect.getAmplifier();
        event.setAmount(event.getAmount() * multiplier);
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance effect = entity.getEffect(ConcoctionModMobEffects.MINTY_BREATH);
        if (effect == null || !event.getSource().is(FIRE_DAMAGE)) {
            return;
        }

        float multiplier = Math.max(0.0F, 1.0F - effect.getAmplifier() / 10.0F);
        event.setAmount(event.getAmount() * multiplier);
    }
}
