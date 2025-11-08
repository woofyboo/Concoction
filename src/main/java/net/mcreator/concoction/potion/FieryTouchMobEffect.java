
package net.mcreator.concoction.potion;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class FieryTouchMobEffect extends MobEffect {
	public FieryTouchMobEffect() {
		super(MobEffectCategory.NEUTRAL, -39424);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		boolean found = false;
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		double sx = 0;
		double sy = 0;
		double sz = 0;
		if (!(entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(ConcoctionModMobEffects.FROST_TOUCH))) {
			sx = -1;
			found = false;
			for (int index0 = 0; index0 < 3; index0++) {
				sy = -1;
				for (int index1 = 0; index1 < 3; index1++) {
					sz = -1;
					for (int index2 = 0; index2 < 3; index2++) {
						if ((world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz))).getBlock() == Blocks.ICE || (world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz))).getBlock() == Blocks.FROSTED_ICE) {
							if (!world.isEmptyBlock(BlockPos.containing(x + sx, (y + sy) - 1, z + sz))) {
								world.setBlock(BlockPos.containing(x + sx, y + sy, z + sz), Blocks.WATER.defaultBlockState(), 3);
								world.addParticle(ParticleTypes.CLOUD, (x + sx + 0.5), (y + sy + 0.5), (z + sz + 0.5), 0, 0, 0);
							}
						} else if (world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz)).isFaceSturdy(world, BlockPos.containing(x + sx, y + sy, z + sz), Direction.UP) && world.isEmptyBlock(BlockPos.containing(x + sx, y + sy + 1, z + sz))
								&& entity.isSprinting()) {
							world.setBlock(BlockPos.containing(x + sx, y + sy + 1, z + sz), Blocks.FIRE.defaultBlockState(), 3);
						} else if ((world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz))).getBlock() == Blocks.SNOW || (world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz))).getBlock() == Blocks.POWDER_SNOW) {
							world.addParticle(ParticleTypes.CLOUD, (x + sx + 0.5), (y + sy + 0.5), (z + sz + 0.5), 0, 0, 0);
							world.setBlock(BlockPos.containing(x + sx, y + sy, z + sz), Blocks.AIR.defaultBlockState(), 3);
						}
						sz = sz + 1;
					}
					sy = sy + 1;
				}
				sx = sx + 1;
			}
		}
		return super.applyEffectTick(entity, amplifier);
	}
}
