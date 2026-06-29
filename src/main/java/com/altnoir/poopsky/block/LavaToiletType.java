package com.altnoir.poopsky.block;

import com.altnoir.poopsky.init.PToiletTypes;
import com.altnoir.poopsky.init.ToiletType;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum LavaToiletType implements StringRepresentable {
    STONE(PToiletTypes.STONE),
    COBBLESTONE(PToiletTypes.COBBLESTONE),
    MOSSY_COBBLESTONE(PToiletTypes.MOSSY_COBBLESTONE),
    SMOOTH_STONE(PToiletTypes.SMOOTH_STONE),
    STONE_BRICK(PToiletTypes.STONE_BRICK),
    MOSSY_STONE_BRICK(PToiletTypes.MOSSY_STONE_BRICK),
    TILE(PToiletTypes.TILE),
    WHITE_CONCRETE(PToiletTypes.WHITE_CONCRETE),
    ORANGE_CONCRETE(PToiletTypes.ORANGE_CONCRETE),
    MAGENTA_CONCRETE(PToiletTypes.MAGENTA_CONCRETE),
    LIGHT_BLUE_CONCRETE(PToiletTypes.LIGHT_BLUE_CONCRETE),
    YELLOW_CONCRETE(PToiletTypes.YELLOW_CONCRETE),
    LIME_CONCRETE(PToiletTypes.LIME_CONCRETE),
    PINK_CONCRETE(PToiletTypes.PINK_CONCRETE),
    GRAY_CONCRETE(PToiletTypes.GRAY_CONCRETE),
    LIGHT_GRAY_CONCRETE(PToiletTypes.LIGHT_GRAY_CONCRETE),
    CYAN_CONCRETE(PToiletTypes.CYAN_CONCRETE),
    PURPLE_CONCRETE(PToiletTypes.PURPLE_CONCRETE),
    BLUE_CONCRETE(PToiletTypes.BLUE_CONCRETE),
    BROWN_CONCRETE(PToiletTypes.BROWN_CONCRETE),
    GREEN_CONCRETE(PToiletTypes.GREEN_CONCRETE),
    RED_CONCRETE(PToiletTypes.RED_CONCRETE),
    BLACK_CONCRETE(PToiletTypes.BLACK_CONCRETE),
    IRON(PToiletTypes.IRON),
    GOLD(PToiletTypes.GOLD),
    COPPER(PToiletTypes.COPPER),
    LAPIS(PToiletTypes.LAPIS),
    REDSTONE(PToiletTypes.REDSTONE),
    QUARTZ(PToiletTypes.QUARTZ),
    DIAMOND(PToiletTypes.DIAMOND),
    EMERALD(PToiletTypes.EMERALD),
    NETHERITE(PToiletTypes.NETHERITE);

    public static final Codec<LavaToiletType> CODEC = StringRepresentable.fromEnum(LavaToiletType::values);

    private final ToiletType toiletType;

    LavaToiletType(ToiletType toiletType) {
        this.toiletType = toiletType;
    }

    public ToiletType getToiletType() {
        return toiletType;
    }

    public ToiletType.Category getCategory() {
        return toiletType.category();
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }

    public static LavaToiletType byToiletType(ToiletType type) {
        for (var v : values()) {
            if (v.toiletType.equals(type)) return v;
        }
        return null;
    }
}