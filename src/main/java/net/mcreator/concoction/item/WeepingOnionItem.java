package net.mcreator.concoction.item;

import net.mcreator.concoction.init.ConcoctionModBlocks;
import net.mcreator.concoction.item.food.types.FoodEffectComponent;
import net.mcreator.concoction.item.food.types.FoodEffectType;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Rarity;

import static net.mcreator.concoction.init.ConcoctionModDataComponents.FOOD_EFFECT;

public class WeepingOnionItem extends ItemNameBlockItem {

    public WeepingOnionItem() {
        super(
                ConcoctionModBlocks.CROP_WEEPING_ONION.get(), // какой блок сажать
                new Item.Properties()
                        .stacksTo(64)
                        .component(FOOD_EFFECT.value(),
                                new FoodEffectComponent(FoodEffectType.WEEPING, 1, 90, false))
                        .rarity(Rarity.COMMON)
                        .food(new FoodProperties.Builder()
                                .nutrition(5)
                                .saturationModifier(0.3f)
                                .build())
        );
    }
}
