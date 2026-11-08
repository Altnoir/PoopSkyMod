package com.altnoir.poopsky.impl.network;

import net.minecraft.resources.ResourceLocation;

/** 模组自有消息标识，不依赖 1.21 的网络类。 */
public interface PoPayload {
    Type<? extends PoPayload> type();
    record Type<T extends PoPayload>(ResourceLocation id) {}
}
