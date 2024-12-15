
package net.mcreator.concoction.item;

import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;
import org.jetbrains.annotations.Nullable;


import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT;

@EventBusSubscriber
public class CherryItem extends Item {
	public CherryItem() {
		super(new Item.Properties().stacksTo(64).component(FOOD_EFFECT.value(), new FoodEffectComponent("sweet", 1, 6)).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(2).saturationModifier(0.3f).build()));
	}
	@SubscribeEvent
	public static void itemEntityDespawn(ItemExpireEvent event) {
		if (!event.getEntity().level().isClientSide() &&
				event.getEntity().getItem().getItem() == ConcoctionModItems.CHERRY.get()) {
			if (event.getEntity().level().getBlockState(event.getEntity().blockPosition()).getBlock() == Blocks.AIR &&
					event.getEntity().level().getBlockState(event.getEntity().blockPosition().below()).is(BlockTags.create(ResourceLocation.parse("minecraft:dirt")))) {
				event.getEntity().level().setBlock(event.getEntity().blockPosition(), Blocks.CHERRY_SAPLING.defaultBlockState(), 3);
			}
//			if (itemSt.getCount() < 4) {}
		}
	}

	@Override
	public @Nullable FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
		return super.getFoodProperties(stack, entity);
	}
}
