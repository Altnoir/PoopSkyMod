package com.altnoir.poopsky.item;

import com.altnoir.poopsky.common.FlyType;

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
    GLOWSTONE,
    BLAZE,
    ENDER,
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