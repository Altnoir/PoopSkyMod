package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlayAnimationPayload(PoAnimation animation) implements CustomPacketPayload {
    public static final Type<PlayAnimationPayload> TYPE = new Type<>(PoopSky.loc("play_animation"));
    public static final StreamCodec<FriendlyByteBuf, PlayAnimationPayload> CODEC = StreamCodec.of(
            PlayAnimationPayload::encode,
            PlayAnimationPayload::decode
    );

    public static void handle(PlayAnimationPayload payload, IPayloadContext context) {
        context.enqueueWork(() ->
                com.altnoir.poopsky.client.PoAnimationController.play(payload.animation())
        );
    }

    private static void encode(FriendlyByteBuf buffer, PlayAnimationPayload payload) {
        buffer.writeByte(payload.animation().ordinal());
    }

    private static PlayAnimationPayload decode(FriendlyByteBuf buffer) {
        return new PlayAnimationPayload(PoAnimation.byId(buffer.readUnsignedByte()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
