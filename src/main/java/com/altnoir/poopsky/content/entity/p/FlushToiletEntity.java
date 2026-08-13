package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.content.block.p.FlushToiletBlock;
import com.altnoir.poopsky.impl.util.ToiletUtil;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoEffects;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class FlushToiletEntity extends Entity {
    private long poopTime;

    public FlushToiletEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.poopTime = input.getLongOr("poopTime", 0L);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putLong("poopTime", this.poopTime);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) return;
        var state = this.level().getBlockState(this.blockPosition());
        if (!(state.getBlock() instanceof FlushToiletBlock)) {
            this.kill((net.minecraft.server.level.ServerLevel) this.level());
            return;
        }

        if (this.getPassengers().isEmpty() || state.getValue(FlushToiletBlock.CLOSED)) {
            this.kill((net.minecraft.server.level.ServerLevel) this.level());
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
        }
        super.removePassenger(passenger);
        if (!this.isRemoved()) {
            this.kill((net.minecraft.server.level.ServerLevel) this.level());
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

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float damage) {
        return false;
    }
}
