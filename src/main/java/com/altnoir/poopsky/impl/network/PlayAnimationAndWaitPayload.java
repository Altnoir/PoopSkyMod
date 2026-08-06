package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.PoAnimationController;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PlayAnimationAndWaitPayload(PoAnimation animation) implements CustomPacketPayload {
    public static final Type<PlayAnimationAndWaitPayload> TYPE =
            new Type<>(PoopSky.loc("play_animation_and_wait"));
    public static final StreamCodec<FriendlyByteBuf, PlayAnimationAndWaitPayload> CODEC = StreamCodec.of(
            PlayAnimationAndWaitPayload::encode,
            PlayAnimationAndWaitPayload::decode
    );

    public static void handle(PlayAnimationAndWaitPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> PoAnimationController.play(
                payload.animation(),
                () -> ClientPlayNetworking.send(new AnimationFinishedPayload(payload.animation()))
        ));
    }

    private static void encode(FriendlyByteBuf buffer, PlayAnimationAndWaitPayload payload) {
        buffer.writeByte(payload.animation().ordinal());
    }

    private static PlayAnimationAndWaitPayload decode(FriendlyByteBuf buffer) {
        return new PlayAnimationAndWaitPayload(PoAnimation.byId(buffer.readUnsignedByte()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
