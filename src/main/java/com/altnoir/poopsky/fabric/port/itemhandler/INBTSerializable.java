package com.altnoir.poopsky.fabric.port.itemhandler;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.UnknownNullability;

public interface INBTSerializable<T extends Tag> {
    @UnknownNullability
    T serializeNBT(HolderLookup.Provider provider);

    void deserializeNBT(HolderLookup.Provider provider, T nbt);
}
