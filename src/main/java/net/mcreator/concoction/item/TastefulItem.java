package net.mcreator.concoction.item;

import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.*;

public class TastefulItem extends Item {
    public TastefulItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack,
                                @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip,
                                @NotNull TooltipFlag flag) {

        // === Зажат ли CTRL? ===
        boolean ctrlDown = Screen.hasControlDown();

        // Если Ctrl НЕ зажат — подсказываем
        if (!ctrlDown) {
            tooltip.add(
                    Component.translatable(
                            "tooltip.concoction.hold_key",
                            Component.literal("Ctrl")   // ← всегда пишем CTRL
                    ).withStyle(ChatFormatting.DARK_GRAY)
            );
            return;
        }

        // === Ctrl зажат → показываем детали ===

        List<Component> tastes = collectTastes(stack);

        if (!tastes.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.concoction.tastes_header")
                    .withStyle(ChatFormatting.DARK_GREEN));

            tooltip.addAll(tastes);

            tooltip.add(Component.empty());
        }

        // Описание предмета
        tooltip.add(
                Component.translatable(getDescriptionKeyForThisItem())
                        .withStyle(ChatFormatting.GRAY)
        );
    }

    /** Собирает все вкусы предмета в список */
    private List<Component> collectTastes(ItemStack stack) {
        List<Component> tastes = new ArrayList<>();
        addTasteIfPresent(stack, tastes, FOOD_EFFECT.value());
        addTasteIfPresent(stack, tastes, FOOD_EFFECT_2.value());
        addTasteIfPresent(stack, tastes, FOOD_EFFECT_3.value());
        addTasteIfPresent(stack, tastes, FOOD_EFFECT_4.value());
        addTasteIfPresent(stack, tastes, FOOD_EFFECT_5.value());
        return tastes;
    }

    private void addTasteIfPresent(ItemStack stack, List<Component> list,
                                   DataComponentType<FoodEffectComponent> type) {
        FoodEffectComponent comp = stack.get(type);
        if (comp != null) {
            list.add(comp.type().getTooltip(comp.level(), comp.duration(), comp.isHidden()));
        }
    }

    private String getDescriptionKeyForThisItem() {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(this);
        String ns = id == null ? "minecraft" : id.getNamespace();
        String path = id == null ? "unknown" : id.getPath();
        return "description.concoction." + ns + "." + path;
    }
}
