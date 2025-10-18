
package net.mcreator.concoction.potion;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.concoction.ConcoctionMod;

public class BreakfastMobEffect extends MobEffect {
	public BreakfastMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -865098);
		this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "effect.breakfast_0"), 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
	}
}
