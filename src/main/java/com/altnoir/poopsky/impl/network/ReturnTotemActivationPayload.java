package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ReturnTotemActivationPayload(ItemStack stack) implements CustomPacketPayload {
    public static final Type<ReturnTotemActivationPayload> TYPE = new Type<>(PoopSky.loc("return_totem_activation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ReturnTotemActivationPayload> CODEC =
            ItemStack.STREAM_CODEC.map(ReturnTotemActivationPayload::new, ReturnTotemActivationPayload::stack);

    public static void handle(ReturnTotemActivationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> Minecraft.getInstance().gameRenderer.displayItemActivation(payload.stack()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}