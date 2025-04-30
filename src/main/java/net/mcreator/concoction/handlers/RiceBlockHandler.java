package net.mcreator.concoction.handlers;

import net.mcreator.concoction.block.RiceBlockBlock;
import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.mcreator.concoction.init.ConcoctionModEntities;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber
public class RiceBlockHandler {

        @SubscribeEvent
        public static void entityTickEvent(EntityTickEvent.Pre event) {

            if (event.getEntity() instanceof FallingBlockEntity fallingBlock){
                Block block = fallingBlock.getBlockState().getBlock();
                if (block == ConcoctionModBlocks.RICE_BLOCK.get()) {
                    if (Utils.touchesLiquid(event.getEntity().level(), event.getEntity().blockPosition(), block.defaultBlockState())) {
                        Utils.tryAbsorbWater(event.getEntity().level(), event.getEntity().blockPosition(), (RiceBlockBlock) block);
                        fallingBlock.remove(Entity.RemovalReason.DISCARDED);
                        event.getEntity().level().setBlock(event.getEntity().blockPosition(), ConcoctionModBlocks.SOAKED_RICE_BLOCK.get().defaultBlockState(), 3);
                    }
                }
            }
        }
}
