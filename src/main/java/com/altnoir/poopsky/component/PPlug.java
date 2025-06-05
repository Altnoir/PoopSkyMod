package com.altnoir.poopsky.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PPlug(boolean controlMode) {
    public static final Codec<PPlug> PLUG_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("conrtolMode").forGetter(PPlug::controlMode)
            ).apply(instance, PPlug::new)
    );

    public static final StreamCodec<ByteBuf, PPlug> PLUG_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PPlug::controlMode,
            PPlug::new
    );
}
