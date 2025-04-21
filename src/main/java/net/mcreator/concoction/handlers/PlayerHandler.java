package net.mcreator.concoction.handlers;

import net.mcreator.concoction.utils.Utils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

@EventBusSubscriber
public class PlayerHandler {
    private static final TagKey<Item> SOULLAND_RELATION = TagKey.create(Registries.ITEM, ResourceLocation.parse("concoction:soulland_relation"));
    private static final TagKey<Block> WILDLIFE_PLANTS = TagKey.create(Registries.BLOCK, ResourceLocation.parse("concoction:wildlife_plants"));
    private static final TagKey<Item> SPECIAL_FOOD = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:foods/dish"));
    private static final TagKey<Item> SPECIAL_SOUP = TagKey.create(Registries.ITEM, ResourceLocation.parse("c:foods/soup"));

    @SubscribeEvent
    public static void playerInventoryChangeEvent(PlayerContainerEvent.Close event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            boolean hasTaggedItem = false;
            for (ItemStack stack : player.getInventory().items) {
                if (!stack.isEmpty() && stack.is(SOULLAND_RELATION)) {
                    hasTaggedItem = true;
                    break;
                }
            }
            
            if (hasTaggedItem) {
                Utils.addAchievement(player, "concoction:nether_mutation");
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerEatItem(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack itemStack = event.getItem();
            if (itemStack.is(SPECIAL_FOOD) || itemStack.is(SPECIAL_SOUP)) {
                Utils.addAchievement(player, "concoction:eat_dish");
            }
        }
    }

    @SubscribeEvent
    public static void playerBreaksBlock(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            if (event.getState().is(WILDLIFE_PLANTS)) {
                Utils.addAchievement(player, "concoction:new_crops");
            }
        }
    }
}
