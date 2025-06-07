package com.altnoir.poopsky.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

public class PoopParticle extends TextureSheetParticle {
    protected PoopParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.setSpriteFromAge(sprites);
        this.gravity = 0.8F;
        this.friction = 0.88F;
        this.lifetime = 40 + level.random.nextInt(20);
        this.quadSize *= 0.8F + level.random.nextFloat() * 0.4F;
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age >= 20) {
            var progress = (float)(this.age - 20) / (this.lifetime - 20);
            this.alpha = 1.0F - progress;
        } else {
            this.alpha = 1.0F;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new PoopParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
