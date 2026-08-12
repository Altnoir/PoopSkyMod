package com.altnoir.poopsky.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class ToiletParticle extends SingleQuadParticle {
    protected ToiletParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z, sprites.first());
        this.setSpriteFromAge(sprites);
        this.gravity = 0.8F;
        this.friction = 0.99F;
        this.lifetime = 40 + level.getRandom().nextInt(20);
        this.quadSize *= 0.8F + level.getRandom().nextFloat() * 0.4F;
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
        this.xd = (level.getRandom().nextFloat() - 0.5F) * 0.05F;
        this.yd = 0.25F + level.getRandom().nextFloat() * 0.3F;
        this.zd = (level.getRandom().nextFloat() - 0.5F) * 0.05F;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age >= 20) {
            var progress = (float) (this.age - 20) / (this.lifetime - 20);
            this.alpha = 1.0F - progress;
        } else {
            this.alpha = 1.0F;
        }
    }

    @Override
    public Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            return new ToiletParticle(level, x, y, z, this.sprites);
        }
    }
}
