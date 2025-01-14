package net.mcreator.concoction.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(net.minecraft.client.gui.Gui.class)
public class GuiMixin {
//    private static final ResourceLocation FOOD_EMPTY_HUNGER_SPRITE = ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "textures/gui/hud/photosynthesis_food_empty_hunger");
//    private static final ResourceLocation FOOD_HALF_HUNGER_SPRITE = ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "textures/gui/hud/photosynthesis_food_half_hunger");
//    private static final ResourceLocation FOOD_FULL_HUNGER_SPRITE = ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "textures/gui/hud/photosynthesis_food_full_hunger");

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void renderFood(GuiGraphics p_335615_, Player player, int p_335399_, int p_335589_, CallbackInfo ci) {
        if (player.hasEffect(ConcoctionModMobEffects.PHOTOSYNTHESIS)) {
            int dayTime = Math.floorMod(player.level().dayTime(), 24000);
            if (((dayTime >= 0 && dayTime < 13000) || (dayTime >= 23000 && dayTime < 24000)) &&
                    player.level().canSeeSky(player.blockPosition().above())) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
    private void renderHearts(GuiGraphics p_282497_, Player player, int p_168691_, int p_168692_, int p_168693_, int p_168694_, float p_168695_, int p_168696_, int p_168697_, int p_168698_, boolean p_168699_, CallbackInfo ci) {
        if (player.hasEffect(ConcoctionModMobEffects.SPICY)) {
            ci.cancel();
        }
    }

//    }    @Inject(method = "renderFood", at = @At(
//            value = "INVOKE",
//            ordinal = 0,
//            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"))
//    private void renderFood(GuiGraphics p_335615_, Player player, int p_335399_, int p_335589_, CallbackInfo ci,
//                            @Local(ordinal = 0) LocalRef<ResourceLocation> resLoc,
//                            @Local(ordinal = 1) LocalRef<ResourceLocation> resLoc1,
//                            @Local(ordinal = 2) LocalRef<ResourceLocation> resLoc2) {
//        if (player.hasEffect(ConcoctionModMobEffects.PHOTOSYNTHESIS)) {
//            int dayTime = Math.floorMod(player.level().dayTime(), 24000);
//            if (((dayTime >= 0 && dayTime < 13000) || (dayTime >= 23000 && dayTime < 24000)) &&
//                    player.level().canSeeSky(player.blockPosition().above())) {
////                ConcoctionMod.LOGGER.debug("Photosynthesis effect active");
//                resLoc.set(ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID,"textures/gui/hud/photosynthesis_food_empty.png"));
//                resLoc1.set(ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "gui/hud/photosynthesis_food_half"));
//                resLoc2.set(ResourceLocation.fromNamespaceAndPath(ConcoctionMod.MODID, "textures/gui/hud/photosynthesis_food_full.png"));
//
////                resLoc.set(FOOD_EMPTY_HUNGER_SPRITE);
////                resLoc1.set(FOOD_HALF_HUNGER_SPRITE);
////                resLoc2.set(FOOD_FULL_HUNGER_SPRITE);
//            }
//        }
//    }
}
