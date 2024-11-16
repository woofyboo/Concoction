package net.mcreator.concoction.block;

import net.mcreator.concoction.ConcoctionMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.LavaFluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.logging.Logger;

import static java.lang.Math.pow;

public class CookingCauldron extends LayeredCauldronBlock {
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
}
