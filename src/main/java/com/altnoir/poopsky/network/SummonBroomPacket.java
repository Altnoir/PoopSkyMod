package com.altnoir.poopsky.network;

import com.altnoir.poopsky.PoopSky;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SummonBroomPacket() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SummonBroomPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, "summon_broom_packet"));

    public static final StreamCodec<ByteBuf, SummonBroomPacket> STREAM_CODEC = StreamCodec.unit(new SummonBroomPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
