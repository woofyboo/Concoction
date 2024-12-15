package net.mcreator.concoction.mixins;

import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({net.minecraft.world.level.block.CakeBlock.class})
public class CakeBlockMixin {
    @Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/ResourceLocation;)V"))
    private static void addFoodEffect(LevelAccessor p_51186_, BlockPos p_51187_, BlockState p_51188_, Player player, CallbackInfoReturnable<InteractionResult> cir) {
        player.addEffect(FoodEffectType.getEffect(FoodEffectType.SWEET, 1, 30, true));
    }
}
