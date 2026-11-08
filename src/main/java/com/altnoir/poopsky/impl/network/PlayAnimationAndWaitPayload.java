package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.PoAnimationController;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import com.altnoir.poopsky.impl.network.PoPayload;
import com.altnoir.poopsky.impl.network.PoNetworking;
import com.altnoir.poopsky.impl.network.PoPayloadContext;

public record PlayAnimationAndWaitPayload(PoAnimation animation) implements PoPayload {
    public static final Type<PlayAnimationAndWaitPayload> TYPE =
            new Type<>(PoopSky.loc("play_animation_and_wait"));
    public static final StreamCodec<FriendlyByteBuf, PlayAnimationAndWaitPayload> CODEC = StreamCodec.of(
            PlayAnimationAndWaitPayload::encode,
            PlayAnimationAndWaitPayload::decode
    );

    public static void handle(PlayAnimationAndWaitPayload payload, PoPayloadContext context) {
        context.enqueueWork(() -> PoAnimationController.play(
                payload.animation(),
                () -> PoNetworking.sendToServer(new AnimationFinishedPayload(payload.animation()))
        ));
    }

    private static void encode(FriendlyByteBuf buffer, PlayAnimationAndWaitPayload payload) {
        buffer.writeByte(payload.animation().ordinal());
    }

    private static PlayAnimationAndWaitPayload decode(FriendlyByteBuf buffer) {
        return new PlayAnimationAndWaitPayload(PoAnimation.byId(buffer.readUnsignedByte()));
    }

    @Override
    public Type<? extends PoPayload> type() {
        return TYPE;
    }
}
