package net.mcreator.concoction.item.food.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public record FoodEffectComponent(String type, int level, int duration) {
    static FoodEffectType enumType;
    public FoodEffectComponent {
        this.enumType = FoodEffectType.getByName(type);
    }

    public @NotNull FoodEffectType getEnumType() {
        return enumType;
    }

    public static final Codec<FoodEffectComponent> FOOD_EFFECT_COMPONENT_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("type").forGetter(FoodEffectComponent::type),
                    Codec.INT.fieldOf("level").forGetter(FoodEffectComponent::level),
                    Codec.INT.fieldOf("duration").forGetter(FoodEffectComponent::duration)
            ).apply(instance, FoodEffectComponent::new)
    );
    public static final StreamCodec<ByteBuf, FoodEffectComponent> FOOD_EFFECT_COMPONENT_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, FoodEffectComponent::type,
            ByteBufCodecs.INT, FoodEffectComponent::level,
            ByteBufCodecs.INT, FoodEffectComponent::duration,
            FoodEffectComponent::new
    );


//    // Unit stream codec if nothing should be sent across the network
//    public static final StreamCodec<ByteBuf, FoodEffectComponent> FOOD_EFFECT_COMPONENT_UNIT_STREAM_CODEC = StreamCodec.unit(new FoodEffectComponent(null, 0, 0));
//
//    /// Component will not be saved to disk
//    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FoodEffectComponent>> FOOD_EFFECT_COMPONENT_TRANSIENT = REGISTRY.registerComponentType(
//            "transient",
//            builder -> builder.networkSynchronized(FOOD_EFFECT_COMPONENT_CODEC)
//    );
//
//    // No data will be synced across the network
//    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FoodEffectComponent>> FOOD_EFFECT_COMPONENT_EXAMPLE = REGISTRY.registerComponentType(
//            "no_network",
//            builder -> builder
//                    .persistent(BASIC_CODEC)
//                    // Note we use a unit stream codec here
//                    .networkSynchronized(FOOD_EFFECT_COMPONENT_UNIT_STREAM_CODEC)
//    );
}
