package net.mcreator.concoction.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.mcreator.concoction.init.ConcoctionModBlocks;


import com.mojang.serialization.MapCodec;

public class SoakedRiceBlockBlock extends FallingBlock {
	public static final MapCodec<SoakedRiceBlockBlock> CODEC = simpleCodec(properties -> new SoakedRiceBlockBlock());

	@Override
	public MapCodec<SoakedRiceBlockBlock> codec() {
		return CODEC;
	}

	public SoakedRiceBlockBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).sound(SoundType.WET_SPONGE).strength(0.5f));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
	if (level.dimensionType().ultraWarm()) {
		level.setBlock(pos, ConcoctionModBlocks.RICE_BLOCK.get().defaultBlockState(), 3);
		level.levelEvent(2009, pos, 0);
		level.playSound((Player) null, pos, SoundEvents.WET_SPONGE_DRIES, SoundSource.BLOCKS, 1.0F,
				(1.0F + level.getRandom().nextFloat() * 0.2F) * 0.7F);
	} else {
		level.scheduleTick(pos, this, this.getDelayAfterPlace());
	}
}


	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		Direction direction = Direction.getRandom(random);
		if (direction != Direction.UP) {
			BlockPos neighborPos = pos.relative(direction);
			BlockState neighborState = level.getBlockState(neighborPos);
			if (!state.canOcclude() || !neighborState.isFaceSturdy(level, neighborPos, direction.getOpposite())) {
				double x = pos.getX();
				double y = pos.getY();
				double z = pos.getZ();
				if (direction == Direction.DOWN) {
					y -= 0.05;
					x += random.nextDouble();
					z += random.nextDouble();
				} else {
					y += random.nextDouble() * 0.8;
					if (direction.getAxis() == Direction.Axis.X) {
						z += random.nextDouble();
						x += direction == Direction.EAST ? 1.0 : 0.05;
					} else {
						x += random.nextDouble();
						z += direction == Direction.SOUTH ? 1.0 : 0.05;
					}
				}
				level.addParticle(ParticleTypes.DRIPPING_WATER, x, y, z, 0.0F, 0.0F, 0.0F);
			}
		}
	}
}
