
package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModParticleTypes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;

public class PillowBlockBlock extends SlimeBlock {
	public PillowBlockBlock() {
		super(BlockBehaviour.Properties.of().ignitedByLava().mapColor(MapColor.WOOL).sound(SoundType.WOOL).strength(0.2f, 1f));
	}

	@Override
	public void updateEntityAfterFallOn(BlockGetter block, Entity player) {
		if (player.isSuppressingBounce()) {
			super.updateEntityAfterFallOn(block, player);
		} else {
			this.bounceUp(player);
		}
		summonLeafParticles(player);
	}

	@Override
  	public void fallOn(Level p_154567_, BlockState p_154568_, BlockPos p_154569_, Entity player, float damage) {
		if (player.isSuppressingBounce()) {
			super.fallOn(p_154567_, p_154568_, p_154569_, player, damage);
			if (player instanceof ServerPlayer _player) {
				AdvancementHolder _advTask = _player.server.getAdvancements().get(ResourceLocation.parse("concoction:mount_ever_rest"));
				if (_advTask != null) {
					AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_advTask);
					if (!_ap.isDone())  _player.getAdvancements().award(_advTask, _ap.getRemainingCriteria().iterator().next());
				}
				AdvancementHolder _advChallenge = _player.server.getAdvancements().get(ResourceLocation.parse("concoction:get_folded"));
				if (_advChallenge != null) {
					if (_player.getHealth() > 0f && _player.getHealth() < 1.5f) {
						AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_advChallenge);
						if (!_ap.isDone())
							_player.getAdvancements().award(_advChallenge, _ap.getRemainingCriteria().iterator().next());
					}
				}
			}
		} else {
			player.causeFallDamage(damage, 0.0F, p_154567_.damageSources().fall());
		}
	}

	private void summonLeafParticles(Entity player) {
		if (player.level() instanceof ServerLevel _level) {
			Vec3 pos = player.position();
			int particlesCount = calcAmpl(player.getDeltaMovement());
			if (particlesCount != 0)
			_level.sendParticles(ConcoctionModParticleTypes.FEATHER_PARTICLE.get(),
					pos.x, pos.y, pos.z,
					particlesCount, 0, 0.1, 0, 0.4);
		}
	}
	private int calcAmpl(Vec3 ampl) {
		if (ampl.y < 0.3d)
			return 0;
		else return (int) (Math.min((float) ampl.y, 2f) * 50);
	}

	private void bounceUp(Entity player) {
		Vec3 vec3 = player.getDeltaMovement();
		if (vec3.y < 0.0) {
			double d0 = player instanceof LivingEntity ? 1.0 : 0.8;
			player.setDeltaMovement(vec3.x, -vec3.y * 0.66F * d0, vec3.z);
		}
	}

	@Override
	public void stepOn(Level p_154573_, BlockPos p_154574_, BlockState p_154575_, Entity p_154576_) {
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 125;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 50;
	}
}
