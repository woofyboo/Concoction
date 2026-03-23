package net.mcreator.concoction.item;

import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT;
import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT_2;

public class MagmaEggItem extends TastefulItem {
    public MagmaEggItem() {
        super(new Item.Properties()
                .stacksTo(64)
                .component(FOOD_EFFECT.value(), new FoodEffectComponent(FoodEffectType.BITTER, 2, 180, true))
                .component(FOOD_EFFECT_2.value(), new FoodEffectComponent(FoodEffectType.MINTY, 1, 90, true))
                .rarity(Rarity.COMMON)
                .food(new FoodProperties.Builder()
                        .nutrition(2)
                        .saturationModifier(0.3f)
                        .build()));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);

        if (!level.isClientSide) {
            entity.igniteForSeconds(3);
            if (entity.getHealth() > 1.0F) {
                entity.hurt(level.damageSources().hotFloor(), 1.0F);
            }
        }

        return result;
    }
}
