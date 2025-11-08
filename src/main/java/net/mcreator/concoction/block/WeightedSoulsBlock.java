package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.WorldGenLevel;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.pathfinder.PathComputationType;

import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;


import net.mcreator.concoction.init.ConcoctionModFluids;

public class WeightedSoulsBlock extends LiquidBlock {
	public WeightedSoulsBlock() {
		super(ConcoctionModFluids.WEIGHTED_SOULS.get(), BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(100f).hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).lightLevel(s -> 6).noCollission()
				.noLootTable().liquid().pushReaction(PushReaction.DESTROY).sound(SoundType.EMPTY).replaceable());
	}

	@Override
	public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(blockstate, world, pos, oldState, moving);

		if (!((world instanceof Level level ? level.dimension() :
				(world instanceof WorldGenLevel wgl ? wgl.getLevel().dimension() : Level.OVERWORLD)) == Level.NETHER)) {
			world.setBlock(BlockPos.containing(pos.getX(), pos.getY(), pos.getZ()), ConcoctionModBlocks.SOUL_ICE.get().defaultBlockState(), 3);
		}
	}
	
	@Override
	public boolean isPathfindable(BlockState p_53267_, PathComputationType p_53270_) {
		return false;
	}


	@Override
	public void entityInside(BlockState blockstate, Level world, BlockPos pos, Entity entity) {
		super.entityInside(blockstate, world, pos, entity);
		if (entity == null)
			return;
		if (!world.isClientSide()) {
			if (!entity.isInLava()) {
				entity.setTicksFrozen(300);
			}
			entity.clearFire();
			entity.hurt(new DamageSource(world.holderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("concoction:soul_damage")))), 2);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState blockstate, Level world, BlockPos pos, RandomSource random) {
		super.animateTick(blockstate, world, pos, random);
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();
		if (world.isEmptyBlock(BlockPos.containing(x, y + 1, z))) {
			if (Math.random() < 0.015) {
				world.addParticle(ParticleTypes.SOUL, (x + 0.5), (y + 0.9), (z + 0.5), 0, (Mth.nextDouble(RandomSource.create(), 0.3, 0.45)), 0);
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("particle.soul_escape")), SoundSource.BLOCKS, 1, (float) Math.random());
					} else {
						_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("particle.soul_escape")), SoundSource.BLOCKS, 1, (float) Math.random(), false);
					}
				}
				if (Math.random() < 0.5) {
					world.addParticle(ParticleTypes.SOUL, (x + 0.5), (y + 0.9), (z + 0.5), (Mth.nextDouble(RandomSource.create(), -0.1, 0.1)), (Mth.nextDouble(RandomSource.create(), 0.3, 0.45)), (Mth.nextDouble(RandomSource.create(), -0.1, 0.1)));
				}
				if (Math.random() < 0.5) {
					world.addParticle(ParticleTypes.SOUL, (x + 0.5), (y + 0.9), (z + 0.5), (Mth.nextDouble(RandomSource.create(), -0.1, 0.1)), (Mth.nextDouble(RandomSource.create(), 0.3, 0.45)), (Mth.nextDouble(RandomSource.create(), -0.1, 0.1)));
				}
				if (Math.random() < 0.8) {
					world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, (x + 0.5), (y + 0.9), (z + 0.5), (Mth.nextDouble(RandomSource.create(), -0.1, 0.1)), (Mth.nextDouble(RandomSource.create(), 0.3, 0.45)),
							(Mth.nextDouble(RandomSource.create(), -0.1, 0.1)));
				}
			}
		}
	}

	@Override
public void neighborChanged(BlockState blockstate, Level world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
    super.neighborChanged(blockstate, world, pos, neighborBlock, neighborPos, isMoving);

    // Check if lava is above the block
    BlockPos blockAbove = pos.above();
    if (world.getBlockState(blockAbove).getBlock() == Blocks.LAVA) {
        // Replace the current block with blackstone
        world.setBlock(pos, Blocks.BLACKSTONE.defaultBlockState(), 3);
        
        // Play sound at the block position
        world.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.7F, 1.0F);
    }
}

}
