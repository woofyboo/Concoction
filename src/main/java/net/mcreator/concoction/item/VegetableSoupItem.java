package net.mcreator.concoction.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.*;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;

public class VegetableSoupItem extends Item {
    public VegetableSoupItem() {
        super(new Item.Properties().stacksTo(16)
                .rarity(Rarity.COMMON)
                .food((new FoodProperties.Builder()).nutrition(8).saturationModifier(0.8f).build()));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
        ItemStack retval = new ItemStack(Items.BOWL);

        // СЃРЅР°С‡Р°Р»Р° СЃС‚Р°РЅРґР°СЂС‚РЅРѕРµ РїРѕРІРµРґРµРЅРёРµ РµРґС‹ (С…РёР», РЅР°СЃС‹С‰РµРЅРёРµ, СЌС„С„РµРєС‚С‹ Рё С‚.Рґ.)
        ItemStack result = super.finishUsingItem(itemstack, world, entity);

        // РЎР‘Р РћРЎ Р—РђРњРћР РћР—РљР
        if (!world.isClientSide) {
            // РјРѕРјРµРЅС‚Р°Р»СЊРЅРѕ СЂР°Р·РјРѕСЂР°Р¶РёРІР°РµРј СЃСѓС‰РЅРѕСЃС‚СЊ (РїРѕСЂРѕС€РєРѕРІС‹Р№ СЃРЅРµРі, С…РѕР»РѕРґ Рё С‚.Рї.)
            entity.setTicksFrozen(0);
        }

        // РґР°Р»СЊС€Рµ РІР°РЅРёР»СЊРЅР°СЏ Р»РѕРіРёРєР° СЃ РјРёСЃРєРѕР№
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

