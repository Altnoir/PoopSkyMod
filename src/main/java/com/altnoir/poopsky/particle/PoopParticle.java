package com.altnoir.poopsky.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PoopParticle extends SpriteBillboardParticle {
    protected PoopParticle(ClientWorld clientWorld, double x, double y, double z, SpriteProvider spriteProvider, double xSpeed, double ySpeed, double zSpeed) {
        super(clientWorld, x, y, z, xSpeed, ySpeed, zSpeed  );

        this.setSpriteForAge(spriteProvider);

        this.velocityX += (random.nextFloat() - 0.5) * 0.1;
        this.velocityZ += (random.nextFloat() - 0.5) * 0.1;
        this.velocityY += random.nextFloat() * 0.02;

        this.gravityStrength = 0.8F;
        this.velocityMultiplier = 0.88F;
        this.maxAge = 40 + random.nextInt(20);
        this.scale *= 0.8F + random.nextFloat() * 0.4F;

        this.red = 1.0F;
        this.green =1.0F;
        this.blue = 1.0F;
    }
    @Override
    public void tick() {
        super.tick();

        if (this.age >= 20) {
            float progress = (float)(this.age - 20) / (this.maxAge - 20);
            this.alpha = 1.0F - progress;
        } else {
            this.alpha = 1.0F;
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new PoopParticle(world, x, y, z, this.spriteProvider, velocityX, velocityY, velocityZ);
        }
    }
}
