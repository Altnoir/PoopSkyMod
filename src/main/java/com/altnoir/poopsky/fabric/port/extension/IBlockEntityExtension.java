package com.altnoir.poopsky.fabric.port.extension;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IBlockEntityExtension {
    private BlockEntity self() {
        return (BlockEntity) this;
    }

    /**
     * Called when the chunk's TE update tag, gotten from {@link BlockEntity#getUpdateTag(HolderLookup.Provider)}, is received on the client.
     * <p>
     * Used to handle this tag in a special way. By default this simply calls {@link BlockEntity#loadWithComponents(CompoundTag, HolderLookup.Provider)}.
     *
     * @param tag The {@link CompoundTag} sent from {@link BlockEntity#getUpdateTag(HolderLookup.Provider)}
     */
    default void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        self().loadWithComponents(tag, lookupProvider);
    }
}
