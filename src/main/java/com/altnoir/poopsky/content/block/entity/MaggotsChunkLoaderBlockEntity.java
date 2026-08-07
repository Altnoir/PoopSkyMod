package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.content.block.p.MaggotsChunkLoaderBlock;
import com.altnoir.poopsky.impl.MaggotsChunkLoaderSavedData;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MaggotsChunkLoaderBlockEntity extends BlockEntity {
    public static final int MAX_STRUCTURE_LEVEL = 4;
    private static final int STRUCTURE_SCAN_INTERVAL = 80;
    private static final int BLOCKS_PER_TICK = 10;
    private static final int SCAN_EDGE = 9;
    private static final int SCAN_RADIUS = SCAN_EDGE / 2;
    private static final int TOTAL_SCAN_BLOCKS = SCAN_EDGE * SCAN_EDGE * SCAN_EDGE;

    private int scanIndex = -1;
    private int scanCount3;
    private int scanCount5;
    private int scanCount7;
    private int scanCount9;
    private int loadedRadius = -1;
    private boolean ticketsInitialized;

    public MaggotsChunkLoaderBlockEntity(BlockPos pos, BlockState state) {
        super(PoBlockEntityType.MAGGOTS_CHUNK_LOADER.get(), pos, state);
    }

    public static void tick(Level level, BlockState state, MaggotsChunkLoaderBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (serverLevel.getGameTime() % STRUCTURE_SCAN_INTERVAL == 0L && blockEntity.scanIndex < 0) {
            blockEntity.startScan();
        }

        if (blockEntity.scanIndex >= 0) {
            blockEntity.continueScan(serverLevel, state);
        }
    }

    private void startScan() {
        scanIndex = 0;
        scanCount3 = 0;
        scanCount5 = 0;
        scanCount7 = 0;
        scanCount9 = 0;
    }

    private void continueScan(ServerLevel level, BlockState state) {
        int checked = 0;
        while (checked < BLOCKS_PER_TICK && scanIndex < TOTAL_SCAN_BLOCKS) {
            int index = scanIndex++;
            int dz = index % SCAN_EDGE - SCAN_RADIUS;
            int dy = (index / SCAN_EDGE) % SCAN_EDGE - SCAN_RADIUS;
            int dx = index / (SCAN_EDGE * SCAN_EDGE) - SCAN_RADIUS;

            if (level.getBlockState(worldPosition.offset(dx, dy, dz)).is(PoBlocks.MAGGOTS_BLOCK.get())) {
                int distance = Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz));
                if (distance == 4) scanCount9++;
                if (distance == 3) scanCount7++;
                if (distance == 2) scanCount5++;
                if (distance == 1) scanCount3++;
            }
            checked++;
        }

        if (scanIndex >= TOTAL_SCAN_BLOCKS) {
            scanIndex = -1;
            refreshLoading(level, state, levelFromCounts(scanCount3, scanCount5, scanCount7, scanCount9));
        }
    }

    public void refreshLoading(ServerLevel level, BlockState state, int structureLevel) {
        int desiredRadius = state.getValue(MaggotsChunkLoaderBlock.POWERED) ? structureLevel - 1 : -1;
        if (desiredRadius == loadedRadius && ticketsInitialized) {
            return;
        }

        boolean wasActive = loadedRadius >= 0;
        updateTickets(level, desiredRadius);
        loadedRadius = desiredRadius;
        ticketsInitialized = true;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);

        boolean isActive = desiredRadius >= 0;
        if (!wasActive && isActive) {
            level.playSound(null, worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
        } else if (wasActive && !isActive) {
            level.playSound(null, worldPosition, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public int getLoadedRadius() {
        return loadedRadius;
    }

    private static int levelFromCounts(int count3, int count5, int count7, int count9) {
        if (count9 >= 81) return 4;
        if (count7 >= 49) return 3;
        if (count5 >= 25) return 2;
        if (count3 >= 9) return 1;
        return 0;
    }

    public static int getStructureLevel(ServerLevel level, BlockPos loaderPos) {
        int count3 = 0;
        int count5 = 0;
        int count7 = 0;
        int count9 = 0;

        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    if (!level.getBlockState(loaderPos.offset(dx, dy, dz)).is(PoBlocks.MAGGOTS_BLOCK.get())) {
                        continue;
                    }

                    int distance = Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz));
                    if (distance == 4) count9++;
                    if (distance == 3) count7++;
                    if (distance == 2) count5++;
                    if (distance == 1) count3++;
                }
            }
        }

        return levelFromCounts(count3, count5, count7, count9);
    }

    public void releaseAllChunks(ServerLevel level) {
        if (loadedRadius >= 0) {
            level.playSound(null, worldPosition, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        MaggotsChunkLoaderSavedData.get(level).update(level, worldPosition, -1);
        loadedRadius = -1;
        ticketsInitialized = true;
        setChanged();
    }

    private void updateTickets(ServerLevel level, int newRadius) {
        MaggotsChunkLoaderSavedData.get(level).update(level, worldPosition, newRadius);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("loaded_radius", loadedRadius);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        loadedRadius = tag.contains("loaded_radius") ? tag.getInt("loaded_radius") : -1;
    }
}
