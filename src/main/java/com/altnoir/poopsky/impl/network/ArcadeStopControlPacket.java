package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import com.altnoir.poopsky.impl.network.PoPayload;
import net.minecraft.server.level.ServerPlayer;
import com.altnoir.poopsky.impl.network.PoPayloadContext;

public record ArcadeStopControlPacket(BlockPos machinePos) implements PoPayload {
    public static final Type<ArcadeStopControlPacket> TYPE = new Type<>(PoopSky.loc("arcade_stop_control"));

    public static final StreamCodec<FriendlyByteBuf, ArcadeStopControlPacket> STREAM_CODEC =
            StreamCodec.composite(com.altnoir.poopsky.impl.network.codec.ByteBufCodecs.BLOCK_POS, ArcadeStopControlPacket::machinePos, ArcadeStopControlPacket::new);


    public static void handle(ArcadeStopControlPacket payload, PoPayloadContext context) {
        ArcadePacketHandler.handle(context, payload.machinePos(),
                arcade -> arcade.stopControl((ServerPlayer) context.player()));
    }

    @Override
    public Type<ArcadeStopControlPacket> type() {
        return TYPE;
    }
}
