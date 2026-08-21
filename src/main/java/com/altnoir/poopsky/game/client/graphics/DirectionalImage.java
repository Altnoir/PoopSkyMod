package com.altnoir.poopsky.game.client.graphics;

import net.minecraft.resources.Identifier;

public class DirectionalImage extends MultiImage {
    public DirectionalImage(Identifier location, int fileWidth, int fileHeight) {
        super(location, fileWidth, fileHeight, fromFile(fileWidth, fileHeight, 4));
    }

    @Override
    public int count() {
        return 4;
    }
}
