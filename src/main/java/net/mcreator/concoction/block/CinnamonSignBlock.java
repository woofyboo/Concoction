package net.mcreator.concoction.block;

import net.mcreator.concoction.block.entity.CinnamonSignBlockEntity;
import net.mcreator.concoction.init.ConcoctionWoodTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class CinnamonSignBlock extends StandingSignBlock {

    public CinnamonSignBlock() {
        super(
                ConcoctionWoodTypes.CINNAMON,
                BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)
        );
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CinnamonSignBlockEntity(pos, state);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return 20;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return 5;
    }
}
