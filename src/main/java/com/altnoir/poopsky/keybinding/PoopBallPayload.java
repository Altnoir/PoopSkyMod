package com.altnoir.poopsky.keybinding;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.component.PSComponents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PoopBallPayload(int value) implements CustomPayload {
    public static final CustomPayload.Id<PoopBallPayload> ID = new CustomPayload.Id<>(Identifier.of(PoopSky.MOD_ID, "poop_ball_component"));
    public static final PacketCodec<RegistryByteBuf, PoopBallPayload> CODEC = PacketCodec.ofStatic(
            (buf, payload) -> buf.writeInt(payload.value),
            buf -> new PoopBallPayload(buf.readInt())
    );
    public static void register() {
        PayloadTypeRegistry.playC2S().register(PoopBallPayload.ID, PoopBallPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PoopBallPayload.ID, (payload, context) -> {
            context.player().server.execute(() -> {
                ItemStack stack = context.player().getMainHandStack();
                stack.set(PSComponents.POOP_BALL_COMPONENT, payload.value());
            });
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

