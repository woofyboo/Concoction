package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class CherryCakeBlock extends ConcoctionCakeBlock {
	public CherryCakeBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.WOOL).strength(0.5f).noOcclusion().pushReaction(PushReaction.DESTROY)
				.isRedstoneConductor((bs, br, bp) -> false), 3, 0.6F);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockState belowState = level.getBlockState(pos.below());
		return belowState.isSolid() || belowState.getBlock() instanceof CherryCakeBlock || belowState.getBlock() instanceof CakeBlock;
	}

	@Override
	protected void onEaten(Level level, BlockPos pos, Player player) {
		player.heal(2.0F);
		player.addEffect(new MobEffectInstance(
				ConcoctionModMobEffects.SWEETNESS,
				30 * 20,
				0,
				false,
				false,
				true,
				null
		));

		if (level instanceof ServerLevel serverLevel) {
			double px = player.getX();
			double py = player.getY() + player.getEyeHeight() / 2.0;
			double pz = player.getZ();

			for (int i = 0; i < 5; i++) {
				double offsetX = (player.getRandom().nextDouble() - 0.5) * 3.0;
				double offsetY = (player.getRandom().nextDouble() - 0.5) * 2.0;
				double offsetZ = (player.getRandom().nextDouble() - 0.5) * 3.0;
				serverLevel.sendParticles(ParticleTypes.HEART, px + offsetX, py + offsetY, pz + offsetZ, 1, 0.0, 0.05, 0.0, 0.0);
			}
		}
	}
}
