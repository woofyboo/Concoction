package net.mcreator.concoction.block.entity;

import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class OakKitchenCabinetBlockEntity extends AbstractKitchenCabinetBlockEntity {
	public OakKitchenCabinetBlockEntity(BlockPos position, BlockState state) {
		super(ConcoctionModBlockEntities.OAK_KITCHEN_CABINET.get(), position, state, "oak_kitchen_cabinet", "Oak Kitchen Cabinet");
	}
}
