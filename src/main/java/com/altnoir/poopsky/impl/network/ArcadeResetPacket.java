package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import com.altnoir.poopsky.impl.network.PoPayload;
import net.minecraft.server.level.ServerPlayer;
import com.altnoir.poopsky.impl.network.PoPayloadContext;

public record ArcadeResetPacket(BlockPos machinePos) implements PoPayload {
    public static final Type<ArcadeResetPacket> TYPE = new Type<>(PoopSky.loc("arcade_reset"));

    public static final StreamCodec<FriendlyByteBuf, ArcadeResetPacket> STREAM_CODEC =
            StreamCodec.composite(com.altnoir.poopsky.impl.network.codec.ByteBufCodecs.BLOCK_POS, ArcadeResetPacket::machinePos, ArcadeResetPacket::new);


    public static void handle(ArcadeResetPacket payload, PoPayloadContext context) {
        ArcadePacketHandler.handle(context, payload.machinePos(),
                arcade -> arcade.resetGame((ServerPlayer) context.player()));
    }

    @Override
    public Type<ArcadeResetPacket> type() {
        return TYPE;
    }
}