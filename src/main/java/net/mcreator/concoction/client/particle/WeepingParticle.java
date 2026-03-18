package net.mcreator.concoction.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WeepingParticle extends TextureSheetParticle {
	private final SpriteSet spriteSet;
	private final int startupDelay;
	private final int animationLifetime;
	private int animationAge;

	public static WeepingParticleProvider provider(SpriteSet spriteSet) {
		return new WeepingParticleProvider(spriteSet);
	}

	public static class WeepingParticleProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public WeepingParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new WeepingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	protected WeepingParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
		super(level, x, y, z);
		this.spriteSet = spriteSet;
		this.startupDelay = this.random.nextInt(5);
		this.animationLifetime = 8 + this.random.nextInt(4);
		this.animationAge = 0;
		this.friction = 0.96F;
		this.gravity = -0.1F;
		this.speedUpWhenYMotionIsBlocked = true;
		this.hasPhysics = false;
		this.quadSize *= 0.75F;
		this.lifetime = this.startupDelay + this.animationLifetime;
		this.xd = (this.random.nextDouble() - 0.5D) * 0.08D;
		this.yd = 0.02D + this.random.nextDouble() * 0.02D;
		this.zd = (this.random.nextDouble() - 0.5D) * 0.08D;
		this.alpha = 0.0F;
		this.setSprite(this.spriteSet.get(0, this.animationLifetime));
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.removed) {
			if (this.age <= this.startupDelay) {
				this.alpha = 0.0F;
				this.setSprite(this.spriteSet.get(0, this.animationLifetime));
				return;
			}

			this.alpha = 1.0F;
			this.animationAge = Math.min(this.animationAge + 1, this.animationLifetime);
			this.setSprite(this.spriteSet.get(this.animationAge, this.animationLifetime));
		}
	}
}
