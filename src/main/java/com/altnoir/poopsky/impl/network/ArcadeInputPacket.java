package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.game.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.ByteBufCodecs;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import com.altnoir.poopsky.impl.network.PoPayload;
import net.minecraft.server.level.ServerPlayer;
import com.altnoir.poopsky.impl.network.PoPayloadContext;

public record ArcadeInputPacket(BlockPos machinePos, Button button, boolean pressed) implements PoPayload {
    public static final Type<ArcadeInputPacket> TYPE = new Type<>(PoopSky.loc("arcade_input"));


    public static final StreamCodec<FriendlyByteBuf, ArcadeInputPacket> STREAM_CODEC =
            StreamCodec.composite(
                    com.altnoir.poopsky.impl.network.codec.ByteBufCodecs.BLOCK_POS,
                    ArcadeInputPacket::machinePos,
                    Button.STREAM_CODEC,
                    ArcadeInputPacket::button,
                    ByteBufCodecs.BOOL,
                    ArcadeInputPacket::pressed,
                    ArcadeInputPacket::new
            );

    public static void handle(ArcadeInputPacket payload, PoPayloadContext context) {
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