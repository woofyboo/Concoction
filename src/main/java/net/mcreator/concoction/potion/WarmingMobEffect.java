package net.mcreator.concoction.potion;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.concoction.ConcoctionMod;

public class WarmingMobEffect extends MobEffect {
	public WarmingMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -873597);

		this.addAttributeModifier(
			Attributes.STEP_HEIGHT,
			ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "effect.warming_0"),
			AttributeModifier.Operation.ADD_VALUE,
			(amplifier) -> 1.0D // фиксированное значение, не зависит от уровня
		);
	}
}
