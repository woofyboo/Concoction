package net.mcreator.concoction.init;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.entity.CinnamonBoatEntity;
import net.mcreator.concoction.entity.CinnamonChestBoatEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ConcoctionBoatEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, ConcoctionMod.MODID);

	public static final DeferredHolder<EntityType<?>, EntityType<CinnamonBoatEntity>> CINNAMON_BOAT = REGISTRY.register("cinnamon_boat",
			() -> EntityType.Builder.<CinnamonBoatEntity>of(CinnamonBoatEntity::new, MobCategory.MISC).sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10)
					.build("cinnamon_boat"));

	public static final DeferredHolder<EntityType<?>, EntityType<CinnamonChestBoatEntity>> CINNAMON_CHEST_BOAT = REGISTRY.register("cinnamon_chest_boat",
			() -> EntityType.Builder.<CinnamonChestBoatEntity>of(CinnamonChestBoatEntity::new, MobCategory.MISC).sized(1.375F, 0.5625F).eyeHeight(0.5625F)
					.clientTrackingRange(10).build("cinnamon_chest_boat"));

	private ConcoctionBoatEntities() {
	}
}
