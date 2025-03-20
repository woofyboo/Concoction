
package net.mcreator.concoction.block;

import net.mcreator.concoction.init.ConcoctionModParticleTypes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SmallPillowBlock extends SlabBlock{
	public SmallPillowBlock() {
		super(Properties.of().ignitedByLava().mapColor(MapColor.WOOL).sound(SoundType.WOOL).strength(0.2f, 1f));
	}


	@Override
public void updateEntityAfterFallOn(BlockGetter block, Entity player) {
    this.bounceUp(player);
    summonLeafParticles(player);
    
    // Assuming `damage` is based on the fall distance, you can adjust the logic here
    float fallDistance = player.fallDistance; // Getting fall distance
    float damage = Math.min(fallDistance, 10.0F); // You can define a max damage limit (e.g., 10)
    
    player.causeFallDamage(damage, 0.0F, player.damageSources().fall());
}

	

	private void summonLeafParticles(Entity player) {
		if (player.level() instanceof ServerLevel _level) {
			Vec3 pos = player.position();
			int particlesCount = calcAmpl(player.getDeltaMovement());
			if (particlesCount != 0)
				_level.sendParticles(ConcoctionModParticleTypes.FEATHER_PARTICLE.get(),
					pos.x, pos.y+0.51, pos.z,
					particlesCount, 0, 0.1, 0, 0.4);
		}
	}
	private int calcAmpl(Vec3 ampl) {
		if (ampl.y < 0.3d)
			return 0;
		else return (int) (Math.min((float) ampl.y, 2f) * 50);
	}

	private void bounceUp(Entity player) {
		Vec3 vec3 = player.getDeltaMovement();
		if (vec3.y < 0.0) {
			double d0 = player instanceof LivingEntity ? 1.0 : 0.8;
			player.setDeltaMovement(vec3.x, -vec3.y * 0.66F * d0, vec3.z);
		}
	}

	@Override
	public void stepOn(Level p_154573_, BlockPos p_154574_, BlockState p_154575_, Entity p_154576_) {
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState p_56395_) {
		return p_56395_.getValue(TYPE) != SlabType.DOUBLE;
	}

	@Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_56388_) {
		p_56388_.add(new Property[]{TYPE, WATERLOGGED});
	}

	@Override
	public VoxelShape getShape(BlockState p_56390_, BlockGetter p_56391_, BlockPos p_56392_, CollisionContext p_56393_) {
		SlabType slabtype = (SlabType)p_56390_.getValue(TYPE);
		switch (slabtype) {
            case DOUBLE -> {
                return Shapes.block();
            }
            case TOP -> {
                return TOP_AABB;
            }
            default -> {
                return BOTTOM_AABB;
            }
        }
	}
//	@Override
//	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
//		return 15;
//	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 125;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 50;
	}
}
