package net.mcreator.concoction.block.entity;

import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CherryKitchenCabinetBlockEntity extends AbstractKitchenCabinetBlockEntity {
	public CherryKitchenCabinetBlockEntity(BlockPos position, BlockState state) {
		super(ConcoctionModBlockEntities.CHERRY_KITCHEN_CABINET.get(), position, state, "cherry_kitchen_cabinet", "Cherry Kitchen Cabinet");
	}
}
