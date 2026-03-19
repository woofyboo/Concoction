package net.mcreator.concoction.block;

import net.mcreator.concoction.block.entity.BambooKitchenCabinetBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class BambooKitchenCabinetBlock extends AbstractKitchenCabinetBlock {
	public BambooKitchenCabinetBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.BAMBOO_WOOD).strength(2.5f).pushReaction(PushReaction.BLOCK));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BambooKitchenCabinetBlockEntity(pos, state);
	}

	@Override
	public SoundEvent getOpenSound() {
		return SoundEvents.BAMBOO_WOOD_TRAPDOOR_OPEN;
	}

	@Override
	public SoundEvent getCloseSound() {
		return SoundEvents.BAMBOO_WOOD_TRAPDOOR_CLOSE;
	}
}
