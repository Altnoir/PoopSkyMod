package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.block.p.MaggotsChunkLoaderBlock;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoBlocks;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
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
    public static final TicketController TICKET_CONTROLLER = new TicketController(
            PoopSky.loc("maggots_chunk_loader"),
            MaggotsChunkLoaderBlockEntity::validateTickets
    );

    private static final int STRUCTURE_SCAN_INTERVAL = 20;
    private int scanCooldown;
    private int loadedRadius = -1;
    private boolean ticketsInitialized;

    public MaggotsChunkLoaderBlockEntity(BlockPos pos, BlockState state) {
        super(PoBlockEntityType.MAGGOTS_CHUNK_LOADER.get(), pos, state);
    }

    public static void tick(Level level, BlockState state, MaggotsChunkLoaderBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel && blockEntity.scanCooldown-- <= 0) {
            blockEntity.scanCooldown = STRUCTURE_SCAN_INTERVAL;
            blockEntity.refreshLoading(serverLevel, state);
        }
    }

    public void refreshLoading(ServerLevel level, BlockState state) {
        int desiredRadius = state.getValue(MaggotsChunkLoaderBlock.POWERED)
                ? getStructureLevel(level, worldPosition)
                : -1;
        if (desiredRadius == loadedRadius && ticketsInitialized) {
            return;
        }

        updateTickets(level, ticketsInitialized ? loadedRadius : -1, desiredRadius);
        loadedRadius = desiredRadius;
        ticketsInitialized = true;
        setChanged();
    }

    public void releaseAllChunks(ServerLevel level) {
        ChunkPos center = new ChunkPos(worldPosition);
        for (int offsetX = -MAX_STRUCTURE_LEVEL; offsetX <= MAX_STRUCTURE_LEVEL; offsetX++) {
            for (int offsetZ = -MAX_STRUCTURE_LEVEL; offsetZ <= MAX_STRUCTURE_LEVEL; offsetZ++) {
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

    public static int getStructureLevel(ServerLevel level, BlockPos loaderPos) {
        int structureLevel = 0;
        for (int layer = 1; layer <= MAX_STRUCTURE_LEVEL; layer++) {
            int y = loaderPos.getY() - layer;
            boolean complete = true;
            for (int x = loaderPos.getX() - layer; x <= loaderPos.getX() + layer && complete; x++) {
                for (int z = loaderPos.getZ() - layer; z <= loaderPos.getZ() + layer; z++) {
                    if (!level.getBlockState(new BlockPos(x, y, z)).is(PoBlocks.MAGGOTS_BLOCK.get())) {
                        complete = false;
                        break;
                    }
                }
            }
            if (!complete) {
                break;
            }
            structureLevel = layer;
        }
        return structureLevel;
    }

    private static void validateTickets(ServerLevel level, TicketHelper helper) {
        for (Map.Entry<BlockPos, TicketSet> entry : helper.getBlockTickets().entrySet()) {
            BlockPos owner = entry.getKey();
            BlockState state = level.getBlockState(owner);
            if (!state.is(PoBlocks.MAGGOTS_CHUNK_LOADER.get()) || !level.hasNeighborSignal(owner)) {
                helper.removeAllTickets(owner);
                continue;
            }

            int radius = getStructureLevel(level, owner);
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
