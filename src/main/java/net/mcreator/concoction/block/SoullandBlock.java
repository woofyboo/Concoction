package net.mcreator.concoction.block;

import java.lang.reflect.Method;
import net.mcreator.concoction.ConcoctionMod;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.FarmlandWaterManager;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import javax.annotation.Nullable;

import static net.mcreator.concoction.init.ConcoctionModBlocks.WEIGHTED_SOULS;

@EventBusSubscriber(modid = ConcoctionMod.MODID)
public class SoullandBlock extends Block {
    public static final BooleanProperty SOULCHARGED = BooleanProperty.create("soulcharged");
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 15, 16);

    public SoullandBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BROWN)
                .sound(SoundType.SOUL_SOIL)
                .strength(0.5f)
                .randomTicks()
                .isRedstoneConductor((bs, br, bp) -> false)
                .lightLevel(state -> state.getValue(SOULCHARGED) ? 3 : 0)
                .emissiveRendering((state, world, pos) -> state.getValue(SOULCHARGED))
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(SOULCHARGED, false));
    }

    @SubscribeEvent
    public static void onBlockClick(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().isClientSide) {
            if (event.getEntity() instanceof ServerPlayer player && player.getMainHandItem().is(ItemTags.HOES)) {
                BlockPos pos = event.getPos();
                Level world = event.getLevel();
                if (world.getBlockState(pos).is(Blocks.SOUL_SOIL)) {
                    turnToSoil(player, world.getBlockState(pos), world, pos);
                }
            }
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (dir == Direction.UP && !state.canSurvive(level, pos)) {
            level.scheduleTick(pos, this, 1);
        }

        return super.updateShape(state, dir, neighbor, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState blockstate = level.getBlockState(pos.above());
        return !blockstate.isSolid() || blockstate.getBlock() instanceof FenceGateBlock || blockstate.getBlock() instanceof MovingPistonBlock;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            turnToSoil(null, state, level, pos);
        }
    }

    @Override
    public void randomTick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
        super.randomTick(blockstate, world, pos, random);

        boolean charged = blockstate.getValue(SOULCHARGED);
        boolean nearSoul = isNearSoul(world, pos);

        if (!nearSoul) {
            if (charged) {
                world.setBlock(pos, blockstate.setValue(SOULCHARGED, false), 2);
            } else if (!shouldMaintainFarmland(world, pos)) {
                turnToSoil(null, blockstate, world, pos);
            }
        } else {
            if (!charged) {
                world.setBlock(pos, blockstate.setValue(SOULCHARGED, true), 2);
            }

            if (charged) {
                for (BlockPos offset : BlockPos.betweenClosed(pos.offset(-4, 1, -4), pos.offset(4, 2, 4))) {
                    BlockState cropState = world.getBlockState(offset);
                    BlockState belowState = world.getBlockState(offset.below());

                    if (cropState.getBlock() instanceof CropBlock && belowState.getBlock() instanceof SoullandBlock) {
                        if (random.nextFloat() < 0.16f) {
                            try {
                                Method randomTickMethod = CropBlock.class.getDeclaredMethod(
                                        "randomTick",
                                        BlockState.class,
                                        ServerLevel.class,
                                        BlockPos.class,
                                        RandomSource.class
                                );
                                randomTickMethod.setAccessible(true);
                                randomTickMethod.invoke(cropState.getBlock(), cropState, world, offset, random);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
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
        BlockState blockstate = pushEntitiesUp(state, Blocks.SOUL_SOIL.defaultBlockState(), level, pos);
        level.setBlockAndUpdate(pos, blockstate);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
    }

    private static boolean shouldMaintainFarmland(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos.above()).is(BlockTags.MAINTAINS_FARMLAND);
    }

    private static boolean isNearSoul(LevelReader level, BlockPos pos) {
        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            if (level.getBlockState(blockpos).getBlock().equals(WEIGHTED_SOULS.get())) {
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
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(SOULCHARGED, false);
    }

    /**
     * ВАЖНО: так мы говорим CropBlock'ам, что эта почва "удобренная",
     * когда SOULCHARGED == true. getGrowthSpeed это учтёт.
     */
    @Override
    public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(SOULCHARGED);
    }
}
