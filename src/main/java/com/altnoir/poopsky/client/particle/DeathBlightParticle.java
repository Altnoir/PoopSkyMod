package com.altnoir.poopsky.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DragonBreathParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class DeathBlightParticle extends DragonBreathParticle {
    private static final float YELLOW_PARTICLE_CHANCE = 0.18F;

    protected DeathBlightParticle(ClientLevel level, double x, double y, double z,
                                  double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        if (this.random.nextFloat() < YELLOW_PARTICLE_CHANCE) {
            float brightness = 0.75F + this.random.nextFloat() * 0.25F;
            this.setColor(0.38F * brightness, 0.29F * brightness, 0.035F * brightness);
        } else {
            float blackVariation = this.random.nextFloat() * 0.035F;
            this.setColor(blackVariation, blackVariation, blackVariation);
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new DeathBlightParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
