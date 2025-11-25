package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

public class CinnamonBarkLogBlock extends RotatedPillarBlock {

    public CinnamonBarkLogBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .strength(2.0F)
                .sound(SoundType.WOOD)
                .ignitedByLava()
        );
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state,
                                                     UseOnContext context,
                                                     ItemAbility ability,
                                                     boolean simulate) {
        if (!(context.getItemInHand().getItem() instanceof AxeItem)) {
            return super.getToolModifiedState(state, context, ability, simulate);
        }

        // стадия 2: bark → полностью обтёсанный stripped
        BlockState stripped = ConcoctionModBlocks.STRIPPED_CINNAMON_LOG.get().defaultBlockState()
                .setValue(AXIS, state.getValue(AXIS));

        return stripped;
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }
}
