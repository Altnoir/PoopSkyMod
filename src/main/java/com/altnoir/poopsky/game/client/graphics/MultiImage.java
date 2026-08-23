package com.altnoir.poopsky.game.client.graphics;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.stream.IntStream;

public class MultiImage implements Renderer {
    private final List<Image> images;
    private int current;

    public MultiImage(Identifier file, int fileWidth, int fileHeight, List<Rect2i> rects) {
        images = rects.stream()
                .map(rect -> new Image(file, fileWidth, fileHeight, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight()))
                .toList();
    }

    public MultiImage(Identifier file, int fileWidth, int fileHeight, int count) {
        this(file, fileWidth, fileHeight, fromFile(fileWidth, fileHeight, count));
    }

    public static List<Rect2i> fromFile(int fileWidth, int fileHeight, int count) {
        int imageHeight = fileHeight / count;
        return IntStream.range(0, count)
                .mapToObj(i -> new Rect2i(0, imageHeight * i, fileWidth, imageHeight))
                .toList();
    }

    public MultiImage setImage(int index) {
        current = Math.clamp(index, 0, count() - 1);
        return this;
    }

    public int count() {
        return images.size();
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int posX, int posY) {
        if (!images.isEmpty()) {
            images.get(current).render(graphics, posX, posY);
        }
    }
}

