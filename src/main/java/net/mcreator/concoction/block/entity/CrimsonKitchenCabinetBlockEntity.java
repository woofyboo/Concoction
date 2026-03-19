package net.mcreator.concoction.block.entity;

import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CrimsonKitchenCabinetBlockEntity extends AbstractKitchenCabinetBlockEntity {
	public CrimsonKitchenCabinetBlockEntity(BlockPos position, BlockState state) {
		super(ConcoctionModBlockEntities.CRIMSON_KITCHEN_CABINET.get(), position, state, "crimson_kitchen_cabinet", "Crimson Kitchen Cabinet");
	}
}
