package net.mcreator.concoction.item;

import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT;

public class KozinakItem extends TastefulItem {
    public KozinakItem() {
        super(new Item.Properties().stacksTo(64)
                .component(FOOD_EFFECT.value(), new FoodEffectComponent(FoodEffectType.SWEET, 2, 30, true))
                .rarity(Rarity.COMMON)
                .food((new FoodProperties.Builder()).nutrition(6).saturationModifier(0.6f).build()));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
        ItemStack retval = super.finishUsingItem(itemstack, world, entity);
        if (!world.isClientSide()) {
            entity.addEffect(new MobEffectInstance(ConcoctionModMobEffects.PHOTOSYNTHESIS, 180 * 20, 0, false, false, true, null));
        }
        return retval;
    }
}
