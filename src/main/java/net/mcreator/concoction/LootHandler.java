/*
 * The code of this mod element is always locked.
 *
 * You can register new events in this class too.
 *
 * If you want to make a plain independent class, create it using
 * Project Browser -> New... and make sure to make the class
 * outside net.mcreator.concoction as this package is managed by MCreator.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
 *
 * This class will be added in the mod root package.
*/
package net.mcreator.concoction;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;

import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import java.util.function.Consumer;

@EventBusSubscriber
public class LootHandler {
	@SubscribeEvent
	public static void LootTableLoad(LootTableLoadEvent event) {
		lootLoad(event.getName(), b -> event.getTable().addPool(b.build()));
	}
	
	public static void lootLoad(ResourceLocation id, Consumer<LootPool.Builder> addPool) {
		String prefix = "minecraft:blocks/";
		String name = id.toString();

		if (name.startsWith(prefix)) {
			String file = name.substring(name.indexOf(prefix) + prefix.length());
			switch (file) {
				case "abandoned_mineshaft":
				case "desert_pyramid":
				case "jungle_temple":
				case "simple_dungeon":
				case "spawn_bonus_chest":
				case "stronghold_corridor":
					addPool.accept(getInjectPool(file));
					break;
				case "village/village_temple":
				case "village/village_toolsmith":
				case "village/village_weaponsmith":
					addPool.accept(getInjectPool("village_chest"));
					break;
				case "cherry_leaves":
					addPool.accept(getInjectPool(file));
					break;
				default:
					break;
			}
		}
	}
	private static LootPool.Builder getInjectPool(String entryName) {
		return LootPool.lootPool().add(getInjectEntry(entryName, 5));
	}
	private static ResourceLocation prefix(String path) {
		return new ResourceLocation("concoction", path);
	}

	private static LootPoolEntryContainer.Builder<?> getInjectEntry(String name, int weight) {
		ResourceLocation table = prefix("inject/" + name);
		return NestedLootTable.lootTableReference(
				(ResourceKey.create(ResourceKey.createRegistryKey(
						new ResourceLocation("minecraft:/loot_tables")), table))).setWeight(weight);
	}
}
