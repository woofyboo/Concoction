
package net.mcreator.concoction.client.particle;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;

import net.mcreator.concoction.procedures.FeatherParticleAdditionalParticleExpiryConditionProcedure;

@OnlyIn(Dist.CLIENT)
public class FeatherParticleParticle extends TextureSheetParticle {
	public static FeatherParticleParticleProvider provider(SpriteSet spriteSet) {
		return new FeatherParticleParticleProvider(spriteSet);
	}

	public static class FeatherParticleParticleProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public FeatherParticleParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new FeatherParticleParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	private final SpriteSet spriteSet;
	private float angularVelocity;
	private float angularAcceleration;

	protected FeatherParticleParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
		super(world, x, y, z);
		this.spriteSet = spriteSet;
		this.setSize(0.6f, 0.6f);
		this.lifetime = (int) Math.max(1, 40 + (this.random.nextInt(10) - 5));
		this.gravity = 0.05f;
		this.hasPhysics = true;
		this.xd = vx * 0.6;
		this.yd = vy * 0.6;
		this.zd = vz * 0.6;
		this.angularVelocity = 0.2f;
		this.angularAcceleration = -0.01f;
		this.pickSprite(spriteSet);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@Override
	public void tick() {
		super.tick();
		this.oRoll = this.roll;
		this.roll += this.angularVelocity;
		this.angularVelocity += this.angularAcceleration;
		Level world = this.level;
		if (FeatherParticleAdditionalParticleExpiryConditionProcedure.execute(world, x, y, z))
			this.remove();
	}
}
