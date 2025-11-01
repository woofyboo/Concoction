package net.mcreator.concoction.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record SaponariaPatchConfig(
        ResourceLocation block,  // <-- ID блока, например "concoction:saponaria"
        int radius,
        int spreadTries,
        int attemptsPerPos
) implements FeatureConfiguration {

    public static final Codec<SaponariaPatchConfig> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("block").forGetter(SaponariaPatchConfig::block),
            Codec.INT.fieldOf("radius").forGetter(SaponariaPatchConfig::radius),
            Codec.INT.fieldOf("spread_tries").forGetter(SaponariaPatchConfig::spreadTries),
            Codec.INT.fieldOf("attempts_per_pos").forGetter(SaponariaPatchConfig::attemptsPerPos)
    ).apply(inst, SaponariaPatchConfig::new));
}
