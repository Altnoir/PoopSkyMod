package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UnpoopingTotemActivationPayload(ItemStack stack) implements CustomPacketPayload {
    public static final Type<UnpoopingTotemActivationPayload> TYPE = new Type<>(PoopSky.loc("unpooping_totem_activation"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UnpoopingTotemActivationPayload> CODEC =
            ItemStack.STREAM_CODEC.map(UnpoopingTotemActivationPayload::new, UnpoopingTotemActivationPayload::stack);

    public static void handle(UnpoopingTotemActivationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> Minecraft.getInstance().gameRenderer.displayItemActivation(payload.stack()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}