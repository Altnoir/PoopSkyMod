package com.altnoir.poopsky.init;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.ToiletComponent;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import org.jetbrains.annotations.Nullable;

/** 1.20.1 用物品 NBT 保存自定义状态；原版物品同步和合成复制会保留这些字段。 */
public final class PoComponents {
    public static final Key<ToiletComponent> TOILET_COMPONENT = new Key<>("toilet_component", ToiletComponent.BASIC_CODEC);
    public static final Key<String> FLY_TYPE = new Key<>("fly_type", Codec.STRING);
    public static final Key<ToiletType> TOILET_TYPE = new Key<>("toilet_type", ToiletType.CODEC);
    public static final Key<String> GACHAPON_COLOR = new Key<>("gachapon_color", Codec.STRING);
    public static final Key<String> GACHAPON_MOB = new Key<>("gachapon_mob", Codec.STRING);
    private PoComponents() {}
    public static void register(IEventBus bus) {}

    public record Key<T>(String name, Codec<T> codec) {
        public String tagName() { return "poopsky:" + name; }
        public Key<T> get() { return this; }
        public boolean has(ItemStack stack) { return stack.hasTag() && stack.getTag().contains(tagName()); }
        @Nullable
        public T get(ItemStack stack) {
            if (!has(stack)) return null;
            return codec.parse(NbtOps.INSTANCE, stack.getTag().get(tagName()))
                    .resultOrPartial(error -> PoopSky.LOGGER.warn("Invalid {} item data: {}", tagName(), error)).orElse(null);
        }
        public T getOrDefault(ItemStack stack, T fallback) {
            T value = get(stack);
            return value == null ? fallback : value;
        }
        public void set(ItemStack stack, T value) {
            if (value == null) { stack.removeTagKey(tagName()); return; }
            var encoded = codec.encodeStart(NbtOps.INSTANCE, value)
                    .getOrThrow(false, error -> PoopSky.LOGGER.error("Unable to save {}: {}", tagName(), error));
            stack.getOrCreateTag().put(tagName(), encoded);
        }
    }
}
