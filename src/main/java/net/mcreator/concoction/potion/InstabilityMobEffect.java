
package net.mcreator.concoction.potion;

import net.neoforged.neoforge.common.EffectCure;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import java.util.Set;

public class InstabilityMobEffect extends MobEffect {
	public InstabilityMobEffect() {
		super(MobEffectCategory.HARMFUL, -6749953);
	}

	@Override
	public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
	}
}
