package com.altnoir.poopsky.game.client.graphics;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
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

    public Sprite(Vec2 pos, Vec2 size, ResourceLocation image) {
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

    public void setImage(ResourceLocation image) {
        this.image = new Image(image, (int) size.x, (int) size.y);
    }

    public void render(GuiGraphics graphics, int gameX, int gameY) {
        graphics.pose().pushPose();
        graphics.pose().translate(gameX + x, gameY + y, 0.0F);
        image.render(graphics, 0, 0);
        graphics.pose().popPose();
    }

    public void renderScaled(GuiGraphics graphics, int gameX, int gameY, float scale) {
        graphics.pose().pushPose();
        graphics.pose().translate(gameX + x, gameY + y, 0.0F);
        image.renderScaled(graphics, 0, 0, scale);
        graphics.pose().popPose();
    }
}
