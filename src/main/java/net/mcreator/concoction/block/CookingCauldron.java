package net.mcreator.concoction.block;

import io.netty.buffer.Unpooled;
import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.block.entity.CookingCauldronEntity;
import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.mcreator.concoction.init.ConcoctionModSounds;
import net.mcreator.concoction.utils.Utils;
import net.mcreator.concoction.world.inventory.BoilingCauldronInterfaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;


import java.util.Map;

import static java.lang.Math.pow;

//@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class CookingCauldron extends LayeredCauldronBlock implements EntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty COOKING = BooleanProperty.create("cooking");
//    public static final EnumProperty WATER_TYPE = BooleanProperty.create("cooking");
    private boolean cookingSoundPlaying = false;
    private long lastBoilingSoundTime = 0;
	private static final long BOILING_SOUND_INTERVAL_MS = 2500;
    public CookingCauldron(Biome.Precipitation p_304591_, CauldronInteraction.InteractionMap p_304761_, Properties p_153522_) {
        super(p_304591_, p_304761_, p_153522_);
        registerDefaultState(stateDefinition.any()
                .setValue(LIT, false)
                .setValue(COOKING, false));
    }

    @Override
@OnlyIn(Dist.CLIENT)
public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pSource) {
    if (pState.getValue(LIT)) {
        final boolean isCooking = pState.getValue(COOKING);

        long currentTime = System.currentTimeMillis(); // использовать таймер

            if (currentTime - lastBoilingSoundTime >= BOILING_SOUND_INTERVAL_MS) {
                Player localPlayer = Minecraft.getInstance().player;
if (localPlayer != null) {
    double dx = pPos.getX() + 0.5 - localPlayer.getX();
    double dy = pPos.getY() + 0.5 - localPlayer.getY();
    double dz = pPos.getZ() + 0.5 - localPlayer.getZ();
    double distanceSq = dx * dx + dy * dy + dz * dz;

    double maxDistance = 16.0;
    if (distanceSq < maxDistance * maxDistance) {
        double distance = Math.sqrt(distanceSq);

        // Exponential falloff: closer = louder, drops off quickly
        float volume = (float)Math.pow(1.0 - (distance / maxDistance), 3);
        volume = Math.max(0.0F, Math.min(volume, 1.0F)); // Clamp to [0, 1]

        pLevel.playLocalSound(
            pPos.getX() + 0.5,
            pPos.getY() + 0.5,
            pPos.getZ() + 0.5,
            ConcoctionModSounds.CAULDRON_BOILING.get(),
            SoundSource.BLOCKS,
            0.6F * volume, // base volume scaled by distance
            pSource.nextFloat() * 0.7F + 0.6F,
            false
        );
}


                lastBoilingSoundTime = currentTime;
            }
        } 

        if (pSource.nextInt(2) == 0) {
            pLevel.addParticle(ParticleTypes.BUBBLE, pPos.getX() + 0.5 + pow(-1, pSource.nextInt(2))*pSource.nextFloat()/3f,
                    pPos.getY() + 1, pPos.getZ() + 0.5 + pow(-1, pSource.nextInt(2))*pSource.nextFloat()/3f,
                    0.0, 0.1, 0.0);
            pLevel.addParticle(ParticleTypes.BUBBLE_POP, pPos.getX() + 0.5 + pow(-1, pSource.nextInt(2))*pSource.nextFloat()/4f,
                    pPos.getY() + 1, pPos.getZ() + 0.5 + pow(-1, pSource.nextInt(2))*pSource.nextFloat()/4f,
                    0.0, 0.1, 0.0);
        }

        if (isCooking) {
            pLevel.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    pPos.getX() + 0.5 + pow(-1, pSource.nextInt(2))*pSource.nextFloat()/4f,
                    pPos.getY() + 1,
                    pPos.getZ() + 0.5 + pow(-1, pSource.nextInt(2))*pSource.nextFloat()/4f,
                    0.0, 0.07, 0.0);
        }
    }

    super.animateTick(pState, pLevel, pPos, pSource);
}



    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(LIT, COOKING);
    }

    @Override
    public void onPlace(BlockState p_51978_, Level p_51979_, BlockPos p_51980_, BlockState p_51981_, boolean p_51982_) {
        // p_51978_.setValue(COOKING, false);
//        ConcoctionMod.LOGGER.debug("Cauldron placed at {}, {}", p_51980_, p_51981_.getValues());
        super.onPlace(p_51978_, p_51979_, p_51980_, p_51981_, p_51982_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CookingCauldronEntity(pPos, pState);
    }

    @Nullable // Due to generics, an unchecked cast is necessary here.
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type) {
        if(pLevel.isClientSide()) {
            return null;
        }
        // You can return different tickers here, depending on whatever factors you want. A common use case would be
        // to return different tickers on the client or server, only tick one side to begin with,
        // or only return a ticker for some blockstates (e.g. when using a "my machine is working" blockstate property).
        return (type == ConcoctionModBlockEntities.COOKING_CAULDRON.get()) ? (level, pPos, pState1, pEntity) -> ((CookingCauldronEntity)pEntity).tick(level, pPos, pState1) : null;
    }

    @Override
    public ItemInteractionResult useItemOn(
            ItemStack pItem, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult p_316140_) {
        if (pLevel.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        } else if (!pPlayer.isShiftKeyDown()) {

            MenuProvider containerProvider = new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("container.cooking_cauldron");
                }

                @Override
                public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                    FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
                    packetBuffer.writeBlockPos(pPos);
                    return new BoilingCauldronInterfaceMenu(windowId, playerInventory, packetBuffer);
                }
            };

            // Открываем меню для игрока
            pPlayer.openMenu(containerProvider, (buf) -> {
                buf.writeBlockPos(pPos);
            });

            // Отменяем обычную обработку использования блока
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.CONSUME;
    }

    public Map<String, String>  decreesItemCountFromResult(Map<String, String> result) {
        int new_count = Integer.parseInt(result.get("count"))-1;
        if (new_count <= 0) {
            return Map.ofEntries(
                    Map.entry("id",""),
                    Map.entry("count",""),
                    Map.entry("interactionType",""),
                    Map.entry("experience", "0")
            );

        } else {
            return Map.ofEntries(
                    Map.entry("id",result.get("id")),
                    Map.entry("count",String.valueOf(new_count)),
                    Map.entry("interactionType",result.get("interactionType")),
                    Map.entry("experience", result.get("experience"))
            );
        }
    }

    @Override
    public void onRemove(BlockState p_54085_, Level p_54086_, BlockPos p_54087_, BlockState p_54088_, boolean p_54089_) {
        Containers.dropContentsOnDestroy(p_54085_, p_54088_, p_54086_, p_54087_);
        super.onRemove(p_54085_, p_54086_, p_54087_, p_54088_, p_54089_);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        
        // Передаем информацию об изменении соседнего блока в сущность блока
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof CookingCauldronEntity cookingCauldron) {
            cookingCauldron.neighborChanged(state, level, pos, block, fromPos);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState p_54055_) {
        return false;
    }

//    @SubscribeEvent
//    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
//        // Parameters are the block's state, the level the block is in, the block's position, and the tint index.
//        // The level and position may be null.
//        event.register((state, level, pos, tintIndex) -> {
////                    BlockColors
//                    // Replace with your own calculation. See the BlockColors class for vanilla references.
//                    // Colors are in ARGB format. Generally, if the tint index is -1, it means that no tinting
//                    // should take place and a default value should be used instead.
//                    return 0xFFFFFFFF;
//                },
//                // A varargs of blocks to apply the tinting to
//                Blocks.WATER_CAULDRON);
//    }
}
