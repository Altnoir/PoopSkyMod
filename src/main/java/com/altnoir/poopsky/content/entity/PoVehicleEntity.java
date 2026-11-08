package com.altnoir.poopsky.content.entity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

/** 两种载具共享的受击、网络生成与位置插值；不借用船的浮力和划桨逻辑。 */
public abstract class PoVehicleEntity extends Entity {
    private static final EntityDataAccessor<Integer> HURT_TIME = SynchedEntityData.defineId(PoVehicleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> HURT_DIRECTION = SynchedEntityData.defineId(PoVehicleEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(PoVehicleEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> LEASH_HOLDER = SynchedEntityData.defineId(PoVehicleEntity.class, EntityDataSerializers.INT);

    protected PoVehicleEntity(EntityType<?> type, Level level) { super(type, level); }
    @Override protected void defineSynchedData() {
        entityData.define(HURT_TIME, 0);
        entityData.define(HURT_DIRECTION, 1);
        entityData.define(DAMAGE, 0.0F);
        entityData.define(LEASH_HOLDER, 0);
    }
    protected abstract Item getDropItem();
    public int getHurtTime() { return entityData.get(HURT_TIME); }
    public void setHurtTime(int value) { entityData.set(HURT_TIME, value); }
    public int getHurtDir() { return entityData.get(HURT_DIRECTION); }
    public void setHurtDir(int value) { entityData.set(HURT_DIRECTION, value); }
    public float getDamage() { return entityData.get(DAMAGE); }
    public void setDamage(float value) { entityData.set(DAMAGE, value); }
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
    @Override public void tick() {
        super.tick();
        if (this instanceof PoLeashable leashable) leashable.tickLeash();
    }
    @Override public boolean hurt(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) return false;
        if (level().isClientSide || isRemoved()) return true;
        setHurtDir(-getHurtDir());
        setHurtTime(10);
        setDamage(getDamage() + amount * 10.0F);
        markHurt();
        boolean creative = source.getEntity() instanceof Player player && player.getAbilities().instabuild;
        if (creative || getDamage() > 40.0F) {
            if (!creative && level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) spawnAtLocation(getDropItem());
            discard();
        }
        return true;
    }
    protected double getDefaultGravity() { return 0.04D; }
    protected void applyGravity() {
        if (!isNoGravity()) setDeltaMovement(getDeltaMovement().add(0, -getDefaultGravity(), 0));
    }
    protected void lerpPositionAndRotationStep(int steps, double x, double y, double z, double yaw, double pitch) {
        double fraction = 1.0D / steps;
        setPos(Mth.lerp(fraction, getX(), x), Mth.lerp(fraction, getY(), y), Mth.lerp(fraction, getZ(), z));
        setYRot((float) (getYRot() + Mth.wrapDegrees(yaw - getYRot()) * fraction));
        setXRot((float) Mth.lerp(fraction, getXRot(), pitch));
    }
    public double lerpTargetX() { return getX(); }
    public double lerpTargetY() { return getY(); }
    public double lerpTargetZ() { return getZ(); }
    public float lerpTargetXRot() { return getXRot(); }
    public float lerpTargetYRot() { return getYRot(); }
}
