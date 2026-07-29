package com.altnoir.poopsky.content.block;

public enum CompooperType {
    WATER("water"),
    LAVA("lava"),
    POWDER_SNOW("powder_snow"),
    URINE("urine");

    private final String id;

    CompooperType(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }
}
