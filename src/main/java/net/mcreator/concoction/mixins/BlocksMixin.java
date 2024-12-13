package net.mcreator.concoction.mixins;

import net.mcreator.concoction.block.CookingCauldron;
import net.minecraft.core.cauldron.CauldronInteraction;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin({Blocks.class})
public abstract class BlocksMixin {
    @Redirect(slice = @Slice(from = @At(value = "CONSTANT", args = {"stringValue=water_cauldron"}, ordinal = 0)),
            at = @At(value = "NEW",
                    target = "(Lnet/minecraft/world/level/biome/Biome$Precipitation;Lnet/minecraft/core/cauldron/CauldronInteraction$InteractionMap;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/LayeredCauldronBlock;",
                    ordinal = 0), method = {"<clinit>"})
    private static LayeredCauldronBlock createCauldron(Biome.Precipitation precipitation, CauldronInteraction.InteractionMap iterMap, @NotNull BlockBehaviour.Properties properties) {
        return new CookingCauldron(precipitation, iterMap, properties);
    }
}