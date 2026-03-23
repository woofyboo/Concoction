package net.mcreator.concoction.init;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.item.food.passive.FoodPassiveEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

import static net.mcreator.concoction.item.food.types.FoodEffectComponent.FOOD_EFFECT_COMPONENT_CODEC;
import static net.mcreator.concoction.item.food.types.FoodEffectComponent.FOOD_EFFECT_COMPONENT_STREAM_CODEC;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ConcoctionModDataComponents {

    public static final DeferredRegister.DataComponents REGISTRY = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ConcoctionMod.MODID);
    private static final Codec<List<FoodPassiveEffectComponent>> FOOD_PASSIVE_EFFECTS_CODEC =
            FoodPassiveEffectComponent.CODEC.listOf();
    private static final StreamCodec<ByteBuf, List<FoodPassiveEffectComponent>> FOOD_PASSIVE_EFFECTS_STREAM_CODEC =
            FoodPassiveEffectComponent.STREAM_CODEC.apply(ByteBufCodecs.list());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FoodEffectComponent>> FOOD_EFFECT = REGISTRY.registerComponentType(
            "food_effect",
            builder -> builder
                    .persistent(FOOD_EFFECT_COMPONENT_CODEC)
                    .networkSynchronized(FOOD_EFFECT_COMPONENT_STREAM_CODEC)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FoodEffectComponent>> FOOD_EFFECT_2 = REGISTRY.registerComponentType(
            "food_effect_2",
            builder -> builder
                    .persistent(FOOD_EFFECT_COMPONENT_CODEC)
                    .networkSynchronized(FOOD_EFFECT_COMPONENT_STREAM_CODEC)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FoodEffectComponent>> FOOD_EFFECT_3 = REGISTRY.registerComponentType(
            "food_effect_3",
            builder -> builder
                    .persistent(FOOD_EFFECT_COMPONENT_CODEC)
                    .networkSynchronized(FOOD_EFFECT_COMPONENT_STREAM_CODEC)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FoodEffectComponent>> FOOD_EFFECT_4 = REGISTRY.registerComponentType(
            "food_effect_4",
            builder -> builder
                    .persistent(FOOD_EFFECT_COMPONENT_CODEC)
                    .networkSynchronized(FOOD_EFFECT_COMPONENT_STREAM_CODEC)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FoodEffectComponent>> FOOD_EFFECT_5 = REGISTRY.registerComponentType(
            "food_effect_5",
            builder -> builder
                    .persistent(FOOD_EFFECT_COMPONENT_CODEC)
                    .networkSynchronized(FOOD_EFFECT_COMPONENT_STREAM_CODEC)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<FoodPassiveEffectComponent>>> FOOD_PASSIVE_EFFECTS = REGISTRY.registerComponentType(
            "food_passive_effects",
            builder -> builder
                    .persistent(FOOD_PASSIVE_EFFECTS_CODEC)
                    .networkSynchronized(FOOD_PASSIVE_EFFECTS_STREAM_CODEC)
    );

    @SubscribeEvent
    public static void modifyComponents(ModifyDefaultComponentsEvent event) {
    }
}
