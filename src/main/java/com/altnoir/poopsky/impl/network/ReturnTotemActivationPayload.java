package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import com.altnoir.poopsky.impl.network.PoPayload;
import net.minecraft.world.item.ItemStack;
import com.altnoir.poopsky.impl.network.PoPayloadContext;

public record ReturnTotemActivationPayload(ItemStack stack) implements PoPayload {
    public static final Type<ReturnTotemActivationPayload> TYPE = new Type<>(PoopSky.loc("return_totem_activation"));
    public static final StreamCodec<FriendlyByteBuf, ReturnTotemActivationPayload> CODEC =
            com.altnoir.poopsky.impl.network.codec.ByteBufCodecs.ITEM_STACK.map(ReturnTotemActivationPayload::new, ReturnTotemActivationPayload::stack);

    public static void handle(ReturnTotemActivationPayload payload, PoPayloadContext context) {
        context.enqueueWork(() -> Minecraft.getInstance().gameRenderer.displayItemActivation(payload.stack()));
    }

    @Override
    public Type<? extends PoPayload> type() {
        return TYPE;
    }
}