
package net.mcreator.concoction.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BucketItem;

import net.mcreator.concoction.init.ConcoctionModFluids;

public class WeightedSoulsItem extends BucketItem {
	public WeightedSoulsItem() {
		super(ConcoctionModFluids.WEIGHTED_SOULS.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).rarity(Rarity.COMMON));
	}
}
