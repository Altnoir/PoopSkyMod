package com.altnoir.poopsky.game.client.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import com.altnoir.poopsky.game.client.graphics.Image;
import com.altnoir.poopsky.game.client.graphics.Renderer;

public class Sprite {
    // Properties of the Sprite
    private Vec2 pos = Vec2.ZERO;
    private Vec2 size = Vec2.ZERO;
    private Vec2 vel = Vec2.ZERO;
    private Renderer image = new Renderer();
    private boolean shown = true;
    public Sprite(Vec2 pos, Vec2 size, Renderer image) {
        this.pos = pos;
        this.size = size;
        this.image = image;
    }

    public Sprite(Vec2 pos, Vec2 size, ResourceLocation image) {
        this.pos = pos;
        this.size = size;
        this.image = new Image(image, (int)size.x, (int)size.y);
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
    public void setX(float x) {
        this.pos = new Vec2(x, getY());
    }
    public void setY(float y) {
        this.pos = new Vec2(getX(), y);
    }
    public Vec2 getSize() {
        return size;
    }
    public float getWidth() {
        return size.x;
    }
    public float getHeight() {
        return size.y;
    }
    public void setSize(Vec2 size) {
        this.size = size;
    }
    public Sprite setVelocity(Vec2 vel) {
        this.vel = vel;
        return this;
    }
    public Renderer getImage() {
        return image;
    }
    public void setImage(Renderer image) {
        this.image = image;
    }
    public void setImage(ResourceLocation image) {
        this.image = new Image(image, (int)this.size.x, (int)this.size.y);
    }
    /**
     * Renders the sprite
     * @param graphics GuiGraphics used for rendering
     * @param gameX X position of game
     * @param gameY Y position of game
     */
    public void render(GuiGraphics graphics, int gameX, int gameY) {
        if (shown) {
            image.render(graphics, gameX + (int) this.pos.x, gameY + (int) this.pos.y);
        }
    }
}
