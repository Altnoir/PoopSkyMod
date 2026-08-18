package com.altnoir.poopsky.impl.network;

import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class ArcadeAccess {
    private static final double MAX_CONTROL_DISTANCE_SQ = 8.0D * 8.0D;

    private ArcadeAccess() {
    }

    public static boolean canAccess(ServerPlayer player, BlockPos machinePos) {
        Level level = player.level();
        if (!level.isLoaded(machinePos)) {
            return true;
        }
        if (player.position().distanceToSqr(Vec3.atCenterOf(machinePos)) > MAX_CONTROL_DISTANCE_SQ) {
            return true;
        }
        return !(level.getBlockEntity(machinePos) instanceof ArcadeBlockEntity);
    }
}