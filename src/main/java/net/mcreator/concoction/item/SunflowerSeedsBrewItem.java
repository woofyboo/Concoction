package net.mcreator.concoction.item;

import net.mcreator.concoction.init.ConcoctionModDataComponents;
import net.mcreator.concoction.init.ConcoctionModMobEffects;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SunflowerSeedsBrewItem extends TastefulItem {
    public SunflowerSeedsBrewItem() {
        super(new net.minecraft.world.item.Item.Properties()
                .stacksTo(16)
                .rarity(Rarity.COMMON)
                .food((new FoodProperties.Builder())
                        .nutrition(0)
                        .saturationModifier(0.3f)
                        .alwaysEdible()
                        .build())
                .component(ConcoctionModDataComponents.FOOD_EFFECT.get(), new FoodEffectComponent(FoodEffectType.WARM, 1, 120, true)));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemstack) {
        return UseAnim.DRINK;
    }

    @Override
    public boolean hasCraftingRemainingItem() {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemstack) {
        return new ItemStack(Items.GLASS_BOTTLE);
    }

    @Override
    public @NotNull SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
        ItemStack retval = new ItemStack(Items.GLASS_BOTTLE);
        super.finishUsingItem(itemstack, world, entity);
        if (!world.isClientSide()) {
            entity.addEffect(new MobEffectInstance(ConcoctionModMobEffects.SUNSTRUCK_EFFECT, 300 * 20, 1, false, false, true, null));
            entity.addEffect(new MobEffectInstance(ConcoctionModMobEffects.PHOTOSYNTHESIS, 300 * 20, 3, false, false, true, null));
        }
        if (itemstack.isEmpty()) {
            return retval;
        } else {
            if (entity instanceof Player player && !player.getAbilities().instabuild) {
                if (!player.getInventory().add(retval)) {
                    player.drop(retval, false);
                }
            }
            return itemstack;
        }
    }
}
