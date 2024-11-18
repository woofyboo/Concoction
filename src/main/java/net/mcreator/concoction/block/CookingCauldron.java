package net.mcreator.concoction.block;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.block.entity.CookingCauldronEntity;
import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import static java.lang.Math.pow;

public class CookingCauldron extends LayeredCauldronBlock implements EntityBlock {

    public CookingCauldron(Biome.Precipitation p_304591_, CauldronInteraction.InteractionMap p_304761_, Properties p_153522_) {
        super(p_304591_, p_304761_, p_153522_);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pSource) {
        Block blockBelow = pLevel.getBlockState(pPos.below()).getBlock();
        if (pLevel.getFluidState(pPos.below()).is(Fluids.LAVA.getSource()) || blockBelow instanceof FireBlock ||
            blockBelow instanceof MagmaBlock || blockBelow instanceof CampfireBlock) {
            if (pSource.nextInt(2) == 0) {
                pLevel.addParticle(ParticleTypes.BUBBLE,
                        pPos.getX() + 0.5 + pow(-1, pSource.nextInt(2))*pSource.nextFloat()/3f,
                        pPos.getY() + 1,
                        pPos.getZ() + 0.5 + pow(-1, pSource.nextInt(2))*pSource.nextFloat()/3f,
                        0.0,0.05,0.0);
                pLevel.addParticle(ParticleTypes.BUBBLE_POP,
                        pPos.getX() + 0.5 + pow(-1, pSource.nextInt(2))*pSource.nextFloat()/4f,
                        pPos.getY() + 1,
                        pPos.getZ() + 0.5 + pow(-1, pSource.nextInt(2))*pSource.nextFloat()/4f,
                        0.0,0.03,0.0);
                pLevel.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        pPos.getX() + 0.5 + pow(-1, pSource.nextInt(2))*pSource.nextFloat()/4f,
                        pPos.getY() + 1,
                        pPos.getZ() + 0.5 + pow(-1, pSource.nextInt(2))*pSource.nextFloat()/4f,
                        0.0,0.06,0.0);
            }
        }
        super.animateTick(pState, pLevel, pPos, pSource);
    }

    @Override
    public void onPlace(BlockState p_51978_, Level p_51979_, BlockPos p_51980_, BlockState p_51981_, boolean p_51982_) {
        ConcoctionMod.LOGGER.debug("Cauldron placed at {}", p_51980_);
        super.onPlace(p_51978_, p_51979_, p_51980_, p_51981_, p_51982_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CookingCauldronEntity(pPos, pState);
    }

    @SuppressWarnings("unchecked") // Due to generics, an unchecked cast is necessary here.
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // You can return different tickers here, depending on whatever factors you want. A common use case would be
        // to return different tickers on the client or server, only tick one side to begin with,
        // or only return a ticker for some blockstates (e.g. when using a "my machine is working" blockstate property).
        return type == ConcoctionModBlockEntities.COOKING_CAULDRON.get() ? (BlockEntityTicker<T>) CookingCauldronEntity::tick : null;
    }

    @Override
    public ItemInteractionResult useItemOn(
            ItemStack pItem, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult p_316140_) {
        if (pLevel.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        } else {
            CauldronInteraction cauldroninteraction = this.interactions.map().get(pItem.getItem());
            ItemInteractionResult r = cauldroninteraction.interact(pState, pLevel, pPos, pPlayer, pHand, pItem);
            if (r.consumesAction()) {
                return r;
            }

            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof CookingCauldronEntity cauldron) {
                if (pItem.getItem().equals(Items.AIR)) {
//                    if (pPlayer.isShiftKeyDown()) pPlayer.addItem(cauldron.takeItemOnClick(true));
                    pPlayer.addItem(cauldron.takeItemOnClick(false));
                } else {
//                    if (pPlayer.isShiftKeyDown()) cauldron.addItemOnClick(pItem, pItem.getCount(), pPlayer.isCreative());
                    cauldron.addItemOnClick(pItem, 1, pPlayer.isCreative());
                }
            }
            return ItemInteractionResult.CONSUME;
        }
    }

    @Override
    public void onRemove(BlockState p_54085_, Level p_54086_, BlockPos p_54087_, BlockState p_54088_, boolean p_54089_) {
        Containers.dropContentsOnDestroy(p_54085_, p_54088_, p_54086_, p_54087_);
        super.onRemove(p_54085_, p_54086_, p_54087_, p_54088_, p_54089_);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState p_54055_) {
        return false;
    }



}
