package net.mcreator.concoction.utils;

import net.mcreator.concoction.block.RiceBlock;
import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.init.ConcoctionModParticleTypes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class Utils {
    public static final int BITTERNESS_MIN_FOOD_LEVEL = 6;

    private static final Direction[] ALL_DIRECTIONS = Direction.values();
    private static final ResourceLocation SOUL_ESCAPE_SOUND = ResourceLocation.parse("particle.soul_escape");
    private static final int SUNLIGHT_SEARCH_RADIUS = 2;
    private static final int SUNLIGHT_SEARCH_HEIGHT = 2;
    private static final int MIN_SOFT_SKYLIGHT = 13;

    public static void grantAdvancement(ServerPlayer player, String advancementId) {
        AdvancementHolder adv = player.server.getAdvancements().get(ResourceLocation.parse(advancementId));
        if (adv != null) {
            AdvancementProgress _ap = player.getAdvancements().getOrStartProgress(adv);
            if (!_ap.isDone()) {
                for (String criteria : _ap.getRemainingCriteria())
                    player.getAdvancements().award(adv, criteria);
            }
        }
    }

    @Deprecated(forRemoval = false)
    public static void addAchievement(ServerPlayer player, String achievement) {
        grantAdvancement(player, achievement);
    }

    public static int getColor(ItemStack stack) {
        if (stack.getMaxDamage() <= 0) {
            return 0xFFCB4C;
        }

        int barWidth = Math.round(13.0f - (float) stack.getDamageValue() * 13.0f / stack.getMaxDamage());
        float displayedPercent = barWidth / 13.0f;

        if (displayedPercent < 0.2f) {
            return 0x5E4E87;
        } else if (displayedPercent < 0.36f) {
            return 0x847799;
        } else if (displayedPercent < 0.52f) {
            return 0xB6ACAF;
        } else if (displayedPercent < 0.68f) {
            return 0xD0C4B1;
        } else if (displayedPercent < 0.84f) {
            return 0xDBC89E;
        } else {
            return 0xFFCB4C;
        }
    }

    public static boolean isSunPoweredTime(Level level) {
        return level.isDay();
    }

    public static boolean hasSoftSunExposure(Level level, BlockPos origin) {
        BlockPos headPos = origin.above();
        if (level.canSeeSky(headPos) || level.getBrightness(LightLayer.SKY, headPos) >= MIN_SOFT_SKYLIGHT) {
            return true;
        }

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int radiusSquared = SUNLIGHT_SEARCH_RADIUS * SUNLIGHT_SEARCH_RADIUS;

        for (int dy = 0; dy <= SUNLIGHT_SEARCH_HEIGHT; dy++) {
            for (int dx = -SUNLIGHT_SEARCH_RADIUS; dx <= SUNLIGHT_SEARCH_RADIUS; dx++) {
                for (int dz = -SUNLIGHT_SEARCH_RADIUS; dz <= SUNLIGHT_SEARCH_RADIUS; dz++) {
                    if (dx * dx + dz * dz > radiusSquared) {
                        continue;
                    }

                    cursor.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    if (level.canSeeSky(cursor)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isPlayerSunPowered(Player player) {
        return isSunPoweredTime(player.level()) && hasSoftSunExposure(player.level(), player.blockPosition());
    }

    public static boolean isPhotosynthesisActive(Player player) {
        return player.hasEffect(net.mcreator.concoction.init.ConcoctionModMobEffects.PHOTOSYNTHESIS)
                && isPlayerSunPowered(player);
    }

    public static boolean isBitternessActive(Player player) {
        return player.hasEffect(ConcoctionModMobEffects.BITTERNESS)
                && player.getFoodData().getFoodLevel() > BITTERNESS_MIN_FOOD_LEVEL;
    }

    public static float clampBitternessXpExhaustion(Player player, float exhaustionToAdd) {
        if (exhaustionToAdd <= 0.0F) {
            return 0.0F;
        }

        int availableFoodBuffer = Math.max(player.getFoodData().getFoodLevel() - BITTERNESS_MIN_FOOD_LEVEL, 0);
        int availableSaturationBuffer = Mth.ceil(player.getFoodData().getSaturationLevel());
        float exhaustionCap = 4.0F * (1 + availableFoodBuffer + availableSaturationBuffer);
        float remainingCapacity = Math.max(exhaustionCap - player.getFoodData().getExhaustionLevel(), 0.0F);
        return Math.min(exhaustionToAdd, remainingCapacity);
    }

    public static void spawnBitternessProcParticles(Player player, int intensity) {
        if (!(player.level() instanceof ServerLevel serverLevel) || intensity <= 0) {
            return;
        }

        int particleCount = Math.min(Math.max(intensity, 3), 12);
        serverLevel.sendParticles(
                ParticleTypes.HAPPY_VILLAGER,
                player.getX(),
                player.getY() + player.getBbHeight() * 0.55D,
                player.getZ(),
                particleCount,
                0.35D,
                0.45D,
                0.35D,
                0.02D
        );
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
    
    

    public static void tryAbsorbWater(Level level, BlockPos blockPos, RiceBlock block) {
        if (removeWaterBreadthFirstSearch(level, blockPos, block)) {
            level.setBlock(blockPos, ConcoctionModBlocks.SOAKED_RICE_BLOCK.get().defaultBlockState(), 2);
            level.playSound((Player)null, blockPos, SoundEvents.SPONGE_ABSORB, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public static void playSoulMutationEffect(ServerLevel level, BlockPos pos) {
        level.playSound(
                null,
                pos,
                BuiltInRegistries.SOUND_EVENT.get(SOUL_ESCAPE_SOUND),
                SoundSource.BLOCKS,
                0.85F,
                0.85F + level.random.nextFloat() * 0.3F
        );
        level.sendParticles(
                ConcoctionModParticleTypes.WEEPING_PARTICLE.get(),
                pos.getX() + 0.5D,
                pos.getY() + 0.45D,
                pos.getZ() + 0.5D,
                5,
                0.18D,
                0.2D,
                0.18D,
                0.01D
        );
    }

    public static boolean removeWaterBreadthFirstSearch(Level level, BlockPos blockPos, RiceBlock block) {
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
