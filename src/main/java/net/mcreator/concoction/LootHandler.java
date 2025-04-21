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

import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

import java.util.function.Consumer;

@EventBusSubscriber
public class LootHandler {
	@SubscribeEvent
	public static void LootTableLoad(LootTableLoadEvent event) {
		lootLoad(event.getName(), b -> event.getTable().addPool(b.build()));
	}
	
	public static void lootLoad(ResourceLocation id, Consumer<LootPool.Builder> addPool) {
		String blockPrefix = "minecraft:blocks/";
		String entityPrefix = "minecraft:entities/";
		String name = id.toString();
		if (name.startsWith(entityPrefix)) {
			String file = name.substring(name.indexOf(entityPrefix) + entityPrefix.length());
			switch (file) {
				case "cow":
				case "pig":
				case "sheep":
				case "chicken":
				case "horse":
				case "donkey":
				case "mooshroom":
				case "rabbit":
				case "polar_bear":
				case "llama":
				case "hoglin":
				
					addPool.accept(getInjectPool("animal_fat"));
					break;

				default:
					break;

			}
		}

		if (name.startsWith(blockPrefix)) {
			String file = name.substring(name.indexOf(blockPrefix) + blockPrefix.length());
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
				case "spruce_leaves":
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

	private static LootPoolEntryContainer.Builder<?> getInjectEntry(String name, int weight) {
		return NestedLootTable.lootTableReference(
				ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath("concoction", "inject/" + name))).setWeight(weight);
	}

}
