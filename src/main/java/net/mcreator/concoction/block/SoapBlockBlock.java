
package net.mcreator.concoction.block;

import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.entity.Mob;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.mcreator.concoction.init.ConcoctionModParticleTypes;

public class SoapBlockBlock extends Block {
	public SoapBlockBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PINK).sound(SoundType.STONE).strength(1.5f, 2f).requiresCorrectToolForDrops().friction(1f));
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 15;
	}

	@Override
	public PathType getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
		return PathType.STICKY_HONEY;
	}

	@Override
public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
    super.stepOn(level, pos, state, entity);

    if (!level.isClientSide) return;
    if (!entity.onGround()) return;

    // горизонтальная скорость по перемещению за тик
    double dx = entity.getX() - entity.xOld;
    double dz = entity.getZ() - entity.zOld;
    double speed = Math.sqrt(dx * dx + dz * dz);

    final double minSpeed = 0.05;
    if (speed < minSpeed) return;

    // лёгкий дроссель (каждый 4-й тик)
    if (((level.getGameTime() + entity.getId()) & 3L) != 0L) return;

    final double maxRefSpeed = 0.20;
    double t = Mth.clamp((speed - minSpeed) / (maxRefSpeed - minSpeed), 0.0, 1.0);

    // шанс тика с частицами (редко)
    float spawnProb = (float) (0.10 + 0.25 * t);
    if (level.random.nextFloat() > spawnProb) return;

    // обычно 1, изредка 2 пузырька
    int count = (level.random.nextFloat() < 0.15F) ? 2 : 1;

    for (int i = 0; i < count; i++) {
        double ox = 0.3 + level.random.nextDouble() * 0.4;
        double oz = 0.3 + level.random.nextDouble() * 0.4;
        double x = pos.getX() + ox;
        double y = pos.getY() + 1.02; // чуть над поверхностью блока
        double z = pos.getZ() + oz;

        double vx = (level.random.nextGaussian()) * 0.008;
        double vz = (level.random.nextGaussian()) * 0.008;
        double vy = 0.02 + 0.03 * t + level.random.nextDouble() * 0.015;

        level.addParticle(ConcoctionModParticleTypes.SOAP_BUBBLE.get(), x, y, z, vx, vy, vz);
    }

    // иногда звук "лопнувшего" пузырька
    if (((level.getGameTime() + entity.getId()) & 7L) == 0L && level.random.nextFloat() < 0.2F) {
        double sx = pos.getX() + 0.4 + level.random.nextDouble() * 0.2;
        double sy = pos.getY() + 1.1;
        double sz = pos.getZ() + 0.4 + level.random.nextDouble() * 0.2;
        float volume = 0.25F + level.random.nextFloat() * 0.15F;
        float pitch  = 0.9F  + level.random.nextFloat() * 0.3F;
        level.playLocalSound(sx, sy, sz, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.BLOCKS, volume, pitch, false);
    }
}

}
