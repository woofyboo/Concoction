package net.mcreator.concoction.utils;

import net.mcreator.concoction.block.RiceBlockBlock;
import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class Utils {

    private static final Direction[] ALL_DIRECTIONS = Direction.values();

    public static void addAchievement(ServerPlayer player, String achievement) {
        AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.parse(achievement));
        if (adv != null) {
            AdvancementProgress _ap = player.getAdvancements().getOrStartProgress(adv);
            if (!_ap.isDone()) {
                for (String criteria : _ap.getRemainingCriteria())
                    player.getAdvancements().award(adv, criteria);
            }
        }
    }

    public static int getColor(ItemStack stack) {
        float durabilityPercent = 1.0f - (float)stack.getDamageValue() / stack.getMaxDamage();
        if (durabilityPercent < 0.2f) {
            return 0x5E4E87;
        } else if (durabilityPercent < 0.36f) {
            return 0x847799;
        } else if (durabilityPercent < 0.52f) {
            return 0xB6ACAF;
        } else if (durabilityPercent < 0.68f) {
            return 0xD0C4B1;
        } else if (durabilityPercent < 0.84f) {
            return 0xDBC89E;
        } else {
            return 0xFFCB4C;
        }
    }

    public static boolean touchesLiquid(BlockGetter level, BlockPos blockPos, BlockState state) {
        boolean flag = false;
        BlockPos.MutableBlockPos blockpos$mutableblockpos = blockPos.mutable();

        for(Direction direction : Direction.values()) {
            BlockState blockstate = level.getBlockState(blockpos$mutableblockpos);
            if (direction != Direction.DOWN || state.canBeHydrated(level, blockPos, blockstate.getFluidState(), blockpos$mutableblockpos)) {
                blockpos$mutableblockpos.setWithOffset(blockPos, direction);
                blockstate = level.getBlockState(blockpos$mutableblockpos);
                if (state.canBeHydrated(level, blockPos, blockstate.getFluidState(), blockpos$mutableblockpos) && !blockstate.isFaceSturdy(level, blockPos, direction.getOpposite())) {
                    flag = true;
                    break;
                }
            }
        }

        return flag;
    }

    public static boolean shouldSolidify(BlockGetter level, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
        return blockState.canBeHydrated(level, blockPos, fluidState, blockPos) || touchesLiquid(level, blockPos, blockState);
    }
    
    

    public static void tryAbsorbWater(Level level, BlockPos blockPos, RiceBlockBlock block) {
        if (removeWaterBreadthFirstSearch(level, blockPos, block)) {
            level.setBlock(blockPos, ConcoctionModBlocks.SOAKED_RICE_BLOCK.get().defaultBlockState(), 2);
            level.playSound((Player)null, blockPos, SoundEvents.SPONGE_ABSORB, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public static boolean removeWaterBreadthFirstSearch(Level level, BlockPos blockPos, RiceBlockBlock block) {
        BlockState spongeState = level.getBlockState(blockPos);
        return BlockPos.breadthFirstTraversal(blockPos, 5, 20, (p_277519_, p_277492_) -> {
            for(Direction direction : ALL_DIRECTIONS) {
                p_277492_.accept(p_277519_.relative(direction));
            }

        }, (p_294069_) -> {
            if (p_294069_.equals(blockPos)) {
                return true;
            } else {
                BlockState blockstate = level.getBlockState(p_294069_);
                FluidState fluidstate = level.getFluidState(p_294069_);
                if (!spongeState.canBeHydrated(level, blockPos, fluidstate, p_294069_)) {
                    return false;
                } else {
                    Block patt0$temp = blockstate.getBlock();
                    if (patt0$temp instanceof BucketPickup) {
                        BucketPickup bucketpickup = (BucketPickup)patt0$temp;
                        if (!bucketpickup.pickupBlock((Player)null, level, p_294069_, blockstate).isEmpty()) {
                            return true;
                        }
                    }

                    if (blockstate.getBlock() instanceof LiquidBlock) {
                        level.setBlock(p_294069_, Blocks.AIR.defaultBlockState(), 5);
                    } else {
                        if (!blockstate.is(Blocks.KELP) && !blockstate.is(Blocks.KELP_PLANT) && !blockstate.is(Blocks.SEAGRASS) && !blockstate.is(Blocks.TALL_SEAGRASS)) {
                            return false;
                        }

                        BlockEntity blockentity = blockstate.hasBlockEntity() ? level.getBlockEntity(p_294069_) : null;
                        block.dropResources(blockstate, level, p_294069_, blockentity);
                        level.setBlock(p_294069_, Blocks.AIR.defaultBlockState(), 3);
                    }

                    return true;
                }
            }
        }) > 1;
    }
}
