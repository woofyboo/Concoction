package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
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

        if (!simulate && !context.getLevel().isClientSide()) {
            var level = context.getLevel();
            BlockPos pos = context.getClickedPos();

            // достаём Holder<Enchantment> для Fortune
            var enchantmentRegistry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            var fortuneHolder = enchantmentRegistry.getOrThrow(Enchantments.FORTUNE);

            // реальный уровень Fortune на топоре
            int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(fortuneHolder, context.getItemInHand());

            // базовый шанс: 10% + 20% за уровень
            float extraChance = 0.10F + 0.20F * fortuneLevel;
            if (extraChance > 1.0F) {
                extraChance = 1.0F;
            }

            int count = 1; // гарантированно 1 кора
            var random = level.getRandom();
            if (random.nextFloat() < extraChance) {
                count++; // ещё +1 с шансом
            }

            if (count > 0) {
                Block.popResource(level, pos,
                        new ItemStack(ConcoctionModItems.CINNAMON_BARK.get(), count));
            }
        }

        // bark → stripped
        BlockState stripped = ConcoctionModBlocks.STRIPPED_CINNAMON_LOG.get()
                .defaultBlockState()
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
