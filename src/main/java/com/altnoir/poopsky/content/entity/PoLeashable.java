package com.altnoir.poopsky.content.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.item.Items;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

/** 为 1.20.1 非生物载具保留拴绳存档、距离牵引和客户端持有者同步。 */
public interface PoLeashable {
    @Nullable LeashData getLeashData();
    void setLeashData(@Nullable LeashData data);
    void elasticRangeLeashBehaviour(Entity holder, float distance);
    default Entity entity() { return (Entity) this; }
    default boolean isLeashed() { return getLeashData() != null || entity().getEntityData().get(PoVehicleEntity.LEASH_HOLDER) != 0; }
    default Entity getLeashHolder() {
        if (entity().level().isClientSide) {
            int id = entity().getEntityData().get(PoVehicleEntity.LEASH_HOLDER);
            return id == 0 ? null : entity().level().getEntity(id);
        }
        LeashData data = getLeashData();
        return data == null ? null : data.holder;
    }
    default void setLeashedTo(Entity holder) {
        setLeashData(new LeashData(holder, null, null));
        entity().getEntityData().set(PoVehicleEntity.LEASH_HOLDER, holder.getId());
    }
    default void dropLeash(boolean broadcast, boolean dropItem) {
        setLeashData(null);
        entity().getEntityData().set(PoVehicleEntity.LEASH_HOLDER, 0);
        if (dropItem && !entity().level().isClientSide) entity().spawnAtLocation(Items.LEAD);
    }
    default void tickLeash() {
        if (!(entity().level() instanceof ServerLevel level)) return;
        LeashData data = getLeashData();
        if (data == null) return;
        if (data.holder == null) {
            if (data.uuid != null) data.holder = level.getEntity(data.uuid);
            else if (data.knot != null) data.holder = LeashFenceKnotEntity.getOrCreateKnot(level, data.knot);
            if (data.holder == null) {
                if (entity().tickCount > 100) dropLeash(true, true);
                return;
            }
            entity().getEntityData().set(PoVehicleEntity.LEASH_HOLDER, data.holder.getId());
        }
        if (!data.holder.isAlive() || !entity().isAlive() || data.holder.level() != level) { dropLeash(true, true); return; }
        float distance = entity().distanceTo(data.holder);
        if (distance > 10) dropLeash(true, true);
        else if (distance > 6) elasticRangeLeashBehaviour(data.holder, distance);
    }
    default void writeLeashData(CompoundTag target, LeashData data) {
        if (data == null) return;
        CompoundTag leash = new CompoundTag();
        if (data.holder instanceof LeashFenceKnotEntity knot) {
            BlockPos pos = knot.getPos();
            leash.putInt("X", pos.getX()); leash.putInt("Y", pos.getY()); leash.putInt("Z", pos.getZ());
        } else if (data.holder != null) leash.putUUID("UUID", data.holder.getUUID());
        else if (data.uuid != null) leash.putUUID("UUID", data.uuid);
        else if (data.knot != null) {
            leash.putInt("X", data.knot.getX()); leash.putInt("Y", data.knot.getY()); leash.putInt("Z", data.knot.getZ());
        }
        target.put("Leash", leash);
    }
    default LeashData readLeashData(CompoundTag target) {
        if (!target.contains("Leash", 10)) return null;
        CompoundTag leash = target.getCompound("Leash");
        if (leash.hasUUID("UUID")) return new LeashData(null, leash.getUUID("UUID"), null);
        if (leash.contains("X")) return new LeashData(null, null, new BlockPos(leash.getInt("X"),leash.getInt("Y"),leash.getInt("Z")));
        return null;
    }
    final class LeashData {
        Entity holder;
        final UUID uuid;
        final BlockPos knot;
        LeashData(Entity holder, UUID uuid, BlockPos knot) { this.holder = holder; this.uuid = uuid; this.knot = knot; }
    }
}
