package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.game.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ArcadeInputPacket(BlockPos machinePos, Button button, boolean pressed) implements CustomPacketPayload {
    public static final Type<ArcadeInputPacket> TYPE = new Type<>(PoopSky.loc("arcade_input"));


    public static final StreamCodec<FriendlyByteBuf, ArcadeInputPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    ArcadeInputPacket::machinePos,
                    Button.STREAM_CODEC,
                    ArcadeInputPacket::button,
                    ByteBufCodecs.BOOL,
                    ArcadeInputPacket::pressed,
                    ArcadeInputPacket::new
            );

    public static void handle(ArcadeInputPacket payload, IPayloadContext context) {
        ArcadePacketHandler.handle(
                context,
                payload.machinePos(),
                arcade -> arcade.handleInput(
                        (ServerPlayer) context.player(),
                        payload.button(),
                        payload.pressed()
                )
        );
    }

    @Override
    public Type<ArcadeInputPacket> type() {
        return TYPE;
    }
}