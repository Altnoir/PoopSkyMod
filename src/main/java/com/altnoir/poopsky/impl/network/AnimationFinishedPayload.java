package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.util.ToiletUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record AnimationFinishedPayload(PoAnimation animation) implements CustomPacketPayload {
    public static final Type<AnimationFinishedPayload> TYPE =
            new Type<>(PoopSky.loc("animation_finished"));
    public static final StreamCodec<FriendlyByteBuf, AnimationFinishedPayload> CODEC = StreamCodec.of(
            AnimationFinishedPayload::encode,
            AnimationFinishedPayload::decode
    );

    public static void handle(AnimationFinishedPayload payload, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        context.server().execute(() -> {
            if (payload.animation() == PoAnimation.POEM) ToiletUtil.finishPendingEndToiletTeleport(player);
        });
    }

    private static void encode(FriendlyByteBuf buffer, AnimationFinishedPayload payload) {
        buffer.writeByte(payload.animation().ordinal());
    }

    private static AnimationFinishedPayload decode(FriendlyByteBuf buffer) {
        return new AnimationFinishedPayload(PoAnimation.byId(buffer.readUnsignedByte()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
