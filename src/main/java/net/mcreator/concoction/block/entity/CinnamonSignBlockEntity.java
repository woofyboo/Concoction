package net.mcreator.concoction.block.entity;

import net.mcreator.concoction.init.ConcoctionModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CinnamonSignBlockEntity extends SignBlockEntity {

    public CinnamonSignBlockEntity(BlockPos pos, BlockState state) {
        super(ConcoctionModBlockEntities.CINNAMON_SIGN.get(), pos, state);
    }
}
