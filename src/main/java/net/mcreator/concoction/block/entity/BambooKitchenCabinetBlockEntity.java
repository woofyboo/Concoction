package net.mcreator.concoction.block.entity;

import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class BambooKitchenCabinetBlockEntity extends AbstractKitchenCabinetBlockEntity {
	public BambooKitchenCabinetBlockEntity(BlockPos position, BlockState state) {
		super(ConcoctionModBlockEntities.BAMBOO_KITCHEN_CABINET.get(), position, state, "bamboo_kitchen_cabinet", "Bamboo Kitchen Cabinet");
	}
}
