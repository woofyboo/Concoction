package net.mcreator.concoction.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class NetherSlopItem extends Item {
    public NetherSlopItem() {
        super(new Item.Properties()
                .stacksTo(16)
                .rarity(Rarity.COMMON)
                .food(new FoodProperties.Builder()
                        .nutrition(8)
                        .saturationModifier(0.6f)
                        .build()));
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack bowl = new ItemStack(Items.BOWL);
        super.finishUsingItem(stack, level, entity);

        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            if (stack.isEmpty()) {
                return bowl;
            } else if (!player.getInventory().add(bowl)) {
                player.drop(bowl, false);
            }
        }

        return stack;
    }
}
