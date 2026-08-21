package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import com.altnoir.poopsky.game.client.arcade.ArcadeWorldScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ArcadeGameSnapshotPacket(BlockPos machinePos, CompoundTag gameSnapshot) implements CustomPacketPayload {
    public static final Type<ArcadeGameSnapshotPacket> TYPE = new Type<>(PoopSky.loc("arcade_game_snapshot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArcadeGameSnapshotPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ArcadeGameSnapshotPacket decode(RegistryFriendlyByteBuf buffer) {
            BlockPos machinePos = buffer.readBlockPos();
            CompoundTag gameSnapshot = buffer.readNbt();
            return new ArcadeGameSnapshotPacket(machinePos, gameSnapshot != null ? gameSnapshot : new CompoundTag());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, ArcadeGameSnapshotPacket payload) {
            buffer.writeBlockPos(payload.machinePos());
            buffer.writeNbt(payload.gameSnapshot());
        }
    };

    public static void handle(ArcadeGameSnapshotPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null || !level.isLoaded(payload.machinePos())) {
                return;
            }
            if (level.getBlockEntity(payload.machinePos()) instanceof ArcadeBlockEntity arcade) {
                ArcadeWorldScreenRenderer.applyRemoteSnapshot(
                        payload.machinePos(),
                        arcade.getCartridge(),
                        payload.gameSnapshot()
                );
            } else {
                ArcadeWorldScreenRenderer.applyRemoteSnapshot(payload.machinePos(), ItemStack.EMPTY, null);
            }
        });
    }

    @Override
    public Type<ArcadeGameSnapshotPacket> type() {
        return TYPE;
    }
}