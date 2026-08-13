package com.altnoir.poopsky.client.games.graphics;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.games.util.Particle;

public class ExplosionParticleRenderer extends Renderer {
    private final MultiImage image;
    private final Particle particle;

    public ExplosionParticleRenderer(Particle particle) {
        image = new MultiImage(PoopSky.loc("textures/games/sprite/explosion.png"), 2, 16, 8);
        this.particle = particle;
    }

    @Override
    public void render(GuiGraphics graphics, int posX, int posY) {
        image.setImage((int)((Math.sqrt(particle.getVelocity().lengthSquared())) * 4)).render(graphics, posX, posY);
    }
}
