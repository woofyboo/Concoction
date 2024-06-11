
package net.mcreator.concoction.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;

import net.mcreator.concoction.init.ConcoctionModBlocks;

public class MintSeedsItem extends Item {
	public MintSeedsItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}
}
