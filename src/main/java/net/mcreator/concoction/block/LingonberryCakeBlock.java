package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.core.BlockPos;
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

public class LingonberryCakeBlock extends ConcoctionCakeBlock {
	public LingonberryCakeBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.WOOL).strength(0.5f).noOcclusion().pushReaction(PushReaction.DESTROY)
				.isRedstoneConductor((bs, br, bp) -> false), 2, 0.4F);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockState belowState = level.getBlockState(pos.below());
		return belowState.isSolid() || belowState.getBlock() instanceof LingonberryCakeBlock || belowState.getBlock() instanceof CakeBlock;
	}

	@Override
	protected void onEaten(Level level, BlockPos pos, Player player) {
		player.addEffect(new MobEffectInstance(
				ConcoctionModMobEffects.SWEETNESS,
				30 * 20,
				0,
				false,
				false,
				true,
				null
		));
		player.addEffect(new MobEffectInstance(
				ConcoctionModMobEffects.CREAMY,
				60 * 20,
				0,
				false,
				false,
				true,
				null
		));
	}
}
