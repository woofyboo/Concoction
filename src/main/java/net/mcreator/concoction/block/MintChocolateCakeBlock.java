package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class MintChocolateCakeBlock extends ConcoctionCakeBlock {
	public MintChocolateCakeBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.WOOL).strength(0.5f).noOcclusion().pushReaction(PushReaction.DESTROY)
				.isRedstoneConductor((bs, br, bp) -> false), 2, 0.4F);
	}

	@Override
	public ItemStack getConsumedSliceStack() {
		return new ItemStack(ConcoctionModItems.MINT_CHOCOLATE_CAKE_SLICE.get());
	}
}
