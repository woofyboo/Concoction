
package net.mcreator.concoction.potion;

import com.mojang.datafixers.util.Pair;
import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.Map;
import java.util.function.BiConsumer;

public class SunstruckEffectMobEffect extends MobEffect {
	public SunstruckEffectMobEffect() {
		super(MobEffectCategory.NEUTRAL, -1458376);
	}

	@Override
	public void removeAttributeModifiers(AttributeMap attrMap) {
		AttributeInstance maxHealth = attrMap.getInstance(Attributes.MAX_HEALTH);
		AttributeInstance speed_instance = attrMap.getInstance(Attributes.MOVEMENT_SPEED);
		AttributeInstance atk_speed_instance = attrMap.getInstance(Attributes.ATTACK_SPEED);
		if (maxHealth != null) maxHealth.removeModifier(ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "sunstruck_max_health_reduction"));
		if (speed_instance != null) speed_instance.removeModifier(ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "sunstruck_speed"));
		if (atk_speed_instance != null) atk_speed_instance.removeModifier(ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "sunstruck_atk_speed"));
		super.removeAttributeModifiers(attrMap);
	}
//
//	@Override
//	public void addAttributeModifiers(AttributeMap p_19479_, int p_19480_) {
//		for(Map.Entry<Holder<Attribute>, AttributeTemplate> entry : this.attributeModifiers.entrySet()) {
//			AttributeInstance attributeinstance = p_19479_.getInstance((Holder)entry.getKey());
//			if (attributeinstance != null) {
//				attributeinstance.removeModifier(((AttributeTemplate)entry.getValue()).id());
//				attributeinstance.addPermanentModifier(((AttributeTemplate)entry.getValue()).create(p_19480_));
//			}
//		}
//	}
	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		if (entity instanceof LivingEntity livEnt && livEnt.hasEffect(ConcoctionModMobEffects.SUNSTRUCK_EFFECT)) {
			if (entity.level() instanceof Level pLevel && !pLevel.isClientSide()) {
				AttributeMap attributes = livEnt.getAttributes();
				ResourceLocation mhr = ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "sunstruck_max_health_reduction");
				AttributeModifier mhr_modifier = new AttributeModifier(
						// The name we defined earlier.
						mhr,
						// The amount by which we modify the attribute value.
						Math.max(-12.0, (amplifier+1) * -4.0),
						AttributeModifier.Operation.ADD_VALUE
				);
				AttributeInstance instance = attributes.getInstance(Attributes.MAX_HEALTH);
				if (instance != null) {
					if (!instance.hasModifier(mhr)) instance.addTransientModifier(mhr_modifier);
				}
				ResourceLocation speed = ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "sunstruck_speed");
				AttributeModifier speed_modifier = new AttributeModifier(
						speed,
						(amplifier+1) * 0.1,
						AttributeModifier.Operation.ADD_VALUE
				);
				ResourceLocation atk_speed = ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "sunstruck_atk_speed");
				AttributeModifier atk_speed_modifier = new AttributeModifier(
						atk_speed,
						(amplifier+1) * 0.1,
						AttributeModifier.Operation.ADD_VALUE
				);
				AttributeInstance speed_instance = attributes.getInstance(Attributes.MOVEMENT_SPEED);
				AttributeInstance atk_speed_instance = attributes.getInstance(Attributes.ATTACK_SPEED);
				if (speed_instance == null || atk_speed_instance == null) {
					ConcoctionMod.LOGGER.warn("SunstruckEffectMobEffect: Unable to find speed or attack speed attribute instance for entity: " + entity);
					return super.applyEffectTick(entity, amplifier);
				}
				int dayTime = Math.floorMod(pLevel.dayTime(),24000);
				if (pLevel.dimension() == Level.OVERWORLD && ((dayTime >= 0 && dayTime < 13000) || (dayTime >= 23000 && dayTime < 24000))) {
					if (!speed_instance.hasModifier(speed)) speed_instance.addTransientModifier(speed_modifier);
					if (!atk_speed_instance.hasModifier(atk_speed)) atk_speed_instance.addTransientModifier(atk_speed_modifier);
				} else {
					speed_instance.removeModifier(speed_modifier);
					atk_speed_instance.removeModifier(atk_speed_modifier);
				}
				// Alternatively, LivingEntity also offers shortcuts:
//				AttributeInstance instance = livingEntity.getAttribute(Attributes.ARMOR);
//				double value = livingEntity.getAttributeValue(Attributes.ARMOR);
			}
		}
		//SpicyOnEffectActiveTickProcedure.execute(entity.level(), entity);
		return super.applyEffectTick(entity, amplifier);
	}
}
