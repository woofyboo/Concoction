package net.mcreator.concoction.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public class StrippedCinnamonLogBlock extends RotatedPillarBlock {

    public StrippedCinnamonLogBlock() {
        super(BlockBehaviour.Properties.of()
                // цвет карты как у обычного дерева
                .mapColor(MapColor.WOOD)
                // прочность и взрывоустойчивость как у ванильных логов
                .strength(2.0F)              // (тут же и resistance задаётся)
                // деревянные звуки
                .sound(SoundType.WOOD)
                // ведёт себя как дерево с точки зрения лавы
                .ignitedByLava()
        );
    }

    // --- Горючесть / распространение огня ---

    @Override
    public boolean isFlammable(BlockState state,
                               BlockGetter level,
                               BlockPos pos,
                               Direction direction) {
        // как у обычного дерева — да, горит
        return true;
    }

    @Override
    public int getFlammability(BlockState state,
                               BlockGetter level,
                               BlockPos pos,
                               Direction direction) {
        // близко к ванильным логам (обычно 5)
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state,
                                  BlockGetter level,
                                  BlockPos pos,
                                  Direction direction) {
        // скорость распространения огня (тоже примерно как у дерева)
        return 5;
    }
}
