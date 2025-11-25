package net.mcreator.concoction.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class CinnamonLeavesBlock extends LeavesBlock {

    public CinnamonLeavesBlock() {
        super(BlockBehaviour.Properties.of()
                // как у обычных лиственных
                .mapColor(MapColor.PLANT)
                .strength(0.2F)
                .randomTicks()
                .sound(SoundType.GRASS)
                .noOcclusion()
                // чтобы не душили / не блокировали обзор (как ванильные через лямбды)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false)
        );
    }

    // --- Горючесть и распространение огня (как у обычных листьев) ---

    @Override
    public boolean isFlammable(BlockState state,
                               BlockGetter level,
                               BlockPos pos,
                               Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state,
                               BlockGetter level,
                               BlockPos pos,
                               Direction direction) {
        // ванильные листья: довольно легко загораются
        return 60;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state,
                                  BlockGetter level,
                                  BlockPos pos,
                                  Direction direction) {
        // и быстро распространяют огонь
        return 30;
    }
}
