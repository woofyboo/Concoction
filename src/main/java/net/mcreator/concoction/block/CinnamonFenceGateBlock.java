package net.mcreator.concoction.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class CinnamonFenceGateBlock extends FenceGateBlock {

    public CinnamonFenceGateBlock() {
        // WoodType.OAK тут чисто для того, чтобы поведение/звук были как у дубовой калитки.
        // Если потом заведёшь свой WoodType для корицы — можно поменять.
        super(WoodType.OAK, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE));
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        // как у обычных деревянных блоков: 20
        return 20;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        // как у обычных деревянных блоков: 5
        return 5;
    }
}
