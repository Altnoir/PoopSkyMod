package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ArcadeStopControlPacket(BlockPos machinePos) implements CustomPacketPayload {
    public static final Type<ArcadeStopControlPacket> TYPE = new Type<>(PoopSky.loc("arcade_stop_control"));

    public static final StreamCodec<FriendlyByteBuf, ArcadeStopControlPacket> STREAM_CODEC =
            StreamCodec.composite(BlockPos.STREAM_CODEC, ArcadeStopControlPacket::machinePos, ArcadeStopControlPacket::new);


    public static void handle(ArcadeStopControlPacket payload, IPayloadContext context) {
        ArcadePacketHandler.handle(context, payload.machinePos(),
                arcade -> arcade.stopControl((ServerPlayer) context.player()));
    }

    @Override
    public Type<ArcadeStopControlPacket> type() {
        return TYPE;
    }
}
