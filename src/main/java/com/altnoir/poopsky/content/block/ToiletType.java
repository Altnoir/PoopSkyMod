package com.altnoir.poopsky.content.block;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ToiletType implements Comparable<ToiletType> {
    private static final Map<String, ToiletType> REGISTRY = new LinkedHashMap<>();

    private final Supplier<@Nullable Block> blockSupplier;
    private final Category category;
    private final String id;
    private final Component displayName;
    @Nullable
    private final String texture;
    private final float hardness;
    private final boolean isRedstone;
    private final boolean isGolden;
    @Nullable
    private final String nameKey;

    private ToiletType(Supplier<@Nullable Block> blockSupplier, Category category, String id, Component displayName, @Nullable String texture, float hardness, boolean isRedstone, boolean isGolden, @Nullable String nameKey) {
        this.blockSupplier = blockSupplier;
        this.category = category;
        this.id = id;
        this.displayName = displayName;
        this.texture = texture;
        this.hardness = hardness;
        this.isRedstone = isRedstone;
        this.isGolden = isGolden;
        this.nameKey = nameKey;
    }

    /** 直接用方块对象注册（立即解析 id/名称），适合原版方块 */
    public static ToiletType register(Block sourceBlock, Category category) {
        float h = getBlockDestroyTime(sourceBlock);
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(sourceBlock);
        String nameKey = "block." + blockId.getNamespace() + "." + blockId.getPath();
        var type = new ToiletType(() -> sourceBlock, category,
                blockId.getPath(),
                sourceBlock.getName(), null, h, false, false, nameKey);
        REGISTRY.put(type.id, type);
        return type;
    }

    /** 用 Supplier 懒加载方块对象，适合 DeferredRegister 注册的自定义方块 */
    public static ToiletType register(Supplier<Block> sourceBlockSupplier, Category category) {
        Block block = sourceBlockSupplier.get();
        float h = getBlockDestroyTime(block);
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        String nameKey = "block." + blockId.getNamespace() + "." + blockId.getPath();
        var type = new ToiletType(sourceBlockSupplier, category,
                blockId.getPath(),
                block.getName(), null, h, false, false, nameKey);
        REGISTRY.put(type.id, type);
        return type;
    }

    /** 注册一个无 sourceBlock 的类型（如彩虹厕所），后面可以用 .texture() 设置纹理 */
    public static ToiletType register(String id, Category category, Component displayName) {
        var type = new ToiletType(() -> null, category, id, displayName, null, 10.0F, false, false, null);
        REGISTRY.put(id, type);
        return type;
    }

    /** 用新纹理创建一个副本 */
    public ToiletType texture(String texture) {
        var type = new ToiletType(this.blockSupplier, this.category, this.id, this.displayName, texture, this.hardness, this.isRedstone, this.isGolden, this.nameKey);
        REGISTRY.put(this.id, type);
        return type;
    }

    /** 标记为金质厕所（黄金/彩虹等），创建一个副本 */
    public ToiletType golden() {
        var type = new ToiletType(this.blockSupplier, this.category, this.id, this.displayName, this.texture, this.hardness, this.isRedstone, true, this.nameKey);
        REGISTRY.put(this.id, type);
        return type;
    }

    /** 标记为红石厕所，创建一个副本 */
    public ToiletType redstone() {
        var type = new ToiletType(this.blockSupplier, this.category, this.id, this.displayName, this.texture, this.hardness, true, this.isGolden, this.nameKey);
        REGISTRY.put(this.id, type);
        return type;
    }

    /** 标记自定义名称翻译键，创建一个副本 */
    public ToiletType nameKey(String nameKey) {
        var type = new ToiletType(this.blockSupplier, this.category, this.id, this.displayName, this.texture, this.hardness, this.isRedstone, this.isGolden, nameKey);
        REGISTRY.put(this.id, type);
        return type;
    }

    /** 供数据驱动注册使用（ToiletTypeManager 调用） */
    static ToiletType registerFromData(String id, @Nullable Block sourceBlock, Category category, Component displayName, @Nullable String texture, float hardness, boolean isRedstone, boolean isGolden, @Nullable String nameKey) {
        var type = new ToiletType(() -> sourceBlock, category, id, displayName, texture, hardness, isRedstone, isGolden, nameKey);
        REGISTRY.put(id, type);
        return type;
    }

    /** 尝试通过反射读取 Block 的 destroyTime，失败则返回默认值 10 */
    private static float getBlockDestroyTime(Block block) {
        try {
            Field field = BlockBehaviour.class.getDeclaredField("destroyTime");
            field.setAccessible(true);
            return field.getFloat(block);
        } catch (Exception e) {
            return 10.0F;
        }
    }

    public static final Codec<ToiletType> CODEC = Codec.STRING.comapFlatMap(
            id -> {
                ToiletType type = REGISTRY.get(id);
                return type != null ? DataResult.success(type) : DataResult.error(() -> "Unknown toilet type: " + id);
            },
            ToiletType::id
    );

    public static final StreamCodec<ByteBuf, ToiletType> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(
            id -> {
                ToiletType type = byId(id);
                if (type == null) {
                    throw new IllegalStateException("Unknown toilet type: " + id);
                }
                return type;
            },
            ToiletType::id
    );

    // ─── JSON 数据格式的序列化 ───

    public static final Codec<Category> CATEGORY_CODEC = Codec.STRING.xmap(
            s -> switch (s) {
                case "wood" -> Category.WOOD;
                case "hard" -> Category.HARD;
                default -> Category.HARD;
            },
            c -> c == Category.WOOD ? "wood" : "hard"
    );

    /**
     * 从 JsonElement 解析一个 ToiletType 并注册到 REGISTRY
     */
    public static void parseAndRegister(String id, JsonElement json) {
        var obj = json.getAsJsonObject();

        // source_block (可选)
        Block sourceBlock = null;
        if (obj.has("source_block") && !obj.get("source_block").isJsonNull()) {
            String blockId = obj.get("source_block").getAsString();
            sourceBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockId));
        }

        // category (可选，默认 "wood")
        Category category = Category.WOOD;
        if (obj.has("category")) {
            category = CATEGORY_CODEC.parse(JsonOps.INSTANCE, obj.get("category")).getOrThrow();
        }

        // display_name (可选 - 有 source_block 则自动读取其名称，否则用 id)
        Component displayName;
        if (obj.has("display_name")) {
            displayName = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, obj.get("display_name")).getOrThrow();
        } else if (sourceBlock != null) {
            displayName = sourceBlock.getName();
        } else {
            displayName = Component.literal(id);
        }

        // texture (可选)
        String texture = obj.has("texture") && !obj.get("texture").isJsonNull() ? obj.get("texture").getAsString() : null;

        // hardness (可选 - 如果有 source_block 则默认读取其 destroyTime，否则 10)
        float hardness;
        if (obj.has("hardness")) {
            hardness = obj.get("hardness").getAsFloat();
        } else if (sourceBlock != null) {
            hardness = getBlockDestroyTime(sourceBlock);
        } else {
            hardness = 10.0F;
        }

        // is_redstone (可选，默认 false)
        boolean isRedstone = obj.has("is_redstone") && obj.get("is_redstone").getAsBoolean();

        // is_golden (可选，默认 false)
        boolean isGolden = obj.has("is_golden") && obj.get("is_golden").getAsBoolean();

        // name_key (可选 - 未指定则默认用源方块的翻译键)
        String nameKey;
        if (obj.has("name_key") && !obj.get("name_key").isJsonNull()) {
            nameKey = obj.get("name_key").getAsString();
        } else if (sourceBlock != null) {
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(sourceBlock);
            nameKey = "block." + blockId.getNamespace() + "." + blockId.getPath();
        } else {
            nameKey = null;
        }

        registerFromData(id, sourceBlock, category, displayName, texture, hardness, isRedstone, isGolden, nameKey);
    }

    public static ToiletType byId(String id) {
        return REGISTRY.get(id);
    }

    public static ToiletType bySourceBlock(Block block) {
        String id = BuiltInRegistries.BLOCK.getKey(block).getPath();
        return REGISTRY.get(id);
    }

    public static Map<String, ToiletType> getAll() {
        return Collections.unmodifiableMap(REGISTRY);
    }

    public static Map<String, ToiletType> getByCategory(Category category) {
        return REGISTRY.entrySet().stream()
                .filter(e -> e.getValue().category == category)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    public static int getIndex(ToiletType type) {
        int i = 0;
        for (var e : REGISTRY.entrySet()) {
            if (e.getValue().equals(type)) return i;
            i++;
        }
        return 0;
    }

    // ─── Accessors ───

    /** 懒获取 sourceBlock，调用时才会从 Supplier 解析 */
    @Nullable
    public Block sourceBlock() {
        return blockSupplier.get();
    }

    public Category category() {
        return category;
    }

    public String id() {
        return id;
    }

    public Component displayName() {
        return displayName;
    }

    @Nullable
    public String texture() {
        return texture;
    }

    public float hardness() {
        return hardness;
    }

    public boolean isRedstone() {
        return isRedstone;
    }

    public boolean isGolden() {
        return isGolden;
    }

    @Nullable
    public String nameKey() {
        return nameKey;
    }

    // ─── Object overrides ───

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ToiletType that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "ToiletType[" + id + "]";
    }

    @Override
    public int compareTo(ToiletType other) {
        return Integer.compare(getIndex(this), getIndex(other));
    }

    public enum Category {
        WOOD, HARD
    }
}