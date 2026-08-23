package com.altnoir.poopsky.game.client.graphics;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
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

    public Sprite(Vec2 pos, Vec2 size, ResourceLocation image) {
        this.pos = pos;
        this.size = size;
        this.image = new Image(image, (int) size.x, (int) size.y);
    }

    public void setPos(Vec2 pos) {
        this.pos = pos;
    }

    public void setY(float y) {
        this.pos = new Vec2(this.pos.x, y);
    }

    public float getWidth() {
        return size.x;
    }

    public Renderer getImage() {
        return image;
    }

    public void setImage(Renderer image) {
        this.image = image;
    }

    public void setImage(ResourceLocation image) {
        this.image = new Image(image, (int) size.x, (int) size.y);
    }

    public void render(GuiGraphics graphics, int gameX, int gameY) {
        image.render(graphics, gameX + (int) pos.x, gameY + (int) pos.y);
    }

    public void renderScaled(GuiGraphics graphics, int gameX, int gameY, float scale) {
        image.renderScaled(graphics, gameX + (int) pos.x, gameY + (int) pos.y, scale);
    }
}
