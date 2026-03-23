
package net.mcreator.concoction.init;

import net.mcreator.concoction.potion.*;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.registries.Registries;

import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, ConcoctionMod.MODID);
	public static final DeferredHolder<MobEffect, MobEffect> MINTY_BREATH = REGISTRY.register("minty_breath", () -> new MintyBreathMobEffect());
	public static final DeferredHolder<MobEffect, MobEffect> FROST_TOUCH = REGISTRY.register("frost_touch", () -> new FrostTouchMobEffect());
	public static final DeferredHolder<MobEffect, MobEffect> FIERY_TOUCH = REGISTRY.register("fiery_touch", () -> new FieryTouchMobEffect());
	public static final DeferredHolder<MobEffect, MobEffect> INSTABILITY = REGISTRY.register("instability", () -> new InstabilityMobEffect());
	public static final DeferredHolder<MobEffect, MobEffect> SPICY = REGISTRY.register("spicy", () -> new SpicyMobEffect());
	public static final DeferredHolder<MobEffect, MobEffect> PHOTOSYNTHESIS = REGISTRY.register("photosynthesis", () -> new PhotosynthesisMobEffect());
	public static final DeferredHolder<MobEffect, MobEffect> SUNSTRUCK_EFFECT = REGISTRY.register("sunstruck_effect", () -> new SunstruckEffectMobEffect());
	public static final DeferredHolder<MobEffect, MobEffect> BITTERNESS = REGISTRY.register("bitterness", () -> new BitternessMobEffect());
    public static final DeferredHolder<MobEffect, MobEffect> WEEPING = REGISTRY.register("weeping", () -> new WeepingMobEffect());
}
