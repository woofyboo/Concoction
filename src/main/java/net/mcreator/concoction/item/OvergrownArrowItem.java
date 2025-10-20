package net.mcreator.concoction.item;

import net.mcreator.concoction.entity.SunstruckArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantments;

import javax.annotation.Nullable;

public class OvergrownArrowItem extends ArrowItem {

    public OvergrownArrowItem() {
        super(new Item.Properties());
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammoStack, LivingEntity shooter, @Nullable ItemStack weaponStack) {
        // Возвращаем твою кастомную сущность стрелы
        return new SunstruckArrowEntity(
                net.mcreator.concoction.init.ConcoctionModEntities.SUNSTRUCK_ARROW.get(),
                shooter, level, weaponStack
        );
    }
}
