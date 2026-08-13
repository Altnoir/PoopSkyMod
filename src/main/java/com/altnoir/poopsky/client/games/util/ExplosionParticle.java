package com.altnoir.poopsky.client.games.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec2;
import com.altnoir.poopsky.client.games.graphics.ExplosionParticleRenderer;
import com.altnoir.poopsky.client.games.graphics.Renderer;

public class ExplosionParticle extends Particle {
    private ExplosionParticleRenderer renderer = null;
    public ExplosionParticle(Vec2 pos, int lifetime, ParticleLevel level) {
        super(pos, new Renderer(), lifetime, level);
    }

    @Override
    public void render(GuiGraphics graphics, int gameX, int gameY, GameStage stage) {
        if (level.isFor(stage)) {
            if (renderer == null) {
                renderer = new ExplosionParticleRenderer(this);
            }
            renderer.render(graphics, gameX + (int)getX(), gameY + (int)getY());
        }
    }
}
