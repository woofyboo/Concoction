package net.mcreator.concoction.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class CinnamonPlanksBlock extends Block {

    public CinnamonPlanksBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)                  // как обычные доски
                .strength(2.0F, 3.0F)                    // твёрдость/взрывоустойчивость как у planks
                .sound(SoundType.WOOD)                   // звук дерева
                .ignitedByLava()                         // может загореться от лавы
                .instrument(NoteBlockInstrument.BASS)    // нотный блок ведёт себя как с досками
        );
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        // как обычные доски (20)
        return 20;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        // как обычные доски (5)
        return 5;
    }
}
