
package net.mcreator.concoction.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.ParticleOptions;

import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.init.ConcoctionModFluids;
import net.mcreator.concoction.init.ConcoctionModFluidTypes;
import net.mcreator.concoction.init.ConcoctionModBlocks;

public abstract class WeightedSoulsFluid extends BaseFlowingFluid {
	public static final BaseFlowingFluid.Properties PROPERTIES = new BaseFlowingFluid.Properties(() -> ConcoctionModFluidTypes.WEIGHTED_SOULS_TYPE.get(), () -> ConcoctionModFluids.WEIGHTED_SOULS.get(),
			() -> ConcoctionModFluids.FLOWING_WEIGHTED_SOULS.get()).explosionResistance(100f).tickRate(20).levelDecreasePerBlock(2).bucket(() -> ConcoctionModItems.WEIGHTED_SOULS_BUCKET.get())
			.block(() -> (LiquidBlock) ConcoctionModBlocks.WEIGHTED_SOULS.get());

	private WeightedSoulsFluid() {
		super(PROPERTIES);
	}

	@Override
	public ParticleOptions getDripParticle() {
		return ParticleTypes.SOUL;
	}

	public static class Source extends WeightedSoulsFluid {
		public int getAmount(FluidState state) {
			return 8;
		}

		public boolean isSource(FluidState state) {
			return true;
		}
	}
			@Override
public void spread(Level level, BlockPos pos, FluidState fluidState) {
    if (!fluidState.isEmpty()) {
        BlockState blockstate = level.getBlockState(pos);
        BlockPos blockposDown = pos.below();
        BlockState blockstateDown = level.getBlockState(blockposDown);

        // Проверка, есть ли лава на уровне текущем или выше уровня с душами
        boolean lavaOnLevelOrHigher = false;
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            FluidState neighborFluidState = level.getFluidState(neighborPos);
            if (neighborFluidState.getType().defaultFluidState().is(net.minecraft.world.level.material.Fluids.LAVA) ||
                neighborFluidState.getType().defaultFluidState().is(net.minecraft.world.level.material.Fluids.FLOWING_LAVA)) {
                if (neighborPos.getY() >= pos.getY()) {
                    lavaOnLevelOrHigher = true;
                    break;
                }
            }
        }

        // Если лава на текущем уровне или выше, заменяем жидкие души на черный камень
        if (lavaOnLevelOrHigher) {
            level.setBlockAndUpdate(pos, Blocks.BLACKSTONE.defaultBlockState());
            // Звук при установке черного камня с громкостью 70% и эффектом погашения лавы
            level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.7F, 1.0F);
        } else {
            // Если лава снизу
            if (level.getFluidState(blockposDown).getType().defaultFluidState().is(net.minecraft.world.level.material.Fluids.LAVA) ||
                level.getFluidState(blockposDown).getType().defaultFluidState().is(net.minecraft.world.level.material.Fluids.FLOWING_LAVA)) {
                
                // Заменяем лаву на черный камень на блоке ниже
                level.setBlockAndUpdate(blockposDown, Blocks.BLACKSTONE.defaultBlockState());
                // Звук при установке черного камня с громкостью 70% и эффектом погашения лавы
                level.playSound(null, blockposDown, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.7F, 1.0F);
            }
        }

        // Продолжаем распространение жидкости (она должна течь дальше)
        super.spread(level, pos, fluidState);
    } else {
        super.spread(level, pos, fluidState);
    }
}








	public static class Flowing extends WeightedSoulsFluid {
		protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder);
			builder.add(LEVEL);
		}

		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}

		public boolean isSource(FluidState state) {
			return false;
		}
	}
}
