package net.mcreator.concoction.item;

import net.mcreator.concoction.entity.CinnamonBoatEntity;
import net.mcreator.concoction.entity.CinnamonChestBoatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class CinnamonBoatDispenseItemBehavior extends DefaultDispenseItemBehavior {
	private final DispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();
	private final boolean chestBoat;

	public CinnamonBoatDispenseItemBehavior(boolean chestBoat) {
		this.chestBoat = chestBoat;
	}

	@Override
	public ItemStack execute(BlockSource source, ItemStack stack) {
		Direction direction = source.state().getValue(DispenserBlock.FACING);
		Level level = source.level();
		BlockPos dispenserPos = source.pos();
		double x = dispenserPos.getX() + 0.5D + (double)((float)direction.getStepX() * 1.125F);
		double y = dispenserPos.getY() + (double)direction.getStepY();
		double z = dispenserPos.getZ() + 0.5D + (double)((float)direction.getStepZ() * 1.125F);
		BlockPos targetPos = source.pos().relative(direction);
		double verticalOffset;

		if (level.getFluidState(targetPos).is(FluidTags.WATER)) {
			verticalOffset = 1.0D;
		} else {
			if (!level.getBlockState(targetPos).isAir() || !level.getFluidState(targetPos.below()).is(FluidTags.WATER)) {
				return this.defaultDispenseItemBehavior.dispense(source, stack);
			}
			verticalOffset = 0.0D;
		}

		Boat boat = this.chestBoat ? new CinnamonChestBoatEntity(level, x, y + verticalOffset, z) : new CinnamonBoatEntity(level, x, y + verticalOffset, z);
		boat.setYRot(direction.toYRot());
		level.addFreshEntity(boat);
		stack.shrink(1);
		return stack;
	}

	@Override
	protected void playSound(BlockSource source) {
		source.level().levelEvent(1000, source.pos(), 0);
	}
}
