
package net.mcreator.concoction.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

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
			FluidState fluidstateNew = this.getNewLiquid(level, blockposDown, blockstateDown);

			// Проверка на контакт с лавой
			boolean touchingLava = false;
			for (Direction direction : Direction.values()) {
				BlockPos neighborPos = pos.relative(direction);
				if (level.getFluidState(neighborPos).getType().defaultFluidState().is(net.minecraft.world.level.material.Fluids.LAVA) ||
						level.getFluidState(neighborPos).getType().defaultFluidState().is(net.minecraft.world.level.material.Fluids.FLOWING_LAVA)) {
					touchingLava = true;
					break;
				}
			}

			if (touchingLava) {
				// Превращаем жидкость в SOUL_SOIL при контакте с лавой
				level.setBlockAndUpdate(pos, Blocks.SOUL_SOIL.defaultBlockState());
				return;
			}
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
