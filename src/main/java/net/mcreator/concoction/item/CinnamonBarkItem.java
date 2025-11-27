
package net.mcreator.concoction.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public class CinnamonBarkItem extends Item {
	public CinnamonBarkItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}


}
