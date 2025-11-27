package net.mcreator.concoction.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class CinnamonStairsBlock extends StairBlock {

    public CinnamonStairsBlock() {
        // Копируем свойства с ванильных деревянных ступенек
        super(
                Blocks.OAK_PLANKS.defaultBlockState(),          // базовый блок для поведения
                BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS) // все свойства как у oak_stairs
        );
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
