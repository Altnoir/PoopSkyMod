package com.altnoir.poopsky.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.entity.p.ToiletPlugEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlugInputPayload(
        boolean forward,
        boolean backward,
        boolean left,
        boolean right,
        boolean up,
        boolean down,
        boolean fast
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PlugInputPayload> TYPE = new CustomPacketPayload.Type<>(
            PoopSky.loc("plug_input")
    );
    public static final StreamCodec<FriendlyByteBuf, PlugInputPayload> CODEC = StreamCodec.of(
            PlugInputPayload::encode,
            PlugInputPayload::decode
    );

    public static void handle(final PlugInputPayload payload, final IPayloadContext context) {
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
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}