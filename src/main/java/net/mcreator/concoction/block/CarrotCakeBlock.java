package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class CarrotCakeBlock extends ConcoctionCakeBlock {
	public CarrotCakeBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.WOOL).strength(0.5f).noOcclusion().pushReaction(PushReaction.DESTROY)
				.isRedstoneConductor((bs, br, bp) -> false), 4, 1.2F);
	}

	@Override
	public ItemStack getConsumedSliceStack() {
		return new ItemStack(ConcoctionModItems.CARROT_CAKE_SLICE.get());
	}
}
