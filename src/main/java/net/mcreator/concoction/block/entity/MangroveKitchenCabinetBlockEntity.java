package net.mcreator.concoction.block.entity;

import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MangroveKitchenCabinetBlockEntity extends AbstractKitchenCabinetBlockEntity {
	public MangroveKitchenCabinetBlockEntity(BlockPos position, BlockState state) {
		super(ConcoctionModBlockEntities.MANGROVE_KITCHEN_CABINET.get(), position, state, "mangrove_kitchen_cabinet", "Mangrove Kitchen Cabinet");
	}
}
