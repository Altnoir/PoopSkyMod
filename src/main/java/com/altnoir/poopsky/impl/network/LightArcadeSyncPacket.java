package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record LightArcadeSyncPacket(BlockPos machinePos, CompoundTag data) implements CustomPacketPayload {
    public static final Type<LightArcadeSyncPacket> TYPE = new Type<>(PoopSky.loc("light_arcade_sync"));

    public static final StreamCodec<FriendlyByteBuf, LightArcadeSyncPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, LightArcadeSyncPacket::machinePos,
            ByteBufCodecs.COMPOUND_TAG, LightArcadeSyncPacket::data,
            LightArcadeSyncPacket::new
    );

    public static void handle(LightArcadeSyncPacket payload, @NotNull IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null || !level.isLoaded(payload.machinePos())) {
                return;
            }

            if (level.getBlockEntity(payload.machinePos()) instanceof ArcadeBlockEntity arcade) {
                arcade.applyClientData(payload.data(), level.registryAccess());
            }
        });
    }

    @Override
    public @NotNull Type<LightArcadeSyncPacket> type() {
        return TYPE;
    }
}
