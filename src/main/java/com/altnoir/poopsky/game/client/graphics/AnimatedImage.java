package com.altnoir.poopsky.game.client.graphics;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class AnimatedImage extends MultiImage {
    public AnimatedImage(List<Image> images, int duration) {
        super(images);
    }

    public AnimatedImage(ResourceLocation file, int fileWidth, int fileHeight, List<Rect2i> rects, int duration) {
        super(file, fileWidth, fileHeight, rects);
    }

    public AnimatedImage(ResourceLocation file, int fileWidth, int fileHeight, int frames, int duration) {
        this(file, fileWidth, fileHeight, fromFile(fileWidth, fileHeight, frames), duration);
    }
}
