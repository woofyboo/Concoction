package net.mcreator.concoction.block.entity;

import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AcaciaKitchenCabinetBlockEntity extends AbstractKitchenCabinetBlockEntity {
	public AcaciaKitchenCabinetBlockEntity(BlockPos position, BlockState state) {
		super(ConcoctionModBlockEntities.ACACIA_KITCHEN_CABINET.get(), position, state, "acacia_kitchen_cabinet", "Acacia Kitchen Cabinet");
	}
}
