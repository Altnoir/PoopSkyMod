package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ArcadeResetPacket(BlockPos machinePos) implements CustomPacketPayload {
    public static final Type<ArcadeResetPacket> TYPE = new Type<>(PoopSky.loc("arcade_reset"));

    public static final StreamCodec<FriendlyByteBuf, ArcadeResetPacket> STREAM_CODEC =
            StreamCodec.composite(BlockPos.STREAM_CODEC, ArcadeResetPacket::machinePos, ArcadeResetPacket::new);


    public static void handle(ArcadeResetPacket payload, IPayloadContext context) {
        ArcadePacketHandler.handle(context, payload.machinePos(),
                arcade -> arcade.resetGame((ServerPlayer) context.player()));
    }

    @Override
    public Type<ArcadeResetPacket> type() {
        return TYPE;
    }
}