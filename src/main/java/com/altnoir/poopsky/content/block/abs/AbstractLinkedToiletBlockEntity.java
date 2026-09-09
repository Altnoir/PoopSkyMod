package com.altnoir.poopsky.content.block.abs;

import com.altnoir.abysslib.AbyssLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public abstract class AbstractLinkedToiletBlockEntity extends BlockEntity {
    private BlockPos linkedPos;
    private String linkedDim;

    protected AbstractLinkedToiletBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState blockState) {
        super(blockEntityType, pos, blockState);
    }

    public String getLinkedDim() {
        return linkedDim;
    }

    public BlockPos getLinkedPos() {
        return linkedPos;
    }

    public void clearLinkedBlock() {
        if (level == null || level.isClientSide()) return;
        if (linkedPos == null || linkedDim == null || linkedDim.isBlank()) return;

        var targetDimension = AbyssLib.tryParse(linkedDim);
        if (targetDimension == null) return;

        var server = ((ServerLevel) level).getServer();
        var targetWorld = server.getLevel(ResourceKey.create(Registries.DIMENSION, targetDimension));
        if (targetWorld == null) return;

        var chunkPos = new ChunkPos(linkedPos);
        targetWorld.getChunkSource().getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true);

        if (targetWorld.getBlockEntity(linkedPos) instanceof AbstractLinkedToiletBlockEntity be) {
            be.setLinkedPos(BlockPos.ZERO, "");
        }
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

    protected void saveLinkedData(CompoundTag tag) {
        if (linkedPos != null && linkedDim != null) {
            tag.putLong("LinkedPos", linkedPos.asLong());
            tag.putString("LinkedDim", linkedDim);
        }
    }

    protected void loadLinkedData(CompoundTag tag) {
        if (tag.contains("LinkedPos")) {
            this.linkedPos = BlockPos.of(tag.getLong("LinkedPos"));
            this.linkedDim = tag.getString("LinkedDim");
        }
    }
}
