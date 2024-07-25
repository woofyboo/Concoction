
package net.mcreator.concoction.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;

public class PillowBlockBlock extends SlimeBlock {
	public PillowBlockBlock() {
		super(BlockBehaviour.Properties.of().ignitedByLava().mapColor(MapColor.WOOL).sound(SoundType.WOOL).strength(0.2f, 1f));
	}

	@Override
	public void updateEntityAfterFallOn(BlockGetter p_56406_, Entity p_56407_) {
		if (p_56407_.isSuppressingBounce()) {
			super.updateEntityAfterFallOn(p_56406_, p_56407_);
		} else {
			this.bounceUp(p_56407_);
		}
	}

	private void bounceUp(Entity p_56404_) {
		Vec3 vec3 = p_56404_.getDeltaMovement();
		if (vec3.y < 0.0) {
			double d0 = p_56404_ instanceof LivingEntity ? 1.0 : 0.8;
			p_56404_.setDeltaMovement(vec3.x, -vec3.y * 0.66F * d0, vec3.z);
		}
	}

	@Override
	public void stepOn(Level p_154573_, BlockPos p_154574_, BlockState p_154575_, Entity p_154576_) {
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 125;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 50;
	}
}
