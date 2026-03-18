package net.mcreator.concoction.entity;

import net.mcreator.concoction.init.ConcoctionBoatEntities;
import net.mcreator.concoction.init.ConcoctionBoatItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class CinnamonBoatEntity extends Boat {
	public CinnamonBoatEntity(EntityType<? extends Boat> entityType, Level level) {
		super(entityType, level);
		this.setVariant(Type.CHERRY);
	}

	public CinnamonBoatEntity(Level level, double x, double y, double z) {
		this(ConcoctionBoatEntities.CINNAMON_BOAT.get(), level);
		this.setPos(x, y, z);
		this.xo = x;
		this.yo = y;
		this.zo = z;
	}

	@Override
	public Item getDropItem() {
		return ConcoctionBoatItems.CINNAMON_BOAT.get();
	}
}
