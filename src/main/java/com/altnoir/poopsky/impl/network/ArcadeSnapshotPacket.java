package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.client.arcade.ArcadeWorldScreenRenderer;
import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import com.altnoir.poopsky.content.item.p.GameDiscItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ArcadeSnapshotPacket(BlockPos machinePos, CompoundTag blockData,
                                   CompoundTag gameSnapshot) implements CustomPacketPayload {
    public static final Type<ArcadeSnapshotPacket> TYPE = new Type<>(PoopSky.loc("arcade_snapshot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ArcadeSnapshotPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ArcadeSnapshotPacket::machinePos,
            ByteBufCodecs.COMPOUND_TAG, ArcadeSnapshotPacket::blockData,
            ByteBufCodecs.COMPOUND_TAG, ArcadeSnapshotPacket::gameSnapshot,
            ArcadeSnapshotPacket::new
    );

    public static void handle(ArcadeSnapshotPacket payload, @NotNull IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null || !level.isLoaded(payload.machinePos())) {
                return;
            }
            if (!(level.getBlockEntity(payload.machinePos()) instanceof ArcadeBlockEntity arcade)) {
                return;
            }

            arcade.applyClientData(payload.blockData(), level.registryAccess());
            ItemStack cartridge = arcade.getCartridge();
            if (cartridge.getItem() instanceof GameDiscItem) {
                ArcadeWorldScreenRenderer.applyRemoteSnapshot(payload.machinePos(), cartridge, payload.gameSnapshot());
            }
        });
    }

    @Override
    public @NotNull Type<ArcadeSnapshotPacket> type() {
        return TYPE;
    }
}
