package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike; // <-- тоже отсюда
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WeepingOnionsCropBlock extends CropBlock {

    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

    public WeepingOnionsCropBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .sound(SoundType.CROP)
                .instabreak()
                .noCollission()
                .noOcclusion()
                .randomTicks()
                .pushReaction(PushReaction.DESTROY)
                .isRedstoneConductor((bs, br, bp) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) {
            return;
        }

        int age = this.getAge(state);
        int maxAge = this.getMaxAge();

        if (age < maxAge) {
            float f = getSoullandGrowthSpeed(state, level, pos);
            if (random.nextInt((int)(25.0F / f) + 1) == 0) {
                level.setBlock(pos, this.getStateForAge(age + 1), 2);
            }
        }
    }

    private float getSoullandGrowthSpeed(BlockState state, LevelReader level, BlockPos pos) {
        float f = 1.0F;
        Block block = state.getBlock();
        BlockPos belowPos = pos.below();

        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                float bonus = 0.0F;
                BlockState soil = level.getBlockState(belowPos.offset(dx, 0, dz));

                if (soil.getBlock() instanceof SoullandBlock) {
                    bonus = 1.0F;
                    if (soil.getValue(SoullandBlock.SOULCHARGED)) {
                        bonus = 3.0F;
                    }
                }

                if (dx != 0 || dz != 0) {
                    bonus /= 4.0F;
                }

                f += bonus;
            }
        }

        BlockPos north = pos.north();
        BlockPos south = pos.south();
        BlockPos west  = pos.west();
        BlockPos east  = pos.east();

        boolean sameX = level.getBlockState(west).is(block) || level.getBlockState(east).is(block);
        boolean sameZ = level.getBlockState(north).is(block) || level.getBlockState(south).is(block);

        if (sameX && sameZ) {
            f /= 2.0F;
        } else {
            boolean diagonal =
                    level.getBlockState(west.north()).is(block)
                            || level.getBlockState(east.north()).is(block)
                            || level.getBlockState(east.south()).is(block)
                            || level.getBlockState(west.south()).is(block);
            if (diagonal) {
                f /= 2.0F;
            }
        }

        return f;
    }

    @Override
    public boolean mayPlaceOn(BlockState soilState, BlockGetter world, BlockPos pos) {
        return soilState.getBlock() instanceof SoullandBlock;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState soil = world.getBlockState(below);
        return soil.getBlock() instanceof SoullandBlock;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return switch (state.getValue(AGE)) {
            case 0 -> Block.box(3, 0, 3, 13, 5, 13);
            case 1 -> Block.box(2, 0, 2, 14, 10, 14);
            case 2 -> Block.box(1, 0, 1, 15, 13, 15);
            case 3 -> Block.box(1, 0, 1, 15, 14, 15);
            default -> Block.box(1, 0, 1, 15, 15, 15);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ConcoctionModItems.WEEPING_ONION.get();
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(ConcoctionModItems.WEEPING_ONION.get());
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return 0;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 0;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 0;
    }

    @Override
    public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
        return PathType.OPEN;
    }
}
