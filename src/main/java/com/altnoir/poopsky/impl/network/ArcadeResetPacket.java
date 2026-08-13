package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ArcadeResetPacket(BlockPos machinePos) implements CustomPacketPayload {
    public static final Type<ArcadeResetPacket> TYPE = new Type<>(PoopSky.loc("arcade_reset"));

    public static final StreamCodec<FriendlyByteBuf, ArcadeResetPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ArcadeResetPacket decode(FriendlyByteBuf buffer) {
            return new ArcadeResetPacket(buffer.readBlockPos());
        }

        @Override
        public void encode(FriendlyByteBuf buffer, ArcadeResetPacket payload) {
            buffer.writeBlockPos(payload.machinePos());
        }
    };

    public static void handle(ArcadeResetPacket payload, @NotNull IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            Level level = player.level();
            if (!level.isLoaded(payload.machinePos())) {
                return;
            }
            if (player.position().distanceToSqr(Vec3.atCenterOf(payload.machinePos())) > 8.0D * 8.0D) {
                return;
            }
            if (level.getBlockEntity(payload.machinePos()) instanceof ArcadeBlockEntity arcade) {
                arcade.resetGame();
            }
        });
    }

    @Override
    public @NotNull Type<ArcadeResetPacket> type() {
        return TYPE;
    }
}