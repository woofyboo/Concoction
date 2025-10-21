
package net.mcreator.concoction.potion;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.concoction.ConcoctionMod;

public class CreamyMobEffect extends MobEffect {
	public CreamyMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -1291);
		this.addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "effect.creamy_0"), 0.08, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
	}
}
