package com.altnoir.poopsky.network;

import com.altnoir.poopsky.PoopSky;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RidePacket(int eid, boolean ride) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RidePacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(PoopSky.MOD_ID, "ride_packet"));

    public static final StreamCodec<ByteBuf, RidePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            RidePacket::eid,
            ByteBufCodecs.BOOL,
            RidePacket::ride,
            RidePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
