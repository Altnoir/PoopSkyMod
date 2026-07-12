package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.renderer.TimeBellOverlay;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TimeBellFreezePayload(boolean frozen) implements CustomPacketPayload {
    public static final Type<TimeBellFreezePayload> TYPE = new Type<>(PoopSky.loc("time_bell_freeze"));
    public static final StreamCodec<FriendlyByteBuf, TimeBellFreezePayload> CODEC = StreamCodec.of(
            TimeBellFreezePayload::encode,
            TimeBellFreezePayload::decode
    );

    public static void handle(final TimeBellFreezePayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> TimeBellOverlay.setFrozen(payload.frozen()));
    }

    private static void encode(FriendlyByteBuf buf, TimeBellFreezePayload payload) {
        buf.writeBoolean(payload.frozen());
    }

    private static TimeBellFreezePayload decode(FriendlyByteBuf buf) {
        return new TimeBellFreezePayload(buf.readBoolean());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}