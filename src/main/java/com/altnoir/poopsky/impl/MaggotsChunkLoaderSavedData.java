package com.altnoir.poopsky.impl;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

public final class MaggotsChunkLoaderSavedData extends SavedData {
    private static final String DATA_NAME = "poopsky_maggots_chunk_loaders";
    private static final Factory<MaggotsChunkLoaderSavedData> FACTORY = new Factory<>(
            MaggotsChunkLoaderSavedData::new,
            MaggotsChunkLoaderSavedData::load,
            DataFixTypes.LEVEL
    );

    private final Long2IntOpenHashMap owners = new Long2IntOpenHashMap();
    private final LongOpenHashSet managedChunks = new LongOpenHashSet();

    private MaggotsChunkLoaderSavedData() {
        owners.defaultReturnValue(-1);
    }

    public static MaggotsChunkLoaderSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public void update(ServerLevel level, BlockPos owner, int radius) {
        long ownerKey = owner.asLong();
        int oldRadius = owners.get(ownerKey);
        if (oldRadius == radius) {
            return;
        }

        if (radius >= 0) {
            owners.put(ownerKey, radius);
        } else {
            owners.remove(ownerKey);
        }

        ChunkPos center = new ChunkPos(owner);
        int affectedRadius = Math.max(oldRadius, radius);
        for (int offsetX = -affectedRadius; offsetX <= affectedRadius; offsetX++) {
            for (int offsetZ = -affectedRadius; offsetZ <= affectedRadius; offsetZ++) {
                updateChunk(level, new ChunkPos(center.x + offsetX, center.z + offsetZ));
            }
        }
        setDirty();
    }

    private void updateChunk(ServerLevel level, ChunkPos chunkPos) {
        long chunkKey = chunkPos.toLong();
        boolean required = isRequired(chunkPos);
        if (required && !managedChunks.contains(chunkKey)) {
            if (!level.getForcedChunks().contains(chunkKey)) {
                level.setChunkForced(chunkPos.x, chunkPos.z, true);
                managedChunks.add(chunkKey);
            }
        } else if (!required && managedChunks.remove(chunkKey)) {
            level.setChunkForced(chunkPos.x, chunkPos.z, false);
        }
    }

    private boolean isRequired(ChunkPos chunkPos) {
        for (Long2IntMap.Entry entry : owners.long2IntEntrySet()) {
            ChunkPos ownerChunk = new ChunkPos(BlockPos.of(entry.getLongKey()));
            int radius = entry.getIntValue();
            if (Math.abs(chunkPos.x - ownerChunk.x) <= radius
                    && Math.abs(chunkPos.z - ownerChunk.z) <= radius) {
                return true;
            }
        }
        return false;
    }

    private static MaggotsChunkLoaderSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        MaggotsChunkLoaderSavedData data = new MaggotsChunkLoaderSavedData();
        long[] ownerKeys = tag.getLongArray("owners");
        int[] radii = tag.getIntArray("radii");
        for (int index = 0; index < Math.min(ownerKeys.length, radii.length); index++) {
            data.owners.put(ownerKeys[index], radii[index]);
        }
        data.managedChunks.addAll(new LongOpenHashSet(tag.getLongArray("managed_chunks")));
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        long[] ownerKeys = new long[owners.size()];
        int[] radii = new int[owners.size()];
        int index = 0;
        for (Long2IntMap.Entry entry : owners.long2IntEntrySet()) {
            ownerKeys[index] = entry.getLongKey();
            radii[index] = entry.getIntValue();
            index++;
        }
        tag.putLongArray("owners", ownerKeys);
        tag.putIntArray("radii", radii);
        tag.putLongArray("managed_chunks", managedChunks.toLongArray());
        return tag;
    }
}
