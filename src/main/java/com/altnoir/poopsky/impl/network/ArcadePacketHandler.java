package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import com.altnoir.poopsky.impl.network.PoPayloadContext;

import java.util.function.Consumer;

public final class ArcadePacketHandler {
    private ArcadePacketHandler() {
    }

    public static void handle(PoPayloadContext context, BlockPos pos, Consumer<ArcadeBlockEntity> action) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            if (ArcadeAccess.canAccess(player, pos)) {
                return;
            }

            if (player.level().getBlockEntity(pos) instanceof ArcadeBlockEntity arcade) {
                action.accept(arcade);
            }
        });
    }
}