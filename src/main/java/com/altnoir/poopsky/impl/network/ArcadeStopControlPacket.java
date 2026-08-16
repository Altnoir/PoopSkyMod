package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ArcadeStopControlPacket(BlockPos machinePos) implements CustomPacketPayload {
    public static final Type<ArcadeStopControlPacket> TYPE = new Type<>(PoopSky.loc("arcade_stop_control"));

    public static final StreamCodec<FriendlyByteBuf, ArcadeStopControlPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ArcadeStopControlPacket decode(FriendlyByteBuf buffer) {
            return new ArcadeStopControlPacket(buffer.readBlockPos());
        }

        @Override
        public void encode(FriendlyByteBuf buffer, ArcadeStopControlPacket payload) {
            buffer.writeBlockPos(payload.machinePos());
        }
    };

    public static void handle(ArcadeStopControlPacket payload, @NotNull IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            if (!ArcadeAccess.canAccess(player, payload.machinePos())) {
                return;
            }
            if (player.level().getBlockEntity(payload.machinePos()) instanceof ArcadeBlockEntity arcade) {
                arcade.stopControl(player);
            }
        });
    }

    @Override
    public @NotNull Type<ArcadeStopControlPacket> type() {
        return TYPE;
    }
}
