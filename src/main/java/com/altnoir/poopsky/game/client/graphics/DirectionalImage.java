package com.altnoir.poopsky.game.client.graphics;

import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class DirectionalImage extends MultiImage {
    public DirectionalImage(ResourceLocation location, int fileWidth, int fileHeight) {
        this(location, fileWidth, fileHeight, fromFile(fileWidth, fileHeight, 4));
    }

    public DirectionalImage(ResourceLocation file, int fileWidth, int fileHeight, List<Rect2i> rects) {
        super(file, fileWidth, fileHeight, rects);
    }

    @Override
    public int count() {
        return 4;
    }
}
