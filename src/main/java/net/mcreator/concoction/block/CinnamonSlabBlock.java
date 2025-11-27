package net.mcreator.concoction.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class CinnamonSlabBlock extends SlabBlock {

    public CinnamonSlabBlock() {
        // Копируем свойства с ванильной деревянной плиты
        super(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB));
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
