package com.altnoir.poopsky.impl.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import java.util.function.Function;

public final class PoTeleport {
    private PoTeleport() {}
    public static Entity move(Entity entity, ServerLevel target, Vec3 destination, float yaw, float pitch, boolean portalSound) {
        Vec3 speed = entity.getDeltaMovement();
        target.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, new ChunkPos(BlockPos.containing(destination)), 1, entity.getId());
        Entity moved;
        if (entity.level() == target) {
            if (entity instanceof ServerPlayer player) player.connection.teleport(destination.x, destination.y, destination.z, yaw, pitch);
            else { entity.teleportTo(destination.x, destination.y, destination.z); entity.setYRot(yaw); entity.setXRot(pitch); }
            moved = entity;
        } else {
            moved = entity.changeDimension(target, new ITeleporter() {
                @Override public PortalInfo getPortalInfo(Entity original, ServerLevel level, Function<ServerLevel, PortalInfo> fallback) { return new PortalInfo(destination, speed, yaw, pitch); }
                @Override public Entity placeEntity(Entity original, ServerLevel current, ServerLevel level, float oldYaw, Function<Boolean, Entity> reposition) { return reposition.apply(false); }
                @Override public boolean playTeleportSound(ServerPlayer player, ServerLevel source, ServerLevel destinationLevel) { return portalSound; }
            });
        }
        if (moved != null) { moved.setDeltaMovement(speed); moved.setPortalCooldown(); }
        return moved;
    }
}
