package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.entity.p.FlushToiletCartEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record FlushToiletCartInputPayload(
        boolean forward,
        boolean backward,
        boolean left,
        boolean right,
        boolean fast,
        boolean jump
) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<FlushToiletCartInputPayload> TYPE = new CustomPacketPayload.Type<>(
            PoopSky.loc("flush_toilet_cart_input")
    );
    public static final StreamCodec<FriendlyByteBuf, FlushToiletCartInputPayload> CODEC = StreamCodec.of(
            FlushToiletCartInputPayload::encode,
            FlushToiletCartInputPayload::decode
    );

    public static void handle(final FlushToiletCartInputPayload payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player && player.getVehicle() instanceof FlushToiletCartEntity cart) {
                cart.setInput(
                        payload.forward(),
                        payload.backward(),
                        payload.left(),
                        payload.right(),
                        payload.fast()
                );
            }
        });
    }

    private static void encode(FriendlyByteBuf buf, FlushToiletCartInputPayload payload) {
        buf.writeBoolean(payload.forward());
        buf.writeBoolean(payload.backward());
        buf.writeBoolean(payload.left());
        buf.writeBoolean(payload.right());
        buf.writeBoolean(payload.fast());
        buf.writeBoolean(payload.jump());
    }

    private static FlushToiletCartInputPayload decode(FriendlyByteBuf buf) {
        return new FlushToiletCartInputPayload(
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
