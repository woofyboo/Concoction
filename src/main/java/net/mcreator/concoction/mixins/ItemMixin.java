package net.mcreator.concoction.mixins;

import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.*;

@Mixin(Item.class)
public abstract class ItemMixin {

    @OnlyIn(Dist.CLIENT)
    private static boolean concoction$isCtrlDown() {
        // Всегда проверяем именно Ctrl, независимо от настроек управления
        return Screen.hasControlDown();
    }

    @OnlyIn(Dist.CLIENT)
    @Inject(method = "appendHoverText", at = @At("TAIL"))
    private void concoction$appendFoodTastesOrDescription(@NotNull ItemStack stack,
                                                          @NotNull Item.TooltipContext ctx,
                                                          @NotNull List<Component> tooltip,
                                                          @NotNull TooltipFlag flag,
                                                          @NotNull CallbackInfo ci) {
        // 1) Собираем вкусы
        FoodEffectComponent[] comps = new FoodEffectComponent[]{
                stack.get(FOOD_EFFECT.value()),
                stack.get(FOOD_EFFECT_2.value()),
                stack.get(FOOD_EFFECT_3.value()),
                stack.get(FOOD_EFFECT_4.value()),
                stack.get(FOOD_EFFECT_5.value())
        };
        boolean hasAnyTaste = false;
        for (FoodEffectComponent c : comps) {
            if (c != null) {
                hasAnyTaste = true;
                break;
            }
        }

        // 2) Ключ описания для любого предмета
        Item self = (Item) (Object) this;
        var id = BuiltInRegistries.ITEM.getKey(self);
        String descKey = (id != null) ? "description.concoction." + id.getNamespace() + "." + id.getPath() : null;
        boolean hasDesc = (descKey != null) && I18n.exists(descKey);

        if (hasAnyTaste) {
            // === ЕСТЬ ВКУСЫ ===
            if (!concoction$isCtrlDown()) {
                tooltip.add(Component.translatable(
                        "tooltip.concoction.hold_key",
                        // Жёстко пишем Ctrl, никаких keybind'ов
                        Component.literal("Ctrl")
                ).withStyle(ChatFormatting.DARK_GRAY));
                return;
            }
            // заголовок
            tooltip.add(Component.translatable("tooltip.concoction.tastes_header").withStyle(ChatFormatting.DARK_GREEN));
            // вкусы
            for (FoodEffectComponent c : comps) {
                if (c != null) {
                    tooltip.add(c.type().getTooltip(c.level(), c.duration(), c.isHidden()));
                }
            }
            // описание (если есть)
            if (hasDesc) {
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY));
            }
            return;
        }

        // === НЕТ ВКУСОВ ===
        if (!hasDesc) {
            // Нечего показывать — выходим, не спамим подсказкой.
            return;
        }

        // Строка описания ЕСТЬ:
        if (!concoction$isCtrlDown()) {
            // Показываем СОВЕТ про Ctrl (но без заголовков/пустых строк).
            tooltip.add(Component.translatable(
                    "tooltip.concoction.hold_key",
                    Component.literal("Ctrl")
            ).withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        // Зажали Ctrl → показываем ТОЛЬКО описание (без «Вкусы», без пустой строки).
        tooltip.add(Component.translatable(descKey).withStyle(ChatFormatting.GRAY));
    }
}
