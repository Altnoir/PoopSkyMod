package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.entity.p.ToiletPlugEntity;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import com.altnoir.poopsky.impl.network.PoPayload;
import net.minecraft.server.level.ServerPlayer;
import com.altnoir.poopsky.impl.network.PoPayloadContext;

public record PlugDismountPayload() implements PoPayload  {
    public static final PoPayload.Type<PlugDismountPayload> TYPE = new PoPayload.Type<>(PoopSky.loc("dismount_plug"));
    public static final StreamCodec<FriendlyByteBuf, PlugDismountPayload> CODEC = StreamCodec.unit(new PlugDismountPayload());

    public static void handle(final PlugDismountPayload payload, final PoPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();

            if (player.getVehicle() instanceof ToiletPlugEntity) {
                player.stopRiding();
            }
        });
    }

    @Override
    public Type<? extends PoPayload> type() {
        return TYPE;
    }
}
