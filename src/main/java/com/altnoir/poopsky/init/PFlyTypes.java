package com.altnoir.poopsky.init;

import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 苍蝇品种枚举。
 * 只有一种苍蝇实体对应所有品种，品种由 DataComponent 区分。
 * 翻译写在语言文件中，键为 fly_type.poopsky.&lt;id&gt;。
 */
public class PFlyTypes {
    private static final Map<String, FlyType> BY_ID = new LinkedHashMap<>();

    // ——— 基础品种（可通过普通手段获得） ———
    public static final FlyType NORMAL = register("normal");
    public static final FlyType WHITE = register("white");
    public static final FlyType BLACK = register("black");
    public static final FlyType GREEN = register("green");
    public static final FlyType YELLOW = register("yellow");
    public static final FlyType BLUE = register("blue");
    public static final FlyType RED = register("red");
    public static final FlyType BROWN = register("brown");

    // ——— 只能变异获得 ———
    public static final FlyType GRAY = register("gray");
    public static final FlyType LIGHT_GRAY = register("light_gray");
    public static final FlyType LIGHT_BLUE = register("light_blue");
    public static final FlyType LIME = register("lime");
    public static final FlyType MAGENTA = register("magenta");
    public static final FlyType CYAN = register("cyan");
    public static final FlyType PINK = register("pink");
    public static final FlyType ORANGE = register("orange");
    public static final FlyType PURPLE = register("purple");

    public static Map<String, FlyType> getAll() {
        return Collections.unmodifiableMap(BY_ID);
    }

    public static int getIndex(FlyType type) { int i = 0; for (var e : BY_ID.entrySet()) { if (e.getValue().equals(type)) return i; i++; } return 0; }

    public static FlyType byId(String id) {
        return BY_ID.getOrDefault(id, NORMAL);
    }

    private static FlyType register(String id) {
        var type = new FlyType(id);
        BY_ID.put(id, type);
        return type;
    }

    public record FlyType(String id) {
        public Component getDisplayName() {
            return Component.translatable("fly_type.poopsky." + id);
        }

        public String getSerializedName() {
            return id;
        }
    }
}
