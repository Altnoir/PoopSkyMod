package com.altnoir.poopsky.client.games.util;

import net.minecraft.world.phys.Vec2;
import com.altnoir.poopsky.client.games.graphics.ParticleColor;

import java.util.Random;

public class ConfettiParticle extends Particle {
    private final Random random = new Random();
    public ConfettiParticle(Vec2 pos, ParticleColor color, int lifetime, ParticleLevel level) {
        super(pos, color, lifetime, level);
    }

    @Override
    public void tick() {
        super.tick();
        this.setVelocity(getVelocity().scale(0.8f));
        this.addVelocity(new Vec2(random.nextFloat(-0.2f, 0.2f), 0.3f));
    }
}
