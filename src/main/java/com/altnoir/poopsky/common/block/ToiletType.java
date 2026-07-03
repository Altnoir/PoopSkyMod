package com.altnoir.poopsky.common.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

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

    private ToiletType(Supplier<@Nullable Block> blockSupplier, Category category, String id, Component displayName, @Nullable String texture) {
        this.blockSupplier = blockSupplier;
        this.category = category;
        this.id = id;
        this.displayName = displayName;
        this.texture = texture;
    }

    /** 直接用方块对象注册（立即解析 id/名称），适合原版方块 */
    public static ToiletType register(Block sourceBlock, Category category) {
        var type = new ToiletType(() -> sourceBlock, category,
                BuiltInRegistries.BLOCK.getKey(sourceBlock).getPath(),
                sourceBlock.getName(), null);
        REGISTRY.put(type.id, type);
        return type;
    }

    /** 用 Supplier 懒加载方块对象，适合 DeferredRegister 注册的自定义方块 */
    public static ToiletType register(Supplier<Block> sourceBlockSupplier, Category category) {
        Block block = sourceBlockSupplier.get();
        var type = new ToiletType(sourceBlockSupplier::get, category,
                BuiltInRegistries.BLOCK.getKey(block).getPath(),
                block.getName(), null);
        REGISTRY.put(type.id, type);
        return type;
    }

    /** 注册一个无 sourceBlock 的类型（如彩虹厕所），后面可以用 .texture() 设置纹理 */
    public static ToiletType register(String id, Category category, Component displayName) {
        var type = new ToiletType(() -> null, category, id, displayName, null);
        REGISTRY.put(id, type);
        return type;
    }

    /** 用新纹理创建一个副本 */
    public ToiletType texture(String texture) {
        var type = new ToiletType(this.blockSupplier, this.category, this.id, this.displayName, texture);
        REGISTRY.put(this.id, type);
        return type;
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