package net.mcreator.concoction.block;

import net.mcreator.concoction.block.entity.SpruceKitchenCabinetBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class SpruceKitchenCabinetBlock extends AbstractKitchenCabinetBlock {
	public SpruceKitchenCabinetBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOD).strength(2.5f).pushReaction(PushReaction.BLOCK));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SpruceKitchenCabinetBlockEntity(pos, state);
	}
}
