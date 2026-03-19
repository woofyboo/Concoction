package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class MintBlock extends DoublePlantBlock {
	public MintBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).sound(SoundType.GRASS).instabreak().noCollission().offsetType(BlockBehaviour.OffsetType.NONE).pushReaction(PushReaction.DESTROY));
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 100;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 60;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState blockstate, Level world, BlockPos pos, RandomSource random) {
		super.animateTick(blockstate, world, pos, random);
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();
		if (Math.random() < 0.06) {
			if (Math.random() < 0.7) {
				world.addParticle((SimpleParticleType) (ConcoctionModParticleTypes.MINT_LEAF_PARTICLE_VARIANT_1.get()), (x + 0.5), (y + 0.5), (z + 0.5), (Mth.nextDouble(RandomSource.create(), -0.5, 0.5)),
						(Mth.nextDouble(RandomSource.create(), -0.5, 0.5)), (Mth.nextDouble(RandomSource.create(), -0.5, 0.5)));
			} else {
				world.addParticle((SimpleParticleType) (ConcoctionModParticleTypes.MINT_LEAF_PARTICLE_VARIANT_2.get()), (x + 0.5), (y + 0.5), (z + 0.5), (Mth.nextDouble(RandomSource.create(), -0.5, 0.5)),
						(Mth.nextDouble(RandomSource.create(), -0.5, 0.5)), (Mth.nextDouble(RandomSource.create(), -0.5, 0.5)));
			}
		}
	}

}
