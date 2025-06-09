package com.altnoir.poopsky.block.entity;

import com.altnoir.poopsky.block.PSBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import javax.annotation.Nullable;

public class ToiletBlockEntity extends BlockEntity {
    private BlockPos linkedPos;
    private String linkedDim;

    public ToiletBlockEntity(BlockPos pos, BlockState state) {
        super(PSBlockEntities.TOILET_BLOCK_ENTITY.get(), pos, state);
    }

    public String getLinkedDim() {
        return linkedDim;
    }

    public BlockPos getLinkedPos() {
        return linkedPos;
    }

    public void clearLinkedBlock() {
        if (level == null || level.isClientSide()) return;
        if (linkedPos == null || linkedDim == null) return;

        var server = ((ServerLevel) level).getServer();
        var targetWorld = server.getLevel(ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(this.getLinkedDim())));
        if (targetWorld == null) return;

        var chunkPos = new ChunkPos(this.getLinkedPos());

        level.getChunkSource().getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);

        var be = (ToiletBlockEntity)targetWorld.getBlockEntity(linkedPos);
        if (be == null) return;
        be.setLinkedPos(BlockPos.ZERO, "");
    }

    public void setLinkedPos(BlockPos pos, String dim) {
        this.linkedPos = pos;
        this.linkedDim = dim;
        this.setChanged();
    }

    public void setLinkedPos(BlockPos pos, ServerLevel serverLevel) {
        this.linkedPos = pos;
        this.linkedDim = serverLevel.dimension().location().toString();
        this.setChanged();
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("LinkedPos")) {
            this.linkedPos = BlockPos.of(tag.getLong("LinkedPos"));
            this.linkedDim = tag.getString("LinkedDim");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (linkedPos != null && linkedDim != null) {
            tag.putLong("LinkedPos", linkedPos.asLong());
            tag.putString("LinkedDim", linkedDim);
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }
}
