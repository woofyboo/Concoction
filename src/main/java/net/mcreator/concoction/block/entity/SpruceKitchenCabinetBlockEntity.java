package net.mcreator.concoction.block.entity;

import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SpruceKitchenCabinetBlockEntity extends AbstractKitchenCabinetBlockEntity {
	public SpruceKitchenCabinetBlockEntity(BlockPos position, BlockState state) {
		super(ConcoctionModBlockEntities.SPRUCE_KITCHEN_CABINET.get(), position, state, "spruce_kitchen_cabinet", "Spruce Kitchen Cabinet");
	}
}
