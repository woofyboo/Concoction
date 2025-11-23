package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike; // <-- ВАЖНО: отсюда!
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.world.item.ItemStack;


import java.util.Objects;

public class NetherPepperCropBlock extends CropBlock {
    public static final int MAX_AGE = 5;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

    public NetherPepperCropBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .sound(SoundType.GRASS)
                .instabreak()
                .noCollission()
                .noOcclusion()
                .randomTicks()
                .pushReaction(PushReaction.DESTROY)
                .lightLevel(s -> 3)
                .hasPostProcess((bs, br, bp) -> true)
                .emissiveRendering((bs, br, bp) -> true)
                .isRedstoneConductor((bs, br, bp) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
        if (!(worldIn instanceof LevelReader levelReader)) {
            return false;
        }
        return canSurvive(state, levelReader, pos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState soil = worldIn.getBlockState(below);
        // растёт только на Soulland
        return soil.getBlock() instanceof SoullandBlock;
    }

    /**
     * Рост: ванильная формула, но БЕЗ проверки света и с учётом SoullandBlock.
     */
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

    /**
     * Копия ванильного getGrowthSpeed, но:
     *  - вместо Blocks.FARMLAND используем SoullandBlock;
     *  - вместо moisture > 0 используем SOULCHARGED = true.
     */
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
                        bonus = 3.0F; // как влажная пашня
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
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        if (!world.isClientSide()) {
            ((ServerLevel) world).scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pItem, BlockState pState, Level pLevel, BlockPos pPos,
                                              Player pPlayer, InteractionHand hand, BlockHitResult hit) {
        if (!pPlayer.isShiftKeyDown() && pState.getBlock() == ConcoctionModBlocks.NETHER_PEPPER_CROP.get()) {
            if (pState.getValue(AGE) == MAX_AGE) {
                pPlayer.swing(InteractionHand.MAIN_HAND, true);
                if (!pLevel.isClientSide()) {
                    pLevel.playSound(
                            null,
                            pPos,
                            Objects.requireNonNull(BuiltInRegistries.SOUND_EVENT.get(
                                    ResourceLocation.parse("block.sweet_berry_bush.pick_berries")
                            )),
                            SoundSource.BLOCKS,
                            1.0F,
                            1.0F
                    );
                } else {
                    pLevel.playLocalSound(
                            pPos,
                            Objects.requireNonNull(BuiltInRegistries.SOUND_EVENT.get(
                                    ResourceLocation.parse("block.sweet_berry_bush.pick_berries")
                            )),
                            SoundSource.BLOCKS,
                            1.0F,
                            1.0F,
                            false
                    );
                }

                if (pLevel instanceof ServerLevel serverLevel) {
                    pLevel.setBlock(pPos, pState.setValue(AGE, 2), 3);

                    ItemEntity drop = new ItemEntity(
                            serverLevel,
                            pPos.getX() + 0.5,
                            pPos.getY() + 0.5,
                            pPos.getZ() + 0.5,
                            new ItemStack(ConcoctionModItems.REAPPER.get(), 1)
                    );
                    drop.setPickUpDelay(10);
                    serverLevel.addFreshEntity(drop);

                    if (Math.random() < 0.3) {
                        ItemEntity extra = new ItemEntity(
                                serverLevel,
                                pPos.getX() + 0.5,
                                pPos.getY() + 0.5,
                                pPos.getZ() + 0.5,
                                new ItemStack(ConcoctionModItems.REAPPER.get())
                        );
                        extra.setPickUpDelay(10);
                        serverLevel.addFreshEntity(extra);
                    }

                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        return super.useItemOn(pItem, pState, pLevel, pPos, pPlayer, hand, hit);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        long time = world.getDayTime() % 24000;
        if (time >= 17950 && time <= 18050) {
            for (Player player : world.getEntitiesOfClass(
                    Player.class,
                    new net.minecraft.world.phys.AABB(pos).inflate(8)
            )) {
                if (player.isAlive()) {
                    player.hurt(
                            new DamageSource(
                                    world.holderOrThrow(
                                            ResourceKey.create(
                                                    Registries.DAMAGE_TYPE,
                                                    ResourceLocation.parse("concoction:soul_damage")
                                            )
                                    )
                            ),
                            4.0F
                    );

                    double px = player.getX() + world.random.nextDouble() * 0.6 - 0.3;
                    double py = player.getY() + player.getBbHeight() * 0.5 + world.random.nextDouble() * 0.6 - 0.3;
                    double pz = player.getZ() + world.random.nextDouble() * 0.6 - 0.3;
                    double vx = world.random.nextDouble() * 0.26 - 0.13;
                    double vy = world.random.nextDouble() * 0.07 + 0.13;
                    double vz = world.random.nextDouble() * 0.26 - 0.13;

                    world.sendParticles(ParticleTypes.SOUL, px, py, pz, 1, vx, vy, vz, 0.2);
                }
            }
        }

        world.scheduleTick(pos, this, 20);
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
        return switch (state.getValue(AGE)) {
            default -> Block.box(1, 0, 1, 15, 15, 15);
            case 0 -> Block.box(4, 0, 4, 12, 8, 12);
            case 1 -> Block.box(2, 0, 2, 14, 12, 14);
            case 2 -> Block.box(1, 0, 1, 15, 15, 15);
            case 3 -> Block.box(1, 0, 1, 15, 15, 15);
            case 4 -> Block.box(1, 0, 1, 15, 15, 15);
            case 5 -> Block.box(1, 0, 1, 15, 15, 15);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 0;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(ConcoctionModItems.REAPPER_SEEDS.get());
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 0;
    }

    @Override
    public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
        return PathType.OPEN;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ConcoctionModItems.REAPPER_SEEDS.get();
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState blockstate, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(blockstate, world, pos, random);

        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        long time = world.dayTime() % 24000;
        if (time >= 17950 && time <= 18050) {
            if (Math.random() < 0.65) {
                world.addParticle(
                        ParticleTypes.SOUL,
                        x + Mth.nextDouble(random, 0.2, 0.8),
                        y + Mth.nextDouble(random, 0.2, 0.8),
                        z + Mth.nextDouble(random, 0.2, 0.8),
                        Mth.nextDouble(random, -0.13, 0.13),
                        Mth.nextDouble(random, 0.13, 0.2),
                        Mth.nextDouble(random, -0.13, 0.13)
                );
            }

            if (Math.random() < 0.1) {
                if (world instanceof Level level) {
                    ResourceLocation soulEscape = ResourceLocation.parse("particle.soul_escape");
                    float pitch = (float)(0.8 + Math.random() * 0.4);
                    if (!level.isClientSide()) {
                        level.playSound(
                                null,
                                BlockPos.containing(x, y, z),
                                BuiltInRegistries.SOUND_EVENT.get(soulEscape),
                                SoundSource.BLOCKS,
                                0.6F,
                                pitch
                        );
                    } else {
                        level.playLocalSound(
                                x, y, z,
                                BuiltInRegistries.SOUND_EVENT.get(soulEscape),
                                SoundSource.BLOCKS,
                                0.6F,
                                pitch,
                                false
                        );
                    }
                }
            }

            if (Math.random() < 0.02) {
                if (world instanceof Level level) {
                    float pitch = (float)(0.5 + Math.random() * 0.5);
                    if (!level.isClientSide()) {
                        level.playSound(
                                null,
                                BlockPos.containing(x, y, z),
                                SoundEvents.GHAST_HURT,
                                SoundSource.HOSTILE,
                                0.4F,
                                pitch
                        );
                    } else {
                        level.playLocalSound(
                                x, y, z,
                                SoundEvents.GHAST_HURT,
                                SoundSource.HOSTILE,
                                0.4F,
                                pitch,
                                false
                        );
                    }
                }
            }
        }
    }
}
