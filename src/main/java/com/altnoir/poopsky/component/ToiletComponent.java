package com.altnoir.poopsky.component;

import com.altnoir.poopsky.block.ToiletBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ToiletComponent(String level1,
                              String level2,
                              int x1,
                              int y1,
                              int z1,
                              int x2,
                              int y2,
                              int z2) {
    public static final Codec<ToiletComponent> BASIC_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("level1").forGetter(ToiletComponent::level1),
                    Codec.STRING.fieldOf("level2").forGetter(ToiletComponent::level2),
                    Codec.INT.fieldOf("x1").forGetter(ToiletComponent::x1),
                    Codec.INT.fieldOf("y1").forGetter(ToiletComponent::y1),
                    Codec.INT.fieldOf("z1").forGetter(ToiletComponent::z1),
                    Codec.INT.fieldOf("x2").forGetter(ToiletComponent::x2),
                    Codec.INT.fieldOf("y2").forGetter(ToiletComponent::y2),
                    Codec.INT.fieldOf("z2").forGetter(ToiletComponent::z2)
            ).apply(instance, ToiletComponent::new)
    );

    public static final StreamCodec<ByteBuf, ToiletComponent> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ToiletComponent decode(ByteBuf buf) {
            String level1 = ByteBufCodecs.STRING_UTF8.decode(buf);
            String level2 = ByteBufCodecs.STRING_UTF8.decode(buf);
            int x1 = buf.readInt();
            int y1 = buf.readInt();
            int z1 = buf.readInt();
            int x2 = buf.readInt();
            int y2 = buf.readInt();
            int z2 = buf.readInt();
            return new ToiletComponent(level1, level2, x1, y1, z1, x2, y2, z2);
        }

        @Override
        public void encode(ByteBuf buf, ToiletComponent value) {
            ByteBufCodecs.STRING_UTF8.encode(buf, value.level1());
            ByteBufCodecs.STRING_UTF8.encode(buf, value.level2());
            buf.writeInt(value.x1());
            buf.writeInt(value.y1());
            buf.writeInt(value.z1());
            buf.writeInt(value.x2());
            buf.writeInt(value.y2());
            buf.writeInt(value.z2());
        }
    };

    public static final ToiletComponent EMPTY = new ToiletComponent("", "", 0, 0, 0, 0, 0, 0);
}
