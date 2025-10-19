package net.mcreator.concoction.procedures;

import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.component.DataComponents;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.minecraft.core.component.DataComponentType;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.primitives.Floats.min;
import static net.minecraft.util.Mth.ceil;

@EventBusSubscriber
public class SweetnessWorkProcedure {

    // Запоминаем реальный уровень голода ДО начала поедания
    private static final ConcurrentHashMap<UUID, Integer> PRE_FOOD = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        LivingEntity e = event.getEntity();
        if (!(e instanceof Player p)) return;

        ItemStack stack = event.getItem();
        if (!stack.has(DataComponents.FOOD)) return; // не еда — не интересуемся
        if (!p.hasEffect(ConcoctionModMobEffects.SWEETNESS)) return; // эффекта нет — правки не нужны

        PRE_FOOD.put(p.getUUID(), p.getFoodData().getFoodLevel());
    }

    @SubscribeEvent
    public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() != null) {
            execute(event, event.getEntity(), event.getItem());
        }
    }

    public static void execute(Entity entity, ItemStack itemstack) {
        execute(null, entity, itemstack);
    }

    private static void execute(@Nullable Event event, Entity entity, ItemStack itemstack) {
        if (!(entity instanceof Player _player)) return;
        if (!_player.hasEffect(ConcoctionModMobEffects.SWEETNESS)) return;
        if (!itemstack.has(DataComponents.FOOD)) return;

        // Считываем "уровень голода до" из буфера; если по какой-то причине его нет — используем текущее значение
        Integer beforeBoxed = PRE_FOOD.remove(_player.getUUID());
        int before = (beforeBoxed != null ? beforeBoxed : _player.getFoodData().getFoodLevel());

        int afterVanilla = _player.getFoodData().getFoodLevel();
        int originalHunger = itemstack.get(DataComponents.FOOD).nutrition();

        boolean isSweetFood =
            isSweetFlavor(itemstack, ConcoctionModDataComponents.FOOD_EFFECT.value()) ||
            isSweetFlavor(itemstack, ConcoctionModDataComponents.FOOD_EFFECT_2.value()) ||
            isSweetFlavor(itemstack, ConcoctionModDataComponents.FOOD_EFFECT_3.value()) ||
            isSweetFlavor(itemstack, ConcoctionModDataComponents.FOOD_EFFECT_4.value()) ||
            isSweetFlavor(itemstack, ConcoctionModDataComponents.FOOD_EFFECT_5.value());

        int target; // финальный уровень голода, который выставим

        if (isSweetFood) {
            int effectLevel = _player.getEffect(ConcoctionModMobEffects.SWEETNESS).getAmplifier();
            float bonusPercent = min(0.25f + 0.15f * effectLevel, 1f); // cap 100%
            int hungerMissingBefore = 20 - before;
            int bonusHunger = ceil(hungerMissingBefore * bonusPercent);

            // Сладкая еда: базовое восстановление + бонус, считаем ОТ "before"
            target = Math.min(20, before + originalHunger + bonusHunger);
        } else {
            // Несладкая: половинное восстановление, НО не опускаем ниже "before"
            int reducedHunger = ceil(originalHunger * 0.5f);
            target = Math.min(20, before + reducedHunger);
        }

        // Подстраховка: никогда не опускать ниже "до"
        target = Math.max(target, before);

        if (target != afterVanilla) {
            _player.getFoodData().setFoodLevel(target);
        }
    }

    private static boolean isSweetFlavor(ItemStack stack, DataComponentType<FoodEffectComponent> type) {
        if (!stack.has(type)) return false;
        FoodEffectComponent comp = stack.get(type);
        return comp != null && comp.type() == FoodEffectType.SWEET;
    }
}
