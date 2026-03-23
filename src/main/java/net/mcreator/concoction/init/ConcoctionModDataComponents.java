package net.mcreator.concoction.init;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.mcreator.concoction.item.food.types.FoodEffectComponent.*;
import static net.mcreator.concoction.item.food.types.FoodEffectType.*;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ConcoctionModDataComponents {

    public static final DeferredRegister.DataComponents REGISTRY = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ConcoctionMod.MODID);

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

    @SubscribeEvent
    public static void modifyComponents(ModifyDefaultComponentsEvent event) {
    }
}
