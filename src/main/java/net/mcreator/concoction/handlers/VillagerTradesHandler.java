package net.mcreator.concoction.handlers;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.List;

@EventBusSubscriber
public class VillagerTradesHandler {

    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        if(event.getType() == VillagerProfession.FARMER) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            // Уровень 1
            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ConcoctionModItems.CORN.get(), 15),
                    new ItemStack(Items.EMERALD, 1), 16, 2, 0.05f));

            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModItems.CORN_SEEDS.get(), 3), 16, 2, 0.05f));

            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ConcoctionModItems.ONION.get(), 22),
                    new ItemStack(Items.EMERALD, 1), 16, 2, 0.05f));

            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 4),
                    new ItemStack(ConcoctionModItems.SPICY_PEPPER_SEEDS.get(), 1), 16, 2, 0.05f));

            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ConcoctionModItems.SPICY_PEPPER.get(), 4),
                    new ItemStack(Items.EMERALD, 1), 16, 2, 0.05f));

            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModItems.SUNFLOWER_SEEDS.get(), 16), 16, 2, 0.05f));

            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModItems.CABBAGE_SEEDS.get(), 3), 16, 2, 0.05f));

            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ConcoctionModItems.CABBAGE_LEAF.get(), 22),
                    new ItemStack(Items.EMERALD, 1), 16, 2, 0.05f));

            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModItems.RICE.get(), 8), 16, 2, 0.05f));

            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ConcoctionModItems.MINT.get(), 15),
                    new ItemStack(Items.EMERALD, 1), 16, 2, 0.05f));

            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ConcoctionModItems.PINECONE.get(), 32),
                    new ItemStack(Items.EMERALD, 1), 16, 2, 0.05f));

            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ConcoctionModItems.CHERRY.get(), 8),
                    new ItemStack(Items.EMERALD, 1), 16, 2, 0.05f));

            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModItems.TOMATO_SEEDS.get(), 4), 16, 2, 0.05f));

            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ConcoctionModItems.TOMATO.get(), 20),
                    new ItemStack(Items.EMERALD, 1), 16, 2, 0.05f));

            // Уровень 2
            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ConcoctionModItems.SPICY_PEPPER.get(), 4),
                    new ItemStack(Items.EMERALD, 1), 16, 5, 0.05f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModItems.CABBAGE_LEAF.get(), 3), 16, 5, 0.05f));

            // Уровень 3
            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 4),
                    new ItemStack(ConcoctionModItems.COTTON_OIL.get(), 1), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 4),
                    new ItemStack(ConcoctionModItems.CORN_OIL.get(), 1), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 2),
                    new ItemStack(ConcoctionModItems.SUNFLOWER_OIL.get(), 1), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(ConcoctionModItems.HOT_SAUCE_BOTTLE.get(), 1), 16, 10, 0.05f));

            // Уровень 4
            trades.get(4).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 2),
                    new ItemStack(ConcoctionModItems.MINT_CHOCOLATE_CAKE.get(), 1), 16, 15, 0.05f));
                    
            trades.get(4).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 2),
                    new ItemStack(ConcoctionModItems.CHERRY_CAKE.get(), 1), 16, 15, 0.05f));

                    trades.get(4).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 2),
                    new ItemStack(ConcoctionModItems.CARROT_CAKE.get(), 1), 16, 15, 0.05f));


            // Уровень 5
            trades.get(5).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ConcoctionModBlocks.CABBAGE_BLOCK.get(), 1),
                    new ItemStack(Items.EMERALD, 1), 16, 30, 0.05f));
        }

        if(event.getType() == VillagerProfession.BUTCHER) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            // Уровень 1
            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModItems.ANIMAL_FAT.get(), 2), 16, 2, 0.05f));

            // Уровень 2
            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 8),
                    new ItemStack(ConcoctionModItems.HOT_SAUCE_BOTTLE.get(), 1), 16, 5, 0.05f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ConcoctionModItems.SEA_SALT.get(), 32),
                    new ItemStack(Items.EMERALD, 1), 16, 5, 0.05f));

            // Уровень 3
            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ConcoctionModItems.BUTTER.get(), 4),
                    new ItemStack(Items.EMERALD, 1), 16, 10, 0.05f));

            // Уровень 5
            trades.get(5).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModItems.MEAT_GOULASH.get(), 1), 16, 30, 0.05f));
        }

        if(event.getType() == VillagerProfession.CLERIC) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            // Уровень 1
            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ConcoctionModItems.SEA_SALT.get(), 32),
                    new ItemStack(Items.EMERALD, 1), 16, 2, 0.05f));

            // Уровень 3
            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 4),
                    new ItemStack(ConcoctionModItems.OBSIDIAN_TEARS_BOTTLE.get(), 1), 16, 10, 0.05f));

            // Уровень 5
            trades.get(5).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 4),
                    new ItemStack(ConcoctionModItems.WEIGHTED_SOULS_BUCKET.get(), 1), 16, 30, 0.05f));

            trades.get(5).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 2),
                    new ItemStack(ConcoctionModItems.SOUL_ICE.get(), 1), 16, 30, 0.05f));
        }

        if(event.getType() == VillagerProfession.SHEPHERD) {
            Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

            // Уровень 1
            trades.get(1).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(ConcoctionModItems.FABRIC.get(), 8),
                    new ItemStack(Items.EMERALD, 1), 16, 2, 0.05f));

            // Уровень 2
            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.BLACK_WOVEN_CARPET.get(), 4), 16, 5, 0.05f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.BLUE_WOVEN_CARPET.get(), 4), 16, 5, 0.05f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.BROWN_WOVEN_CARPET.get(), 4), 16, 5, 0.05f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.CYAN_WOVEN_CARPET.get(), 4), 16, 5, 0.05f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.GRAY_WOVEN_CARPET.get(), 4), 16, 5, 0.05f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.GREEN_WOVEN_CARPET.get(), 4), 16, 5, 0.05f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.LIGHT_BLUE_WOVEN_CARPET.get(), 4), 16, 5, 0.05f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.LIGHT_GRAY_WOVEN_CARPET.get(), 4), 16, 5, 0.05f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.LIME_WOVEN_CARPET.get(), 4), 16, 5, 0.05f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.MAGENTA_WOVEN_CARPET.get(), 4), 16, 5, 0.05f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.ORANGE_WOVEN_CARPET.get(), 4), 16, 5, 0.05f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.PINK_WOVEN_CARPET.get(), 4), 16, 5, 0.05f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.PURPLE_WOVEN_CARPET.get(), 4), 16, 5, 0.05f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.RED_WOVEN_CARPET.get(), 4), 16, 5, 0.05f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.WHITE_WOVEN_CARPET.get(), 4), 16, 5, 0.05f));

            trades.get(2).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.YELLOW_WOVEN_CARPET.get(), 4), 16, 5, 0.05f));

            // Уровень 3
            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.BLACK_PILLOW_BLOCK.get(), 4), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.BLUE_PILLOW_BLOCK.get(), 4), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.BROWN_PILLOW_BLOCK.get(), 4), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.CYAN_PILLOW_BLOCK.get(), 4), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.GRAY_PILLOW_BLOCK.get(), 4), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.GREEN_PILLOW_BLOCK.get(), 4), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.LIGHT_BLUE_PILLOW_BLOCK.get(), 4), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.LIGHT_GRAY_PILLOW_BLOCK.get(), 4), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.LIME_PILLOW_BLOCK.get(), 4), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.MAGENTA_PILLOW_BLOCK.get(), 4), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.ORANGE_PILLOW_BLOCK.get(), 4), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.PINK_PILLOW_BLOCK.get(), 4), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.PURPLE_PILLOW_BLOCK.get(), 4), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.RED_PILLOW_BLOCK.get(), 4), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.PILLOW_BLOCK.get(), 4), 16, 10, 0.05f));

            trades.get(3).add((entity, randomSource) -> new MerchantOffer(
                    new ItemCost(Items.EMERALD, 1),
                    new ItemStack(ConcoctionModBlocks.YELLOW_PILLOW_BLOCK.get(), 4), 16, 10, 0.05f));
        }
    }
} 