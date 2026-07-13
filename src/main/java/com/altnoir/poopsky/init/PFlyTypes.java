package com.altnoir.poopsky.init;

import com.altnoir.poopsky.content.FlyType;

public enum PFlyTypes {
    NORMAL,
    WHITE,
    BLACK,
    GREEN,
    BLUE,
    RED,
    GRAY,
    LIGHT_GRAY,
    BROWN,
    ORANGE,
    YELLOW,
    LIME,
    CYAN,
    LIGHT_BLUE,
    PURPLE,
    MAGENTA,
    PINK,
    // MORE
    IRON,
    COPPER,
    GOLD,
    EMERALD,
    DIAMOND,
    NETHERITE,
    DRAGON_FRUIT,
    GLOWSTONE,
    ENDER,
    // CREATE
    ZINC,
    ;

    private final FlyType.Type flyType;

    PFlyTypes() {
        this.flyType = new FlyType.Type(name().toLowerCase());
    }

    public String id() {
        return name().toLowerCase();
    }

    public FlyType.Type get() {
        return flyType;
    }
}