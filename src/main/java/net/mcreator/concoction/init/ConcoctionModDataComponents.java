package net.mcreator.concoction.init;

import net.mcreator.concoction.ConcoctionMod;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.mcreator.concoction.item.food.types.FoodEffectComponent.*;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ConcoctionModDataComponents {

    // In another class
    // The specialized DeferredRegister.DataComponents simplifies data component registration and avoids some generic inference issues with the `DataComponentType.Builder` within a `Supplier`
    public static final DeferredRegister.DataComponents REGISTRY = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ConcoctionMod.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FoodEffectComponent>> FOOD_EFFECT = REGISTRY.registerComponentType(
            "food_effect",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(FOOD_EFFECT_COMPONENT_CODEC)
                    // The codec to read/write the data across the network
                    .networkSynchronized(FOOD_EFFECT_COMPONENT_STREAM_CODEC)
    );

    // Listened to on the mod event bus
    @SubscribeEvent
    public static void modifyComponents(ModifyDefaultComponentsEvent event) {
        // Sets the component on melon seeds
        event.modify(Items.APPLE, builder ->
                builder.set(FOOD_EFFECT.value(), new FoodEffectComponent("sweet", 1, 30))
        );

        // Removes the component for any items that have a crafting item
        event.modifyMatching(
                Item::hasCraftingRemainingItem,
                builder -> builder.remove(FOOD_EFFECT.value())
        );
    }
}
