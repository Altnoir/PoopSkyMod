package com.altnoir.poopsky.content.entity.p;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ChairEntity extends Entity {
    public ChairEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void removePassenger(Entity passenger) {
        passenger.setPos(this.getX(), this.getY() + 1.0, this.getZ());
        super.removePassenger(passenger);
        if (!this.isRemoved()) {
            this.kill();
        }
    }
}
