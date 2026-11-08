package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import com.altnoir.poopsky.game.client.arcade.ArcadeWorldScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import com.altnoir.poopsky.impl.network.codec.StreamCodec;
import com.altnoir.poopsky.impl.network.PoPayload;
import net.minecraft.world.item.ItemStack;
import com.altnoir.poopsky.impl.network.PoPayloadContext;

public record ArcadeGameSnapshotPacket(BlockPos machinePos, CompoundTag gameSnapshot) implements PoPayload {
    public static final Type<ArcadeGameSnapshotPacket> TYPE = new Type<>(PoopSky.loc("arcade_game_snapshot"));

    public static final StreamCodec<FriendlyByteBuf, ArcadeGameSnapshotPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ArcadeGameSnapshotPacket decode(FriendlyByteBuf buffer) {
            BlockPos machinePos = buffer.readBlockPos();
            CompoundTag gameSnapshot = buffer.readNbt();
            return new ArcadeGameSnapshotPacket(machinePos, gameSnapshot != null ? gameSnapshot : new CompoundTag());
        }

        @Override
        public void encode(FriendlyByteBuf buffer, ArcadeGameSnapshotPacket payload) {
            buffer.writeBlockPos(payload.machinePos());
            buffer.writeNbt(payload.gameSnapshot());
        }
    };

    public static void handle(ArcadeGameSnapshotPacket payload, PoPayloadContext context) {
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