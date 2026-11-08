package com.altnoir.poopsky.impl.network.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class ByteBufCodecs {
    private ByteBufCodecs() {}
    private static FriendlyByteBuf friendly(ByteBuf buffer) {
        return buffer instanceof FriendlyByteBuf f ? f : new FriendlyByteBuf(buffer);
    }
    public static final StreamCodec<ByteBuf, String> STRING_UTF8 = StreamCodec.of(
            (b, v) -> friendly(b).writeUtf(v), b -> friendly(b).readUtf());
    public static final StreamCodec<ByteBuf, Integer> VAR_INT = StreamCodec.of(
            (b, v) -> friendly(b).writeVarInt(v), b -> friendly(b).readVarInt());
    public static final StreamCodec<ByteBuf, Integer> INT = StreamCodec.of(ByteBuf::writeInt, ByteBuf::readInt);
    public static final StreamCodec<ByteBuf, Float> FLOAT = StreamCodec.of(ByteBuf::writeFloat, ByteBuf::readFloat);
    public static final StreamCodec<ByteBuf, Boolean> BOOL = StreamCodec.of(ByteBuf::writeBoolean, ByteBuf::readBoolean);
    public static final StreamCodec<FriendlyByteBuf, BlockPos> BLOCK_POS = StreamCodec.of(FriendlyByteBuf::writeBlockPos, FriendlyByteBuf::readBlockPos);
    public static final StreamCodec<FriendlyByteBuf, ResourceLocation> RESOURCE_LOCATION = StreamCodec.of(FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::readResourceLocation);
    public static final StreamCodec<FriendlyByteBuf, ItemStack> ITEM_STACK = StreamCodec.of(FriendlyByteBuf::writeItem, FriendlyByteBuf::readItem);
    public static final StreamCodec<FriendlyByteBuf, Ingredient> INGREDIENT = StreamCodec.of((b, v) -> v.toNetwork(b), Ingredient::fromNetwork);

    @SuppressWarnings("unchecked")
    public static <T> StreamCodec<FriendlyByteBuf, T> registry(ResourceKey<? extends Registry<T>> key) {
        Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(key.location());
        if (registry == null) throw new IllegalArgumentException("Unknown registry: " + key.location());
        return StreamCodec.of((b, value) -> {
            ResourceLocation id = registry.getKey(value);
            if (id == null) throw new IllegalArgumentException("Unregistered value: " + value);
            b.writeResourceLocation(id);
        }, b -> {
            ResourceLocation id = b.readResourceLocation();
            if (!registry.containsKey(id)) throw new IllegalArgumentException("Unknown registry value: " + id);
            return registry.get(id);
        });
    }

    public static <B extends ByteBuf, T> Function<StreamCodec<B, T>, StreamCodec<B, List<T>>> list() {
        return codec -> StreamCodec.of((b, values) -> {
            if (values.size() > 65536) throw new IllegalArgumentException("List too large");
            friendly(b).writeVarInt(values.size());
            values.forEach(v -> codec.encode(b, v));
        }, b -> {
            int count = friendly(b).readVarInt();
            if (count < 0 || count > 65536) throw new IllegalArgumentException("Invalid list size: " + count);
            List<T> result = new ArrayList<>(count);
            for (int i = 0; i < count; i++) result.add(codec.decode(b));
            return result;
        });
    }
}
