package net.mcreator.concoction.block;

import net.mcreator.concoction.block.entity.DarkOakKitchenCabinetBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class DarkOakKitchenCabinetBlock extends AbstractKitchenCabinetBlock {
	public DarkOakKitchenCabinetBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOD).strength(2.5f).pushReaction(PushReaction.BLOCK));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DarkOakKitchenCabinetBlockEntity(pos, state);
	}
}
