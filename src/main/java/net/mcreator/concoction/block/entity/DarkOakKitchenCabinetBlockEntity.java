package net.mcreator.concoction.block.entity;

import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DarkOakKitchenCabinetBlockEntity extends AbstractKitchenCabinetBlockEntity {
	public DarkOakKitchenCabinetBlockEntity(BlockPos position, BlockState state) {
		super(ConcoctionModBlockEntities.DARK_OAK_KITCHEN_CABINET.get(), position, state, "dark_oak_kitchen_cabinet", "Dark Oak Kitchen Cabinet");
	}
}
