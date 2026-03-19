package net.mcreator.concoction.block.entity;

import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class JungleKitchenCabinetBlockEntity extends AbstractKitchenCabinetBlockEntity {
	public JungleKitchenCabinetBlockEntity(BlockPos position, BlockState state) {
		super(ConcoctionModBlockEntities.JUNGLE_KITCHEN_CABINET.get(), position, state, "jungle_kitchen_cabinet", "Jungle Kitchen Cabinet");
	}
}
