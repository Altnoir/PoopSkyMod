package com.altnoir.poopsky.block;

import com.altnoir.poopsky.init.PToiletTypes;
import com.altnoir.poopsky.init.ToiletType;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum WoodToiletVariant implements StringRepresentable {
    OAK(PToiletTypes.OAK),
    SPRUCE(PToiletTypes.SPRUCE),
    BIRCH(PToiletTypes.BIRCH),
    JUNGLE(PToiletTypes.JUNGLE),
    ACACIA(PToiletTypes.ACACIA),
    CHERRY(PToiletTypes.CHERRY),
    DARK_OAK(PToiletTypes.DARK_OAK),
    MANGROVE(PToiletTypes.MANGROVE),
    BAMBOO(PToiletTypes.BAMBOO),
    CRIMSON(PToiletTypes.CRIMSON),
    WARPED(PToiletTypes.WARPED);

    public static final Codec<WoodToiletVariant> CODEC = StringRepresentable.fromEnum(WoodToiletVariant::values);

    private final ToiletType toiletType;

    WoodToiletVariant(ToiletType toiletType) {
        this.toiletType = toiletType;
    }

    public ToiletType getToiletType() {
        return toiletType;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }

    public static WoodToiletVariant byToiletType(ToiletType type) {
        for (var v : values()) {
            if (v.toiletType.equals(type)) return v;
        }
        return null;
    }
}