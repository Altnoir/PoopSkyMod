package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.IntroController;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record IntroPayload() implements CustomPacketPayload {
    public static final IntroPayload INSTANCE = new IntroPayload();
    public static final Type<IntroPayload> TYPE = new Type<>(PoopSky.loc("poopsky_intro"));
    public static final StreamCodec<FriendlyByteBuf, IntroPayload> CODEC = StreamCodec.unit(INSTANCE);

    public static void handle(IntroPayload payload, IPayloadContext context) {
        context.enqueueWork(IntroController::start);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
