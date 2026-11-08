package com.altnoir.poopsky.impl.network;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import java.util.concurrent.CompletableFuture;

public final class PoPayloadContext {
    private final NetworkEvent.Context context;
    public PoPayloadContext(NetworkEvent.Context context) { this.context = context; }
    public Player player() { return context.getSender(); }
    public CompletableFuture<Void> enqueueWork(Runnable action) { return context.enqueueWork(action); }
}
