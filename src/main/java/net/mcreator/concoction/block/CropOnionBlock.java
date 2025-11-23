package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
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
import net.neoforged.neoforge.common.SpecialPlantable;

import java.util.Objects;

import static net.minecraft.core.registries.Registries.SOUND_EVENT;

public class CropOnionBlock extends CropBlock {
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

    public CropOnionBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .sound(SoundType.GRASS)
                .instabreak()
                .noCollission()
                .noOcclusion()
                .randomTicks()
                .pushReaction(PushReaction.DESTROY)
                .isRedstoneConductor((bs, br, bp) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    /**
     * Разрешаем сажать лук и на обычную пашню, и на SoullandBlock.
     */
    @Override
    public boolean mayPlaceOn(BlockState state, BlockGetter world, BlockPos pos) {
        return state.getBlock() instanceof FarmBlock
                || state.getBlock() instanceof SoullandBlock;
    }

    /**
     * Если лук растёт на SoullandBlock — он мутирует в CROP_WEEPING_ONION,
     * сохранив как можно ближе свою стадию роста.
     */
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) {
            return;
        }

        // если снизу именно SoullandBlock — превращаемся в WeepingOnion
        if (level.getBlockState(pos.below()).getBlock() instanceof SoullandBlock) {
            int currentAge = this.getAge(state);

            Block weepingBlock = ConcoctionModBlocks.CROP_WEEPING_ONION.get();
            if (weepingBlock instanceof CropBlock weepingCrop) {
                int maxWeepingAge = weepingCrop.getMaxAge();
                int clampedAge = Mth.clamp(currentAge, 0, maxWeepingAge);

                BlockState newState = weepingCrop.getStateForAge(clampedAge);
                level.setBlock(pos, newState, 2);
            } else {
                // на всякий пожарный
                level.setBlock(pos, weepingBlock.defaultBlockState(), 2);
            }

            // дальше как лук уже не растём
            return;
        }

        // обычный рост, если не на пашне душ
        super.randomTick(state, level, pos, random);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
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
        return switch (state.getValue(AGE)) {
            default -> Block.box(1, 0, 1, 15, 15, 15);

            case 0 -> Block.box(3, 0, 3, 13, 5, 13);
            case 1 -> Block.box(2, 0, 2, 14, 10, 14);
            case 2 -> Block.box(1, 0, 1, 15, 13, 15);
            case 3 -> Block.box(1, 0, 1, 15, 14, 15);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 100;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(ConcoctionModItems.ONION.get());
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 25;
    }

    @Override
    public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
        return PathType.OPEN;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pItem, BlockState pState, Level pLevel, BlockPos pPos,
                                              Player pPlayer, InteractionHand hand, BlockHitResult hit) {
        if (!pPlayer.isShiftKeyDown()
                && pState.getValue(AGE) == 3
                && pPlayer.getMainHandItem().getItem() == Items.SHEARS) {

            if (pLevel instanceof ServerLevel _level) {
                if (!pPlayer.isCreative()) {
                    pItem.hurtAndBreak(1, pPlayer, pPlayer.getEquipmentSlotForItem(pItem));
                }

                // сбрасываем лук в начальную стадию после "среза"
                pLevel.setBlock(pPos, this.defaultBlockState(), 2);

                if (!_level.isClientSide()) {
                    _level.playSound(null, pPos, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1, 1);
                } else {
                    _level.playLocalSound(pPos, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1, 1, false);
                }

                ItemEntity entityToSpawn = new ItemEntity(
                        _level,
                        (pPos.getX() + 0.5),
                        (pPos.getY() + 0.5),
                        (pPos.getZ() + 0.5),
                        new ItemStack(ConcoctionModItems.GREEN_ONION.get(), 1)
                );
                entityToSpawn.setPickUpDelay(10);
                _level.addFreshEntity(entityToSpawn);

                if (Math.random() < 0.5) {
                    ItemEntity extra = new ItemEntity(
                            _level,
                            (pPos.getX() + 0.5),
                            (pPos.getY() + 0.5),
                            (pPos.getZ() + 0.5),
                            new ItemStack(ConcoctionModItems.GREEN_ONION.get())
                    );
                    extra.setPickUpDelay(10);
                    _level.addFreshEntity(extra);
                }
            }

            return ItemInteractionResult.SUCCESS;
        } else {
            return super.useItemOn(pItem, pState, pLevel, pPos, pPlayer, hand, hit);
        }
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ConcoctionModItems.ONION.get();
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }
}
