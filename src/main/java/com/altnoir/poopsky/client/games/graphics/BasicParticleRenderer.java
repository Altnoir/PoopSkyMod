package com.altnoir.poopsky.client.games.graphics;

import net.minecraft.resources.ResourceLocation;
import com.altnoir.poopsky.PoopSky;

public class BasicParticleRenderer extends ParticleRenderer {
    private static final ResourceLocation IMAGE = PoopSky.loc("textures/particle/basic_particle.png");

    public BasicParticleRenderer(ParticleColor color) {
        super(IMAGE, 2, 32, 0, color.value() * 2, 2, 2);
    }
}
