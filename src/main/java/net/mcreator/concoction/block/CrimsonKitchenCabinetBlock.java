package net.mcreator.concoction.block;

import net.mcreator.concoction.block.entity.CrimsonKitchenCabinetBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class CrimsonKitchenCabinetBlock extends AbstractKitchenCabinetBlock {
	public CrimsonKitchenCabinetBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOD).strength(2.5f).pushReaction(PushReaction.BLOCK));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CrimsonKitchenCabinetBlockEntity(pos, state);
	}

	@Override
	public SoundEvent getOpenSound() {
		return SoundEvents.NETHER_WOOD_TRAPDOOR_OPEN;
	}

	@Override
	public SoundEvent getCloseSound() {
		return SoundEvents.NETHER_WOOD_TRAPDOOR_CLOSE;
	}
}
