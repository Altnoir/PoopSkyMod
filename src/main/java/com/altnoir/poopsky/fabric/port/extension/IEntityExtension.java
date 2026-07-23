package com.altnoir.poopsky.fabric.port.extension;

import net.minecraft.nbt.CompoundTag;

public interface IEntityExtension {
    /**
     * Returns a NBTTagCompound that can be used to store custom data for this entity.
     * It will be written, and read from disc, so it persists over world saves.
     *
     * @return A NBTTagCompound
     */
    default CompoundTag getCustomData() {
        return new CompoundTag();
    }
}
