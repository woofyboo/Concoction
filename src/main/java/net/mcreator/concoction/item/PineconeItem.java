
package net.mcreator.concoction.item;

import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;

@EventBusSubscriber
public class PineconeItem extends Item {
	public PineconeItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@SubscribeEvent
	public static void itemEntityDespawn(ItemExpireEvent event) {
		if (!event.getEntity().level().isClientSide() &&
				event.getEntity().getItem().getItem() == ConcoctionModItems.PINECONE.get()) {
			if (event.getEntity().level().getBlockState(event.getEntity().blockPosition()).getBlock() == Blocks.AIR &&
					event.getEntity().level().getBlockState(event.getEntity().blockPosition().below()).is(BlockTags.create(ResourceLocation.parse("minecraft:dirt")))) {
				event.getEntity().level().setBlock(event.getEntity().blockPosition(), Blocks.SPRUCE_SAPLING.defaultBlockState(), 3);

				((ServerLevel) event.getEntity().level()).getPlayers(player ->
						player.distanceToSqr(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ()) <= 256
				).forEach(player -> {
					Utils.addAchievement(player, "concoction:sapling_from_item");
				});
			}
//			if (itemSt.getCount() < 4) {}
		}
	}
}
