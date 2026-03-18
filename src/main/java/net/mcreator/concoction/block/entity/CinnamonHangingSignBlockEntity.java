package net.mcreator.concoction.block.entity;

import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CinnamonHangingSignBlockEntity extends SignBlockEntity {

	public CinnamonHangingSignBlockEntity(BlockPos pos, BlockState state) {
		super(ConcoctionModBlockEntities.CINNAMON_HANGING_SIGN.get(), pos, state);
	}

	@Override
	public int getTextLineHeight() {
		return 9;
	}

	@Override
	public int getMaxTextLineWidth() {
		return 60;
	}

	@Override
	public SoundEvent getSignInteractionFailedSoundEvent() {
		return SoundEvents.WAXED_HANGING_SIGN_INTERACT_FAIL;
	}
}
