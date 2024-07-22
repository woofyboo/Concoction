
package net.mcreator.concoction.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class CottonItem extends Item {
	public CottonItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}
}
