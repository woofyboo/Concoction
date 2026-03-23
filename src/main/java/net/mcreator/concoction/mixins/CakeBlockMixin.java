package net.mcreator.concoction.mixins;

import net.mcreator.concoction.block.ConcoctionCakeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
		if (!(state.getBlock() instanceof ConcoctionCakeBlock cakeBlock)) {
			return;
		}

		if (!(level instanceof Level actualLevel) || actualLevel.isClientSide()) {
			return;
		}

		if (!cir.getReturnValue().consumesAction()) {
			return;
		}

		boolean lastSlice = state.getValue(CakeBlock.BITES) >= 6;
		cakeBlock.onSliceEaten(actualLevel, pos, state, player);

		if (lastSlice) {
			cakeBlock.onCakeFinished(actualLevel, pos, player);
		}
	}
}
