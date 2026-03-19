package net.mcreator.concoction.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class GlowberryCakeBlock extends ConcoctionCakeBlock {
	public GlowberryCakeBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.SLIME_BLOCK).strength(0.5f).lightLevel(s -> 3)
				.hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).noOcclusion().pushReaction(PushReaction.DESTROY)
				.isRedstoneConductor((bs, br, bp) -> false), 2, 0.4F);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockState belowState = level.getBlockState(pos.below());
		return belowState.isSolid() || belowState.getBlock() instanceof GlowberryCakeBlock || belowState.getBlock() instanceof CakeBlock;
	}

	@Override
	protected void onEaten(Level level, BlockPos pos, Player player) {
		player.addEffect(new MobEffectInstance(
				MobEffects.GLOWING,
				30 * 20,
				0,
				false,
				false,
				true,
				null
		));
	}
}
