package net.mcreator.concoction.mixins;

import net.mcreator.concoction.block.ConcoctionCakeBlock;
import net.mcreator.concoction.event.ConcoctionFoodEvents;
import net.mcreator.concoction.init.ConcoctionModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin {
	@Redirect(
			method = "eat",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"
			)
	)
	private static void concoction$useCustomSliceValues(FoodData foodData, int nutrition, float saturation, LevelAccessor level, BlockPos pos, BlockState state, Player player) {
		if (state.getBlock() instanceof ConcoctionCakeBlock cakeBlock) {
			foodData.eat(cakeBlock.getNutritionPerSlice(), cakeBlock.getSaturationPerSlice());
			return;
		}

		foodData.eat(nutrition, saturation);
	}

	@Inject(method = "eat", at = @At("RETURN"))
	private static void concoction$runCustomCakeHooks(LevelAccessor level, BlockPos pos, BlockState state, Player player, CallbackInfoReturnable<InteractionResult> cir) {
		if (!(state.getBlock() instanceof ConcoctionCakeBlock)) {
			return;
		}

		if (!(level instanceof Level actualLevel) || actualLevel.isClientSide()) {
			return;
		}

		if (!cir.getReturnValue().consumesAction()) {
			return;
		}

		ConcoctionFoodEvents.handleVirtualConsumedFood(player, concoction$getConsumedSliceStack(state));

		if (!(state.getBlock() instanceof ConcoctionCakeBlock cakeBlock)) {
			return;
		}

		boolean lastSlice = state.getValue(CakeBlock.BITES) >= 6;
		cakeBlock.onSliceEaten(actualLevel, pos, state, player);

		if (lastSlice) {
			cakeBlock.onCakeFinished(actualLevel, pos, player);
		}
	}

	private static ItemStack concoction$getConsumedSliceStack(BlockState state) {
		if (state.getBlock() instanceof ConcoctionCakeBlock cakeBlock) {
			return cakeBlock.getConsumedSliceStack();
		}

		if (state.is(Blocks.CAKE)) {
			return new ItemStack(ConcoctionModItems.CAKE_SLICE.get());
		}

		return ItemStack.EMPTY;
	}
}
