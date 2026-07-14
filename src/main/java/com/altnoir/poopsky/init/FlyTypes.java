package com.altnoir.poopsky.init;

import com.altnoir.poopsky.content.FlyType;

public enum FlyTypes {
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
    // AE2
    CERTUS,
    SKY_DUST,
    // MEK
    OSMIUM,
    TIN,
    LEAD,
    URANIUM,
    FLUORITE,
    ;

    private final FlyType.Type flyType;

    FlyTypes() {
        this.flyType = new FlyType.Type(name().toLowerCase());
    }

    public String id() {
        return name().toLowerCase();
    }

    public FlyType.Type get() {
        return flyType;
    }
}