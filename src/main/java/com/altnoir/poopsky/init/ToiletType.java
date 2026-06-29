package com.altnoir.poopsky.init;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public record ToiletType(Block sourceBlock, Category category) {

    private static final Map<String, ToiletType> REGISTRY = new LinkedHashMap<>();

    public static ToiletType register(Block sourceBlock, Category category) {
        var type = new ToiletType(sourceBlock, category);
        REGISTRY.put(type.id(), type);
        return type;
    }

    public String id() {
        return BuiltInRegistries.BLOCK.getKey(sourceBlock).getPath();
    }

    public Component getDisplayName() {
        return Component.translatable("toilet_type.poopsky." + id());
    }

    public String getSerializedName() {
        return id();
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

    public enum Category {
        WOOD, STONE, METAL
    }
}