package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.PoAnimationController;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import com.altnoir.poopsky.impl.network.PoPayload;
import com.altnoir.poopsky.impl.network.PoPayloadContext;

public record PlayAnimationPayload(PoAnimation animation) implements PoPayload {
    public static final Type<PlayAnimationPayload> TYPE = new Type<>(PoopSky.loc("play_animation"));
    public static final StreamCodec<FriendlyByteBuf, PlayAnimationPayload> CODEC = StreamCodec.of(
            PlayAnimationPayload::encode,
            PlayAnimationPayload::decode
    );

    public static void handle(PlayAnimationPayload payload, PoPayloadContext context) {
        context.enqueueWork(() -> PoAnimationController.play(payload.animation()));
    }

    private static void encode(FriendlyByteBuf buffer, PlayAnimationPayload payload) {
        buffer.writeByte(payload.animation().ordinal());
    }

    private static PlayAnimationPayload decode(FriendlyByteBuf buffer) {
        return new PlayAnimationPayload(PoAnimation.byId(buffer.readUnsignedByte()));
    }

    @Override
    public Type<? extends PoPayload> type() {
        return TYPE;
    }
}
