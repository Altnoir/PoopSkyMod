package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.p.MaggotsChunkLoaderBlock;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoBlocks;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;
import net.neoforged.neoforge.common.world.chunk.TicketSet;

import java.util.Map;

public class MaggotsChunkLoaderBlockEntity extends BlockEntity {
    public static final int MAX_STRUCTURE_LEVEL = 4;
    private static final int MAX_LOADED_RADIUS = MAX_STRUCTURE_LEVEL - 1;
    public static final TicketController TICKET_CONTROLLER = new TicketController(
            PoopSky.loc("maggots_chunk_loader"),
            MaggotsChunkLoaderBlockEntity::validateTickets
    );

    private static final int STRUCTURE_SCAN_INTERVAL = 80;
    private static final int BLOCKS_PER_TICK = 10;
    private static final int[] REQUIRED_COUNTS = {9, 25, 49, 81};
    private static final BlockPos[][] SHELL_OFFSETS = buildShellOffsets();

    private int scanStage = -1;
    private int scanIndex = -1;
    private int scanCount;
    private int loadedRadius = -1;
    private boolean ticketsInitialized;

    public MaggotsChunkLoaderBlockEntity(BlockPos pos, BlockState state) {
        super(PoBlockEntityType.MAGGOTS_CHUNK_LOADER.get(), pos, state);
    }

    private static BlockPos[][] buildShellOffsets() {
        return new BlockPos[][]{
                buildShell(3),
                buildShell(5),
                buildShell(7),
                buildShell(9)
        };
    }

    private static BlockPos[] buildShell(int edge) {
        int radius = (edge - 1) / 2;
        int size = edge * edge * edge - (edge - 2) * (edge - 2) * (edge - 2);
        BlockPos[] positions = new BlockPos[size];
        int index = 0;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz)) == radius) {
                        positions[index++] = new BlockPos(dx, dy, dz);
                    }
                }
            }
        }
        return positions;
    }

    public static void tick(Level level, BlockState state, MaggotsChunkLoaderBlockEntity blockEntity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (serverLevel.getGameTime() % STRUCTURE_SCAN_INTERVAL == 0L && blockEntity.scanStage < 0) {
            blockEntity.startScan();
        }

        if (serverLevel.getGameTime() % STRUCTURE_SCAN_INTERVAL == 0L && blockEntity.loadedRadius >= 0) {
            float pitch = 0.6F + blockEntity.loadedRadius * 0.3F;
            serverLevel.playSound(null, blockEntity.worldPosition, SoundEvents.BEACON_AMBIENT, SoundSource.BLOCKS, 1.0F, pitch);
        }

        if (blockEntity.scanStage >= 0) {
            blockEntity.continueScan(serverLevel, state);
        }
    }

    private void startScan() {
        scanStage = 0;
        scanIndex = 0;
        scanCount = 0;
    }

    private void continueScan(ServerLevel level, BlockState state) {
        int checked = 0;
        while (checked < BLOCKS_PER_TICK && scanStage >= 0) {
            BlockPos[] shell = SHELL_OFFSETS[scanStage];
            while (checked < BLOCKS_PER_TICK && scanIndex < shell.length) {
                BlockPos offset = shell[scanIndex++];
                if (level.getBlockState(worldPosition.offset(offset)).is(PoTags.Blocks.MAGGOTS_CHUNK_LOADER_BASE_BLOCKS)) {
                    scanCount++;
                }
                checked++;
            }

            if (scanIndex >= shell.length) {
                if (scanCount < REQUIRED_COUNTS[scanStage]) {
                    int structureLevel = scanStage;
                    scanStage = -1;
                    refreshLoading(level, state, structureLevel);
                    return;
                }

                scanStage++;
                scanIndex = 0;
                scanCount = 0;
                if (scanStage >= SHELL_OFFSETS.length) {
                    scanStage = -1;
                    refreshLoading(level, state, MAX_STRUCTURE_LEVEL);
                    return;
                }
            }
        }
    }

    public void refreshLoading(ServerLevel level, BlockState state, int structureLevel) {
        int desiredRadius = state.getValue(MaggotsChunkLoaderBlock.POWERED) ? structureLevel - 1 : -1;
        if (desiredRadius == loadedRadius && ticketsInitialized) {
            return;
        }

        boolean wasActive = loadedRadius >= 0;
        updateTickets(level, ticketsInitialized ? loadedRadius : -1, desiredRadius);
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

    public static int getStructureLevel(ServerLevel level, BlockPos loaderPos) {
        int count3 = countShell(level, loaderPos, 0);
        if (count3 < REQUIRED_COUNTS[0]) {
            return 0;
        }

        int count5 = countShell(level, loaderPos, 1);
        if (count5 < REQUIRED_COUNTS[1]) {
            return 1;
        }

        int count7 = countShell(level, loaderPos, 2);
        if (count7 < REQUIRED_COUNTS[2]) {
            return 2;
        }

        int count9 = countShell(level, loaderPos, 3);
        return count9 >= REQUIRED_COUNTS[3] ? 4 : 3;
    }

    private static int countShell(ServerLevel level, BlockPos loaderPos, int stage) {
        int count = 0;
        for (BlockPos offset : SHELL_OFFSETS[stage]) {
            if (level.getBlockState(loaderPos.offset(offset)).is(PoTags.Blocks.MAGGOTS_CHUNK_LOADER_BASE_BLOCKS)) {
                count++;
            }
        }
        return count;
    }

    public void releaseAllChunks(ServerLevel level) {
        ChunkPos center = new ChunkPos(worldPosition);
        if (loadedRadius >= 0) {
            level.playSound(null, worldPosition, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        int cleanupRadius = Math.max(loadedRadius, MAX_LOADED_RADIUS);
        for (int offsetX = -cleanupRadius; offsetX <= cleanupRadius; offsetX++) {
            for (int offsetZ = -cleanupRadius; offsetZ <= cleanupRadius; offsetZ++) {
                TICKET_CONTROLLER.forceChunk(level, worldPosition, center.x + offsetX, center.z + offsetZ, false, true);
            }
        }
        loadedRadius = -1;
        ticketsInitialized = true;
        setChanged();
    }

    private void updateTickets(ServerLevel level, int oldRadius, int newRadius) {
        ChunkPos center = new ChunkPos(worldPosition);
        int maxRadius = Math.max(oldRadius, newRadius);
        for (int offsetX = -maxRadius; offsetX <= maxRadius; offsetX++) {
            for (int offsetZ = -maxRadius; offsetZ <= maxRadius; offsetZ++) {
                boolean wasLoaded = inRadius(offsetX, offsetZ, oldRadius);
                boolean shouldLoad = inRadius(offsetX, offsetZ, newRadius);
                if (wasLoaded != shouldLoad) {
                    TICKET_CONTROLLER.forceChunk(level, worldPosition, center.x + offsetX, center.z + offsetZ, shouldLoad, true);
                }
            }
        }
    }

    private static boolean inRadius(int offsetX, int offsetZ, int radius) {
        return radius >= 0 && Math.abs(offsetX) <= radius && Math.abs(offsetZ) <= radius;
    }

    private static void validateTickets(ServerLevel level, TicketHelper helper) {
        for (Map.Entry<BlockPos, TicketSet> entry : helper.getBlockTickets().entrySet()) {
            BlockPos owner = entry.getKey();
            BlockState state = level.getBlockState(owner);
            if (!state.is(PoBlocks.MAGGOTS_CHUNK_LOADER.get()) || !level.hasNeighborSignal(owner)) {
                helper.removeAllTickets(owner);
                continue;
            }

            int radius = getStructureLevel(level, owner) - 1;
            ChunkPos center = new ChunkPos(owner);
            for (long chunk : new LongOpenHashSet(entry.getValue().nonTicking())) {
                helper.removeTicket(owner, chunk, false);
            }
            for (long chunk : new LongOpenHashSet(entry.getValue().ticking())) {
                ChunkPos chunkPos = new ChunkPos(chunk);
                if (!inRadius(chunkPos.x - center.x, chunkPos.z - center.z, radius)) {
                    helper.removeTicket(owner, chunk, true);
                }
            }
        }
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
