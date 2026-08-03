package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.content.block.p.FlushToiletBlock;
import com.altnoir.poopsky.impl.util.ToiletUtil;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class FlushToiletEntity extends Entity {
    private long poopTime;

    public FlushToiletEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.poopTime = compoundTag.getLong("poopTime");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putLong("poopTime", this.poopTime);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;
        var state = this.level().getBlockState(this.blockPosition());
        if (!(state.getBlock() instanceof FlushToiletBlock)) {
            this.kill();
            return;
        }

        if (this.getPassengers().isEmpty() || state.getValue(FlushToiletBlock.CLOSED)) {
            this.kill();
            return;
        }

        Entity passenger = this.getPassengers().getFirst();
        if (passenger instanceof LivingEntity livingEntity) {
            this.setYRot(state.getValue(FlushToiletBlock.FACING).toYRot());

            boolean hasSpasm = livingEntity.hasEffect(PoEffects.INTESTINAL_SPASM);
            boolean isGolden = state.is(PoBlocks.GOLDEN_FLUSH_TOILET.get());
            ToiletUtil.containerPoop(level(), blockPosition(), livingEntity, hasSpasm, isGolden, poopTime, time -> this.poopTime = time);
        }
    }

    @Override
    public void onPassengerTurned(Entity entityToUpdate) {

    }

    @Override
    protected void removePassenger(Entity passenger) {
        if (passenger instanceof LivingEntity living) {
            living.setDeltaMovement(living.getDeltaMovement().add(0.0, 0.4, 0.0));
            living.hasImpulse = true;
        }
        super.removePassenger(passenger);
        if (!this.isRemoved()) {
            this.kill();
        }
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        super.positionRider(passenger, callback);
        if (passenger instanceof Player) {
            callback.accept(passenger, this.getX(), this.getY() - 0.15, this.getZ());
        } else {
            callback.accept(passenger, this.getX(), this.getY() + 0.4, this.getZ());
        }
    }
}