package net.mcreator.concoction.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ConcoctionCakeBlock extends CakeBlock {
	private final int nutritionPerSlice;
	private final float saturationPerSlice;

	protected ConcoctionCakeBlock(BlockBehaviour.Properties properties, int nutritionPerSlice, float saturationPerSlice) {
		super(properties);
		this.nutritionPerSlice = nutritionPerSlice;
		this.saturationPerSlice = saturationPerSlice;
	}

	public int getNutritionPerSlice() {
		return nutritionPerSlice;
	}

	public float getSaturationPerSlice() {
		return saturationPerSlice;
	}

	public ItemStack getConsumedSliceStack() {
		return ItemStack.EMPTY;
	}

	public void onSliceEaten(Level level, BlockPos pos, BlockState previousState, Player player) {
	}

	public void onCakeFinished(Level level, BlockPos pos, Player player) {
	}
}
