package com.altnoir.poopsky.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ToiletComponent(String level1, String level2, int x1, int y1, int z1, int x2, int y2, int z2) {
    public static final ToiletComponent EMPTY = new ToiletComponent("", "", 0, 0, 0, 0, 0, 0);

    public static final Codec<ToiletComponent> BASIC_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("level1").forGetter(ToiletComponent::level1),
            Codec.STRING.fieldOf("level2").forGetter(ToiletComponent::level2),
            Codec.INT.fieldOf("x1").forGetter(ToiletComponent::x1),
            Codec.INT.fieldOf("y1").forGetter(ToiletComponent::y1),
            Codec.INT.fieldOf("z1").forGetter(ToiletComponent::z1),
            Codec.INT.fieldOf("x2").forGetter(ToiletComponent::x2),
            Codec.INT.fieldOf("y2").forGetter(ToiletComponent::y2),
            Codec.INT.fieldOf("z2").forGetter(ToiletComponent::z2)
    ).apply(instance, ToiletComponent::new));

    public static final StreamCodec<ByteBuf, ToiletComponent> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ToiletComponent decode(ByteBuf buf) {
            return ToiletComponent.decode(buf);
        }

        @Override
        public void encode(ByteBuf buf, ToiletComponent value) {
            value.encode(buf);
        }
    };

    private static ToiletComponent decode(ByteBuf buf) {
        return new ToiletComponent(
                ByteBufCodecs.STRING_UTF8.decode(buf),
                ByteBufCodecs.STRING_UTF8.decode(buf),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
        );
    }

    private void encode(ByteBuf buf) {
        ByteBufCodecs.STRING_UTF8.encode(buf, level1);
        ByteBufCodecs.STRING_UTF8.encode(buf, level2);
        buf.writeInt(x1);
        buf.writeInt(y1);
        buf.writeInt(z1);
        buf.writeInt(x2);
        buf.writeInt(y2);
        buf.writeInt(z2);
    }
}