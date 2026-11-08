package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.entity.p.ToiletPlugEntity;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import com.altnoir.poopsky.impl.network.PoPayload;
import net.minecraft.server.level.ServerPlayer;
import com.altnoir.poopsky.impl.network.PoPayloadContext;

public record PlugInputPayload(
        boolean forward,
        boolean backward,
        boolean left,
        boolean right,
        boolean up,
        boolean down,
        boolean fast
) implements PoPayload {
    public static final PoPayload.Type<PlugInputPayload> TYPE = new PoPayload.Type<>(
            PoopSky.loc("plug_input")
    );
    public static final StreamCodec<FriendlyByteBuf, PlugInputPayload> CODEC = StreamCodec.of(
            PlugInputPayload::encode,
            PlugInputPayload::decode
    );

    public static void handle(final PlugInputPayload payload, final PoPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player && player.getVehicle() instanceof ToiletPlugEntity plug) {
                plug.setInput(
                        payload.forward(),
                        payload.backward(),
                        payload.left(),
                        payload.right(),
                        payload.up(),
                        payload.down(),
                        payload.fast()
                );
            }
        });
    }

    private static void encode(FriendlyByteBuf buf, PlugInputPayload payload) {
        buf.writeBoolean(payload.forward());
        buf.writeBoolean(payload.backward());
        buf.writeBoolean(payload.left());
        buf.writeBoolean(payload.right());
        buf.writeBoolean(payload.up());
        buf.writeBoolean(payload.down());
        buf.writeBoolean(payload.fast());
    }

    private static PlugInputPayload decode(FriendlyByteBuf buf) {
        return new PlugInputPayload(
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    @Override
    public Type<? extends PoPayload> type() {
        return TYPE;
    }
}