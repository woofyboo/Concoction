package net.mcreator.concoction.block;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.mcreator.concoction.item.OvergrownHoeItem;
import net.mcreator.concoction.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.FarmlandWaterManager;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.ICancellableEvent;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

import static net.mcreator.concoction.init.ConcoctionModBlocks.WEIGHTED_SOULS;

@EventBusSubscriber(modid = ConcoctionMod.MODID)
public class SoullandBlock extends Block {
    public static final BooleanProperty SOULCHARGED = BooleanProperty.create("soulcharged");
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 15, 16);

    public SoullandBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BROWN)
                .sound(SoundType.SOUL_SOIL)
                .strength(0.5F)
                .randomTicks()
                .isRedstoneConductor((bs, br, bp) -> false)
                .lightLevel(state -> state.getValue(SOULCHARGED) ? 3 : 0)
                .emissiveRendering((state, world, pos) -> state.getValue(SOULCHARGED)));
        this.registerDefaultState(this.stateDefinition.any().setValue(SOULCHARGED, false));
    }

    @SubscribeEvent
    public static void onBlockClick(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || event.getLevel().isClientSide()) {
            return;
        }

        if (tryCreateSoulland(player, event.getLevel(), event.getPos(), event.getHand())) {
            ((ICancellableEvent) event).setCanceled(true);
        }
    }

    public static boolean tryCreateSoulland(ServerPlayer player, Level level, BlockPos pos, InteractionHand hand) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(Blocks.SOUL_SOIL)) {
            return false;
        }

        ItemStack tool = player.getItemInHand(hand);
        if (!(tool.getItem() instanceof HoeItem)) {
            return false;
        }

        if (tool.getItem() instanceof OvergrownHoeItem overgrownHoe
                && overgrownHoe.getDamage(tool) >= overgrownHoe.getMaxDamage(tool) - 1) {
            return false;
        }

        turnToSoulland(player, state, level, pos);
        player.swing(hand, true);

        if (!player.isCreative()) {
            tool.hurtAndBreak(1, (ServerLevel) level, null, brokenItem -> {});
        }

        level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.PLAYERS, 1.0F, 0.9F + level.random.nextFloat() * 0.2F);
        Utils.grantAdvancement(player, "concoction:make_soul_soul");
        return true;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP && !state.canSurvive(level, pos)) {
            level.scheduleTick(pos, this, 1);
        }

        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState aboveState = level.getBlockState(pos.above());
        return !aboveState.isSolid() || aboveState.getBlock() instanceof FenceGateBlock || aboveState.getBlock() instanceof MovingPistonBlock;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            turnToSoil(null, state, level, pos);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);

        boolean charged = state.getValue(SOULCHARGED);
        boolean nearSoul = isNearSoul(level, pos);

        if (!nearSoul) {
            if (charged) {
                level.setBlock(pos, state.setValue(SOULCHARGED, false), 2);
            } else if (!shouldMaintainFarmland(level, pos)) {
                turnToSoil(null, state, level, pos);
            }
            return;
        }

        if (!charged) {
            level.setBlock(pos, state.setValue(SOULCHARGED, true), 2);
        }

        if (!charged) {
            return;
        }

        for (BlockPos offset : BlockPos.betweenClosed(pos.offset(-4, 1, -4), pos.offset(4, 2, 4))) {
            BlockState cropState = level.getBlockState(offset);
            BlockState belowState = level.getBlockState(offset.below());
            if (!(cropState.getBlock() instanceof CropBlock) || !(belowState.getBlock() instanceof SoullandBlock) || random.nextFloat() >= 0.16F) {
                continue;
            }

            try {
                Method randomTickMethod = CropBlock.class.getDeclaredMethod(
                        "randomTick",
                        BlockState.class,
                        ServerLevel.class,
                        BlockPos.class,
                        RandomSource.class
                );
                randomTickMethod.setAccessible(true);
                randomTickMethod.invoke(cropState.getBlock(), cropState, level, offset, random);
            } catch (ReflectiveOperationException exception) {
                ConcoctionMod.LOGGER.error("Failed to trigger crop random tick for {}", offset, exception);
            }
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!level.isClientSide && CommonHooks.onFarmlandTrample(level, pos, Blocks.SOUL_SOIL.defaultBlockState(), fallDistance, entity)) {
            turnToSoil(entity, state, level, pos);
        }

        super.fallOn(level, state, pos, entity, fallDistance);
    }

    public static void turnToSoil(@Nullable Entity entity, BlockState state, Level level, BlockPos pos) {
        BlockState newState = pushEntitiesUp(state, Blocks.SOUL_SOIL.defaultBlockState(), level, pos);
        level.setBlockAndUpdate(pos, newState);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, newState));
    }

    public static void turnToSoulland(@Nullable Entity entity, BlockState state, Level level, BlockPos pos) {
        BlockState newState = pushEntitiesUp(state, ConcoctionModBlocks.SOULLAND.get().defaultBlockState(), level, pos);
        level.setBlockAndUpdate(pos, newState);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, newState));
    }

    private static boolean shouldMaintainFarmland(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos.above()).is(BlockTags.MAINTAINS_FARMLAND);
    }

    private static boolean isNearSoul(LevelReader level, BlockPos pos) {
        for (BlockPos blockPos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            if (level.getBlockState(blockPos).getBlock().equals(WEIGHTED_SOULS.get())) {
                return true;
            }
        }

        return FarmlandWaterManager.hasBlockWaterTicket(level, pos);
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SOULCHARGED);
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(SOULCHARGED, false);
    }

    @Override
    public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(SOULCHARGED);
    }
}
