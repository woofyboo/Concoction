package net.mcreator.concoction.item.food.passive;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;

public record FoodPassiveEffectComponent(FoodPassiveEffectType type) {
    public static final Codec<FoodPassiveEffectComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("type")
                            .xmap(FoodPassiveEffectType::getByName, FoodPassiveEffectType::getSerializedName)
                            .forGetter(FoodPassiveEffectComponent::type)
            ).apply(instance, FoodPassiveEffectComponent::new)
    );

    public static final StreamCodec<ByteBuf, FoodPassiveEffectComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(FoodPassiveEffectType::getByName, FoodPassiveEffectType::getSerializedName),
            FoodPassiveEffectComponent::type,
            FoodPassiveEffectComponent::new
    );

    public static FoodPassiveEffectComponent of(FoodPassiveEffectType type) {
        return new FoodPassiveEffectComponent(type);
    }

    public void applyOnConsume(LivingEntity entity) {
        this.type.applyOnConsume(entity);
    }
}
