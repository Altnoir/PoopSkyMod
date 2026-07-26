package com.altnoir.poopsky.impl.network;

public enum PoAnimation {
    INTRO("intro"),
    POEM("poem");

    private static final PoAnimation[] VALUES = values();

    private final String serializedName;

    PoAnimation(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return this.serializedName;
    }

    public static PoAnimation byId(int id) {
        return id >= 0 && id < VALUES.length ? VALUES[id] : INTRO;
    }
}
