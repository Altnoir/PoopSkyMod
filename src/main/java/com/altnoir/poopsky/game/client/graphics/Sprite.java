package com.altnoir.poopsky.game.client.graphics;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec2;

public class Sprite {
    private float x;
    private float y;
    private final Vec2 size;
    private Renderer image;

    public Sprite(Vec2 pos, Vec2 size, Renderer image) {
        setPos(pos);
        this.size = size;
        this.image = image;
    }

    public Sprite(Vec2 pos, Vec2 size, Identifier image) {
        this(pos, size, new Image(image, (int) size.x, (int) size.y));
    }

    public void setPos(Vec2 pos) {
        setPos(pos.x, pos.y);
    }

    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setY(float y) {
        this.y = y;
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
        graphics.pose().pushMatrix();
        graphics.pose().translate(gameX + x, gameY + y);
        image.render(graphics, 0, 0);
        graphics.pose().popMatrix();
    }

    public void renderScaled(GuiGraphicsExtractor graphics, int gameX, int gameY, float scale) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(gameX + x, gameY + y);
        image.renderScaled(graphics, 0, 0, scale);
        graphics.pose().popMatrix();
    }
}
