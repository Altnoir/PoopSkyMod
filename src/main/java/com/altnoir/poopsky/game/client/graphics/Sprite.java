package com.altnoir.poopsky.game.client.graphics;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;

public class Sprite {
    private Vec2 pos;
    private final Vec2 size;
    private Renderer image;

    public Sprite(Vec2 pos, Vec2 size, Renderer image) {
        this.pos = pos;
        this.size = size;
        this.image = image;
    }

    public Sprite(Vec2 pos, Vec2 size, Identifier image) {
        this(pos, size, new Image(image, (int) size.x, (int) size.y));
    }

    public void setPos(Vec2 pos) {
        this.pos = pos;
    }

    public void setY(float y) {
        pos = new Vec2(pos.x, y);
    }

    public float getWidth() {
        return size.x;
    }

    public void setImage(Renderer image) {
        this.image = image;
    }

    public void setImage(Identifier image) {
        this.image = new Image(image, (int) size.x, (int) size.y);
    }

    public void render(GuiGraphicsExtractor graphics, int gameX, int gameY) {
        image.render(graphics, gameX + (int) pos.x, gameY + (int) pos.y);
    }

    public void renderScaled(GuiGraphicsExtractor graphics, int gameX, int gameY, float scale) {
        image.renderScaled(graphics, gameX + (int) pos.x, gameY + (int) pos.y, scale);
    }
}

