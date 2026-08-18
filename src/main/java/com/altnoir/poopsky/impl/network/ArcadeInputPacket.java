package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import com.altnoir.poopsky.game.Button;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ArcadeInputPacket(BlockPos machinePos, Button button, boolean pressed) implements CustomPacketPayload {
    public static final Type<ArcadeInputPacket> TYPE = new Type<>(PoopSky.loc("arcade_input"));

    public static final StreamCodec<FriendlyByteBuf, ArcadeInputPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ArcadeInputPacket decode(FriendlyByteBuf buffer) {
            return new ArcadeInputPacket(
                    buffer.readBlockPos(),
                    Button.valueOf(buffer.readUtf()),
                    buffer.readBoolean()
            );
        }

        @Override
        public void encode(FriendlyByteBuf buffer, ArcadeInputPacket payload) {
            buffer.writeBlockPos(payload.machinePos());
            buffer.writeUtf(payload.button().name());
            buffer.writeBoolean(payload.pressed());
        }
    };

    public static void handle(ArcadeInputPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (ArcadeAccess.canAccess(player, payload.machinePos())) {
                return;
            }
            if (player.level().getBlockEntity(payload.machinePos()) instanceof ArcadeBlockEntity arcade) {
                arcade.handleInput(player, payload.button(), payload.pressed());
            }
        });
    }

    @Override
    public Type<ArcadeInputPacket> type() {
        return TYPE;
    }
}