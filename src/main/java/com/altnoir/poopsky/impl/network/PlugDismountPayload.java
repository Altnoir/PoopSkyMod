package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.entity.p.ToiletPlugEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record PlugDismountPayload() implements CustomPacketPayload  {
    public static final CustomPacketPayload.Type<PlugDismountPayload> TYPE = new CustomPacketPayload.Type<>(PoopSky.loc("dismount_plug"));
    public static final StreamCodec<FriendlyByteBuf, PlugDismountPayload> CODEC = StreamCodec.unit(new PlugDismountPayload());

    public static void handle(final PlugDismountPayload payload, final ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            ServerPlayer player = (ServerPlayer) context.player();

            if (player.getVehicle() instanceof ToiletPlugEntity) {
                player.stopRiding();
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
