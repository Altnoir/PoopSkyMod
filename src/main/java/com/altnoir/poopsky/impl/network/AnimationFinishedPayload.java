package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.util.ToiletUtil;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import com.altnoir.poopsky.impl.network.PoPayload;
import net.minecraft.server.level.ServerPlayer;
import com.altnoir.poopsky.impl.network.PoPayloadContext;

public record AnimationFinishedPayload(PoAnimation animation) implements PoPayload {
    public static final Type<AnimationFinishedPayload> TYPE =
            new Type<>(PoopSky.loc("animation_finished"));
    public static final StreamCodec<FriendlyByteBuf, AnimationFinishedPayload> CODEC = StreamCodec.of(
            AnimationFinishedPayload::encode,
            AnimationFinishedPayload::decode
    );

    public static void handle(AnimationFinishedPayload payload, PoPayloadContext context) {
        context.enqueueWork(() -> {
            if (payload.animation() == PoAnimation.POEM && context.player() instanceof ServerPlayer player) {
                ToiletUtil.finishPendingEndToiletTeleport(player);
            }
        });
    }

    private static void encode(FriendlyByteBuf buffer, AnimationFinishedPayload payload) {
        buffer.writeByte(payload.animation().ordinal());
    }

    private static AnimationFinishedPayload decode(FriendlyByteBuf buffer) {
        return new AnimationFinishedPayload(PoAnimation.byId(buffer.readUnsignedByte()));
    }

    @Override
    public Type<? extends PoPayload> type() {
        return TYPE;
    }
}
