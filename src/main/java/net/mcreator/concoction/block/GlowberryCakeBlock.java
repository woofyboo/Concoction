package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class GlowberryCakeBlock extends ConcoctionCakeBlock {
	public GlowberryCakeBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).sound(SoundType.SLIME_BLOCK).strength(0.5f).lightLevel(s -> 3)
				.hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true).noOcclusion().pushReaction(PushReaction.DESTROY)
				.isRedstoneConductor((bs, br, bp) -> false), 2, 0.4F);
	}

	@Override
	public ItemStack getConsumedSliceStack() {
		return new ItemStack(ConcoctionModItems.GLOWBERRY_CAKE_SLICE.get());
	}
}
