package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record LightArcadeBlockUpdatePacket(BlockPos machinePos, String game, int score)
        implements CustomPacketPayload {
    public static final Type<LightArcadeBlockUpdatePacket> TYPE = new Type<>(PoopSky.loc("light_arcade_update"));

    public static final StreamCodec<FriendlyByteBuf, LightArcadeBlockUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, LightArcadeBlockUpdatePacket::machinePos,
            ByteBufCodecs.STRING_UTF8, LightArcadeBlockUpdatePacket::game,
            ByteBufCodecs.INT, LightArcadeBlockUpdatePacket::score,
            LightArcadeBlockUpdatePacket::new
    );

    public static void handle(LightArcadeBlockUpdatePacket payload, @NotNull IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            ServerLevel level = player.serverLevel();

            if (!level.isLoaded(payload.machinePos())) {
                return;
            }

            if (!(level.getBlockEntity(payload.machinePos()) instanceof ArcadeBlockEntity arcade)) {
                return;
            }

            arcade.settleGame(player, payload.game(), payload.score());
        });
    }

    @Override
    public @NotNull Type<LightArcadeBlockUpdatePacket> type() {
        return TYPE;
    }
}
