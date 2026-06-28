package com.altnoir.poopsky.init;

import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PToiletTypes {
    private static final Map<String, ToiletType> BY_ID = new LinkedHashMap<>();

    // ——— 木质马桶 ———
    public static final ToiletType OAK = register("oak", Category.WOOD);
    public static final ToiletType SPRUCE = register("spruce", Category.WOOD);
    public static final ToiletType BIRCH = register("birch", Category.WOOD);
    public static final ToiletType JUNGLE = register("jungle", Category.WOOD);
    public static final ToiletType ACACIA = register("acacia", Category.WOOD);
    public static final ToiletType CHERRY = register("cherry", Category.WOOD);
    public static final ToiletType DARK_OAK = register("dark_oak", Category.WOOD);
    public static final ToiletType MANGROVE = register("mangrove", Category.WOOD);
    public static final ToiletType BAMBOO = register("bamboo", Category.WOOD);
    public static final ToiletType CRIMSON = register("crimson", Category.WOOD);
    public static final ToiletType WARPED = register("warped", Category.WOOD);

    // ——— 石质马桶 ———
    public static final ToiletType STONE = register("stone", Category.STONE);
    public static final ToiletType COBBLESTONE = register("cobblestone", Category.STONE);
    public static final ToiletType MOSSY_COBBLESTONE = register("mossy_cobblestone", Category.STONE);
    public static final ToiletType SMOOTH_STONE = register("smooth_stone", Category.STONE);
    public static final ToiletType STONE_BRICK = register("stone_brick", Category.STONE);
    public static final ToiletType MOSSY_STONE_BRICK = register("mossy_stone_brick", Category.STONE);
    public static final ToiletType TILE = register("tile", Category.STONE);
    public static final ToiletType WHITE_CONCRETE = register("white_concrete", Category.STONE);
    public static final ToiletType ORANGE_CONCRETE = register("orange_concrete", Category.STONE);
    public static final ToiletType MAGENTA_CONCRETE = register("magenta_concrete", Category.STONE);
    public static final ToiletType LIGHT_BLUE_CONCRETE = register("light_blue_concrete", Category.STONE);
    public static final ToiletType YELLOW_CONCRETE = register("yellow_concrete", Category.STONE);
    public static final ToiletType LIME_CONCRETE = register("lime_concrete", Category.STONE);
    public static final ToiletType PINK_CONCRETE = register("pink_concrete", Category.STONE);
    public static final ToiletType GRAY_CONCRETE = register("gray_concrete", Category.STONE);
    public static final ToiletType LIGHT_GRAY_CONCRETE = register("light_gray_concrete", Category.STONE);
    public static final ToiletType CYAN_CONCRETE = register("cyan_concrete", Category.STONE);
    public static final ToiletType PURPLE_CONCRETE = register("purple_concrete", Category.STONE);
    public static final ToiletType BLUE_CONCRETE = register("blue_concrete", Category.STONE);
    public static final ToiletType BROWN_CONCRETE = register("brown_concrete", Category.STONE);
    public static final ToiletType GREEN_CONCRETE = register("green_concrete", Category.STONE);
    public static final ToiletType RED_CONCRETE = register("red_concrete", Category.STONE);
    public static final ToiletType BLACK_CONCRETE = register("black_concrete", Category.STONE);

    // ——— 金属马桶 ———
    public static final ToiletType IRON = register("iron", Category.METAL);
    public static final ToiletType GOLD = register("gold", Category.METAL);
    public static final ToiletType COPPER = register("copper", Category.METAL);
    public static final ToiletType NETHERITE = register("netherite", Category.METAL);
    public static final ToiletType RAW_IRON = register("raw_iron", Category.METAL);
    public static final ToiletType RAW_GOLD = register("raw_gold", Category.METAL);
    public static final ToiletType RAW_COPPER = register("raw_copper", Category.METAL);
    public static final ToiletType DIAMOND = register("diamond", Category.METAL);
    public static final ToiletType EMERALD = register("emerald", Category.METAL);
    public static final ToiletType LAPIS = register("lapis", Category.METAL);
    public static final ToiletType REDSTONE = register("redstone", Category.METAL);
    public static final ToiletType QUARTZ = register("quartz", Category.METAL);

    public static Map<String, ToiletType> getAll() {
        return Collections.unmodifiableMap(BY_ID);
    }

    public static Map<String, ToiletType> getByCategory(Category category) {
        return BY_ID.entrySet().stream()
                .filter(e -> e.getValue().category() == category)
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static int getIndex(ToiletType type) {
        int i = 0;
        for (var e : BY_ID.entrySet()) {
            if (e.getValue().equals(type)) return i;
            i++;
        }
        return 0;
    }

    public static ToiletType byId(String id) {
        return BY_ID.getOrDefault(id, OAK);
    }

    private static ToiletType register(String id, Category category) {
        var type = new ToiletType(id, category);
        BY_ID.put(id, type);
        return type;
    }

    public enum Category {
        WOOD, STONE, METAL
    }

    public record ToiletType(String id, Category category) {
        public Component getDisplayName() {
            return Component.translatable("toilet_type.poopsky." + id);
        }

        public String getSerializedName() {
            return id;
        }
    }
}