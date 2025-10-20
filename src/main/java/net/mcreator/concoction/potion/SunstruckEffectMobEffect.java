package net.mcreator.concoction.potion;

import net.mcreator.concoction.ConcoctionMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class SunstruckEffectMobEffect extends MobEffect {

    // Идентификаторы модификаторов (фиксированные ResourceLocation)
    private static final ResourceLocation MAX_HEALTH_MOD_ID = ResourceLocation.fromNamespaceAndPath(
            ConcoctionMod.MODID, "sunstruck_max_health_reduction");
    private static final ResourceLocation ATK_SPEED_MOD_ID = ResourceLocation.fromNamespaceAndPath(
            ConcoctionMod.MODID, "sunstruck_atk_speed");

    public SunstruckEffectMobEffect() {
        super(MobEffectCategory.HARMFUL, 0xFFE07A);

        // MAX_HEALTH: −4.0 за уровень, но капим суммарно до −16.0
        // Используем перегрузку с функцией: amount = f(amplifier)
        this.addAttributeModifier(
                Attributes.MAX_HEALTH,
                MAX_HEALTH_MOD_ID,
                AttributeModifier.Operation.ADD_VALUE,
                amp -> Math.max(-16.0D, -4.0D * (amp + 1))
        );

        // ATTACK_SPEED: +0.2 за уровень
        this.addAttributeModifier(
                Attributes.ATTACK_SPEED,
                ATK_SPEED_MOD_ID,
                AttributeModifier.Operation.ADD_VALUE,
                amp -> 0.2D * (amp + 1)
        );

        // Если захочешь ещё и скорость бега — добавь так:
        // this.addAttributeModifier(
        //         Attributes.MOVEMENT_SPEED,
        //         ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "sunstruck_move_speed"),
        //         AttributeModifier.Operation.MULTIPLY_TOTAL,
        //         amp -> -0.05D * (amp + 1)
        // );
    }

    // Никаких tick-методов и ручного снятия модификаторов не требуется —
    // игра сама применит/снимет их на весь срок эффекта.
}
