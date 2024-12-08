
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.concoction.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

import net.mcreator.concoction.ConcoctionMod;

public class ConcoctionModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, ConcoctionMod.MODID);
	public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_HOTICE = REGISTRY.register("music_disc_hotice", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("concoction", "music_disc_hotice")));
	public static final DeferredHolder<SoundEvent, SoundEvent> CAULDRON_COOKING = REGISTRY.register("cauldron_cooking", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("concoction", "cauldron_cooking")));
	public static final DeferredHolder<SoundEvent, SoundEvent> CAULDRON_BOILING = REGISTRY.register("cauldron_boiling", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("concoction", "cauldron_boiling")));
	public static final DeferredHolder<SoundEvent, SoundEvent> SILENCE = REGISTRY.register("silence", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("concoction", "silence")));
}
