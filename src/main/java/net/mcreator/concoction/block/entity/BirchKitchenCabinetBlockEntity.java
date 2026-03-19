package net.mcreator.concoction.block.entity;

import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BirchKitchenCabinetBlockEntity extends AbstractKitchenCabinetBlockEntity {
	public BirchKitchenCabinetBlockEntity(BlockPos position, BlockState state) {
		super(ConcoctionModBlockEntities.BIRCH_KITCHEN_CABINET.get(), position, state, "birch_kitchen_cabinet", "Birch Kitchen Cabinet");
	}
}
