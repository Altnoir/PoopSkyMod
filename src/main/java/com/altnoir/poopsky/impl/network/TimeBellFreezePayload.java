package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.renderer.TimeBellOverlay;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import com.altnoir.poopsky.impl.network.PoPayload;
import com.altnoir.poopsky.impl.network.PoPayloadContext;

public record TimeBellFreezePayload(boolean frozen) implements PoPayload {
    public static final Type<TimeBellFreezePayload> TYPE = new Type<>(PoopSky.loc("time_bell_freeze"));
    public static final StreamCodec<FriendlyByteBuf, TimeBellFreezePayload> CODEC = StreamCodec.of(
            TimeBellFreezePayload::encode,
            TimeBellFreezePayload::decode
    );

    public static void handle(final TimeBellFreezePayload payload, final PoPayloadContext context) {
        context.enqueueWork(() -> { com.altnoir.poopsky.impl.PoTimeFreeze.setClientFrozen(payload.frozen()); TimeBellOverlay.setFrozen(payload.frozen()); });
    }

    private static void encode(FriendlyByteBuf buf, TimeBellFreezePayload payload) {
        buf.writeBoolean(payload.frozen());
    }

    private static TimeBellFreezePayload decode(FriendlyByteBuf buf) {
        return new TimeBellFreezePayload(buf.readBoolean());
    }

    @Override
    public Type<? extends PoPayload> type() {
        return TYPE;
    }
}