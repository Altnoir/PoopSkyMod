package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import com.altnoir.poopsky.impl.network.PoPayload;
import net.minecraft.world.item.ItemStack;
import com.altnoir.poopsky.impl.network.PoPayloadContext;

public record UnpoopingTotemActivationPayload(ItemStack stack) implements PoPayload {
    public static final Type<UnpoopingTotemActivationPayload> TYPE = new Type<>(PoopSky.loc("unpooping_totem_activation"));
    public static final StreamCodec<FriendlyByteBuf, UnpoopingTotemActivationPayload> CODEC =
            com.altnoir.poopsky.impl.network.codec.ByteBufCodecs.ITEM_STACK.map(UnpoopingTotemActivationPayload::new, UnpoopingTotemActivationPayload::stack);

    public static void handle(UnpoopingTotemActivationPayload payload, PoPayloadContext context) {
        context.enqueueWork(() -> Minecraft.getInstance().gameRenderer.displayItemActivation(payload.stack()));
    }

    @Override
    public Type<? extends PoPayload> type() {
        return TYPE;
    }
}