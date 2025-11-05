package net.mcreator.concoction.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT;
import static net.mcreator.concoction.init.ConcoctionModDataComponents.*;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;

public class MagmaEggItem extends Item {
    public MagmaEggItem() {
        super(new Item.Properties()
                .stacksTo(64)
                .component(FOOD_EFFECT.value(),  new FoodEffectComponent(FoodEffectType.FLAMING, 1, 18, true))
                .component(FOOD_EFFECT_2.value(), new FoodEffectComponent(FoodEffectType.BITTER,  2, 180, true))
                .component(FOOD_EFFECT_3.value(), new FoodEffectComponent(FoodEffectType.MINTY,   1, 90,  true))
                .rarity(Rarity.COMMON)
                .food(new FoodProperties.Builder()
                        .nutrition(2)
                        .saturationModifier(0.3f)
                        .build()));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        // Сначала применяем стандартное поведение еды
        ItemStack result = super.finishUsingItem(stack, level, entity);

        // Потом — "острый" урон 1.0, но без летального исхода
        if (!level.isClientSide) {
            // Если у существа больше 1 HP — нанесём 1 единицу урона
            if (entity.getHealth() > 1.0F) {
                // Выбери источник урона по вкусу: hotFloor() не поджигает и хорошо тематически подходит
                entity.hurt(level.damageSources().hotFloor(), 1.0F);
            }
            // Иначе (<=1 HP) урон не наносим — предмет не может убить
        }

        return result;
    }
}
