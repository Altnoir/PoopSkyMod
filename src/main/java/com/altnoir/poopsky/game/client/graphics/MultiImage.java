package com.altnoir.poopsky.game.client.graphics;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MultiImage extends Renderer {
    private List<Image> images = new ArrayList<>();
    private int current = 0;

    public MultiImage(List<Image> images) {
        this.images = images;
    }

    public MultiImage(ResourceLocation file, int fileWidth, int fileHeight, List<Rect2i> rects) {
        for (Rect2i rect : rects) {
            images.add(new Image(file, fileWidth, fileHeight, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()));
        }
    }

    public MultiImage(ResourceLocation file, int fileWidth, int fileHeight, int count) {
        this(file, fileWidth, fileHeight, fromFile(fileWidth, fileHeight, count));
    }

    public static List<Rect2i> fromFile(int fileWidth, int fileHeight, int count) {
        List<Rect2i> rects = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            rects.add(new Rect2i(0, fileHeight / count * i, fileWidth, fileHeight / count));
        }
        return rects;
    }

    public MultiImage setImage(int index) {
        current = Math.clamp(index, 0, count() - 1);
        return this;
    }

    public int count() {
        return images.size();
    }

    @Override
    public void render(GuiGraphics graphics, int posX, int posY) {
        if (count() > 0) {
            images.get(current).render(graphics, posX, posY);
        }
    }
}