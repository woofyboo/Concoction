package net.mcreator.concoction.item.food.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record FoodEffectComponent(FoodEffectType type, int level, int duration, boolean isHidden) {

    public static final Codec<FoodEffectComponent> FOOD_EFFECT_COMPONENT_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("type").xmap(FoodEffectType::getByName, FoodEffectType::getSerializedName).forGetter(FoodEffectComponent::type),
                    Codec.INT.fieldOf("level").forGetter(FoodEffectComponent::level),
                    Codec.INT.fieldOf("duration").forGetter(FoodEffectComponent::duration),
                    Codec.BOOL.fieldOf("isHidden").forGetter(FoodEffectComponent::isHidden)
            ).apply(instance, FoodEffectComponent::new)
    );
    public static final StreamCodec<ByteBuf, FoodEffectComponent> FOOD_EFFECT_COMPONENT_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(FoodEffectType::getByName, FoodEffectType::getSerializedName), FoodEffectComponent::type,
            ByteBufCodecs.INT, FoodEffectComponent::level,
            ByteBufCodecs.INT, FoodEffectComponent::duration,
            ByteBufCodecs.BOOL, FoodEffectComponent::isHidden,
            FoodEffectComponent::new
    );



//    // Unit stream codec if nothing should be sent across the network
//    public static final StreamCodec<ByteBuf, FoodEffectComponents> FOOD_EFFECT_COMPONENT_UNIT_STREAM_CODEC = StreamCodec.unit(new FoodEffectComponents(null, 0, 0));
//
//    /// Component will not be saved to disk
//    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FoodEffectComponents>> FOOD_EFFECT_COMPONENT_TRANSIENT = REGISTRY.registerComponentType(
//            "transient",
//            builder -> builder.networkSynchronized(FOOD_EFFECT_COMPONENT_CODEC)
//    );
//
//    // No data will be synced across the network
//    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FoodEffectComponents>> FOOD_EFFECT_COMPONENT_EXAMPLE = REGISTRY.registerComponentType(
//            "no_network",
//            builder -> builder
//                    .persistent(BASIC_CODEC)
//                    // Note we use a unit stream codec here
//                    .networkSynchronized(FOOD_EFFECT_COMPONENT_UNIT_STREAM_CODEC)
//    );
}
