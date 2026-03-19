package net.mcreator.concoction.block.entity;

import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class WarpedKitchenCabinetBlockEntity extends AbstractKitchenCabinetBlockEntity {
	public WarpedKitchenCabinetBlockEntity(BlockPos position, BlockState state) {
		super(ConcoctionModBlockEntities.WARPED_KITCHEN_CABINET.get(), position, state, "warped_kitchen_cabinet", "Warped Kitchen Cabinet");
	}
}
