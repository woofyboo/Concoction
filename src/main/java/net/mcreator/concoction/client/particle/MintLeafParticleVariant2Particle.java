
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

import net.mcreator.concoction.procedures.MintLeafParticleVariant1AdditionalParticleExpiryConditionProcedure;

@OnlyIn(Dist.CLIENT)
public class MintLeafParticleVariant2Particle extends TextureSheetParticle {
	public static MintLeafParticleVariant2ParticleProvider provider(SpriteSet spriteSet) {
		return new MintLeafParticleVariant2ParticleProvider(spriteSet);
	}

	public static class MintLeafParticleVariant2ParticleProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public MintLeafParticleVariant2ParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new MintLeafParticleVariant2Particle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	private final SpriteSet spriteSet;
	private float angularVelocity;
	private float angularAcceleration;

	protected MintLeafParticleVariant2Particle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
		super(world, x, y, z);
		this.spriteSet = spriteSet;
		this.setSize(0.1f, 0.1f);
		this.lifetime = (int) Math.max(1, 60 + (this.random.nextInt(80) - 40));
		this.gravity = 0.2f;
		this.hasPhysics = true;
		this.xd = vx * 0.1;
		this.yd = vy * 0.1;
		this.zd = vz * 0.1;
		this.angularVelocity = 0.1f;
		this.angularAcceleration = 0f;
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
		if (MintLeafParticleVariant1AdditionalParticleExpiryConditionProcedure.execute(onGround))
			this.remove();
	}
}
