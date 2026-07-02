package com.altnoir.poopsky.common.entity.p;

import com.altnoir.poopsky.init.PEffects;
import com.altnoir.poopsky.PTags;
import com.altnoir.poopsky.util.toiletUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ToiletEntity extends Entity {
    private boolean goldenPoop;
    private long poopTime;

    public ToiletEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    public void setGoldenPoop(boolean goldenPoop) {
        this.goldenPoop = goldenPoop;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.goldenPoop = compoundTag.getBoolean("goldenPoop");
        this.poopTime = compoundTag.getLong("poopTime");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putBoolean("goldenPoop", this.goldenPoop);
        compoundTag.putLong("poopTime", this.poopTime);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        if (!this.level().getBlockState(this.blockPosition()).is(PTags.Blocks.TOILET_BLOCKS)) {
            this.kill();
            return;
        }
        if (this.getPassengers().isEmpty()) {
            this.kill();
            return;
        }
        Entity firstPassenger = this.getPassengers().getFirst();
        if (firstPassenger instanceof LivingEntity livingEntity) {
            float yOffset = livingEntity instanceof Player ? 0.55F : 0.05F;
            toiletUtil.canPoop(level(), livingEntity, livingEntity.hasEffect(PEffects.INTESTINAL_SPASM), goldenPoop, yOffset, 0.5F, poopTime,
                    time -> this.poopTime = time);
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        passenger.setPos(this.getX(), this.getY() + 1.05F, this.getZ());
        super.removePassenger(passenger);
        if (!this.isRemoved()) {
            this.kill();
        }
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        super.positionRider(passenger, callback);
        if (passenger instanceof Player player) {
            player.setPos(this.getX(), this.getY() + 0.45, this.getZ());
        } else {
            passenger.setPos(this.getX(), this.getY() + 0.95, this.getZ());
        }
    }
}