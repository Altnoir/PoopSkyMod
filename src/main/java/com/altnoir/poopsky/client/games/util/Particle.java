package com.altnoir.poopsky.client.games.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import com.altnoir.poopsky.client.games.graphics.*;

public class Particle {
    // Properties of the Particle
    private int lifetime;
    public ParticleLevel level;
    private Vec2 pos = Vec2.ZERO;
    private Vec2 vel = Vec2.ZERO;
    private Renderer image = new Renderer();
    public Particle(Vec2 pos, ParticleColor color, int lifetime, ParticleLevel level) {
        this.pos = pos;
        this.image = new BasicParticleRenderer(color);
        this.lifetime = lifetime;
        this.level = level;
    }
    public Particle(Vec2 pos, Renderer image, int lifetime, ParticleLevel level) {
        this.pos = pos;
        this.image = image;
        this.lifetime = lifetime;
        this.level = level;
    }
    public Particle(Vec2 pos, ResourceLocation file, int fileWidth, int fileHeight, int lifetime, ParticleLevel level) {
        this.pos = pos;
        this.lifetime = lifetime;
        this.level = level;
        this.image = new BreakParticleRenderer(file, fileWidth, fileHeight);
    }
    public Particle(Vec2 pos, ResourceLocation file, int fileWidth, int fileHeight, int posX, int posY, int width, int height, int lifetime, ParticleLevel level) {
        this.pos = pos;
        this.lifetime = lifetime;
        this.level = level;
        this.image = new ParticleRenderer(file, fileWidth, fileHeight, posX, posY, width, height);
    }

    // getters and setters
    public Vec2 getPos() {
        return pos;
    }
    public float getX() {
        return pos.x;
    }
    public float getY() {
        return pos.y;
    }
    public void setPos(Vec2 pos) {
        this.pos = pos;
    }
    public void moveBy(Vec2 offset) {
        pos = pos.add(offset);
    }
    public Vec2 getVelocity() {
        return vel;
    }
    public void setVelocity(Vec2 vel) {
        this.vel = vel;
    }
    public void addVelocity(Vec2 vel) {
        this.vel = this.vel.add(vel);
    }
    public void tick() {
        this.pos = this.pos.add(this.vel);
        this.lifetime--;
    }

    /**
     * Renders the particle
     * @param graphics GuiGraphics used for rendering
     * @param gameX X position of game
     * @param gameY Y position of game
     */
    public void render(GuiGraphics graphics, int gameX, int gameY, GameStage stage) {
        if (level.isFor(stage)) {
            image.render(graphics, gameX + (int) this.pos.x, gameY + (int) this.pos.y);
        }
    }

    public boolean isForOverlay() {
        return level == ParticleLevel.OVERLAY;
    }

    public boolean isDead() {
        return lifetime <= 0;
    }
}
