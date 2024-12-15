package net.mcreator.concoction.mixins;

import net.mcreator.concoction.init.ConcoctionModItems;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT;

@Mixin(net.minecraft.world.entity.player.Player.class)
public abstract class PlayerMixin {

    @Redirect(method = "eat", at = @At(value = "INVOKE",
                                    target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
    private void onEat(Level instance, Player player, double p_46544_, double p_46545_, double p_46546_, SoundEvent p_46547_, SoundSource p_46548_, float p_46549_, float p_46550_, Level pLevel, ItemStack itemStack, FoodProperties foodProperties) {
        if (itemStack.getItem().equals(ConcoctionModItems.MINT_BREW.get()) || itemStack.getItem().equals(ConcoctionModItems.OBSIDIAN_TEARS_BOTTLE.get())) {
            instance.playSound(player, p_46544_, p_46545_, p_46546_, Objects.requireNonNull(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("concoction:silence"))), p_46548_, p_46549_, p_46550_);
        } else {
            instance.playSound(player, p_46544_, p_46545_, p_46546_, p_46547_, p_46548_, p_46549_, p_46550_);
        }
    }
}
