package net.mcreator.concoction.block.entity;

import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CinnamonKitchenCabinetBlockEntity extends AbstractKitchenCabinetBlockEntity {
	public CinnamonKitchenCabinetBlockEntity(BlockPos position, BlockState state) {
		super(ConcoctionModBlockEntities.CINNAMON_KITCHEN_CABINET.get(), position, state, "cinnamon_kitchen_cabinet", "Cinnamon Kitchen Cabinet");
	}
}
