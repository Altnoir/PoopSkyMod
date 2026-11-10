package com.altnoir.poopsky.content.block;

import net.minecraft.util.StringRepresentable;

public enum PigstyPart implements StringRepresentable {
    BACK("back"),
    FRONT("front");

    private final String name;

    PigstyPart(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
