
package net.mcreator.concoction.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class SunflowerOilItem extends Item {
	public SunflowerOilItem() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.COMMON));
	}
}
