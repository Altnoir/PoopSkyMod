package com.altnoir.poopsky.entity.p;

import com.altnoir.poopsky.event.PSKeyBoardInput;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.sound.TPFlySoundWrapper;
import com.google.common.collect.Lists;
import net.minecraft.BlockUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.List;

public class ToiletPlugEntity extends VehicleEntity implements Leashable {
    private LeashData leashData;
    private boolean inputForward;
    private boolean inputBackward;
    private boolean inputLeft;
    private boolean inputRight;
    private boolean inputUp;
    private boolean inputDown;
    private boolean inputFast;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYRot;
    private double lerpXRot;
    public float floatingValue = 0;
    public float floatingCounts = 0;
    public float prevFloatingValue = 0;

    private float verVelocity = 0;
    private static final float ACCELERATION = 0.1f;

    private TPFlySoundWrapper TPFlySound;


    public ToiletPlugEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        if (level.isClientSide()){
            ToiletPlugEntityClient();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void ToiletPlugEntityClient(){
        TPFlySound = new TPFlySoundWrapper(this);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public void push(Entity entity) {
        if (entity instanceof Boat) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.push(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.push(entity);
        }
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYRot = yRot;
        this.lerpXRot = xRot;
        this.lerpSteps = 10;
    }

    @Override
    public double lerpTargetX() {
        return this.lerpSteps > 0 ? this.lerpX : this.getX();
    }

    @Override
    public double lerpTargetY() {
        return this.lerpSteps > 0 ? this.lerpY : this.getY();
    }

    @Override
    public double lerpTargetZ() {
        return this.lerpSteps > 0 ? this.lerpZ : this.getZ();
    }

    @Override
    public float lerpTargetXRot() {
        return this.lerpSteps > 0 ? (float)this.lerpXRot : this.getXRot();
    }

    @Override
    public float lerpTargetYRot() {
        return this.lerpSteps > 0 ? (float)this.lerpYRot : this.getYRot();
    }

    @Override
    public Direction getMotionDirection() {
        return this.getDirection().getClockWise();
    }

    @Override
    public void tick(){
        super.tick();
        this.tickLerp();

        if (this.isControlledByLocalInstance()) {
            if (this.level().isClientSide) {
                this.updateKeyStates();
                this.moveByInput();
                if (this.getControllingPassenger() != null) {
                    TPFlySound.play();
                    spawnParticles();
                }
                else {
                    TPFlySound.stop();
                }
            }
            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }

        this.checkInsideBlocks();
        var list = this.level().getEntities(this, this.getBoundingBox().inflate(0.2F, -0.01F, 0.2F), EntitySelector.pushableBy(this));
        if (!list.isEmpty()) {
            boolean flag = !this.level().isClientSide && !(this.getControllingPassenger() instanceof Player);

            for (Entity entity : list) {
                if (!entity.hasPassenger(this)) {
                    if (flag
                            && this.getPassengers().size() < this.getMaxPassengers()
                            && !entity.isPassenger()
                            && this.hasEnoughSpaceFor(entity)
                            && entity instanceof LivingEntity
                            && !(entity instanceof WaterAnimal)
                            && !(entity instanceof Player)) {
                        entity.startRiding(this);
                    } else {
                        this.push(entity);
                    }
                }
            }
        }

        this.updateFloatingValue();
        this.autoFall();
    }

    private void tickLerp() {
        if (this.isControlledByLocalInstance()) {
            this.lerpSteps = 0;
            this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
        }

        if (this.lerpSteps > 0) {
            this.lerpPositionAndRotationStep(this.lerpSteps, this.lerpX, this.lerpY, this.lerpZ, this.lerpYRot, this.lerpXRot);
            this.lerpSteps--;
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0;
    }

    private void moveByInput() {
        var MAX_SPEED = inputFast ? 0.7f : 0.35f;
        var FBSpeed = inputForward ? MAX_SPEED : inputBackward ? -MAX_SPEED : 0f;
        var LRSpeed = inputLeft ? MAX_SPEED : inputRight ? -MAX_SPEED : 0f;
        var DAMPING = 0.975f;

        var yawRad = (float) Math.toRadians(-this.getYRot());
        var forward = new Vec3(Math.sin(yawRad), 0, Math.cos(yawRad));
        var right = new Vec3(forward.z, 0, -forward.x);
        var movement = forward.scale(FBSpeed).add(right.scale(LRSpeed));

        var velocity = this.getDeltaMovement().add(movement.scale(ACCELERATION)).scale(DAMPING);
        velocity = velocity.multiply(DAMPING, 1, DAMPING);
        var targetY = (inputUp ? MAX_SPEED : 0) + (inputDown ? -MAX_SPEED : 0);
        verVelocity = verVelocity * (1 - ACCELERATION) + targetY * ACCELERATION;

        var mainPassenger = this.getControllingPassenger();
        if (mainPassenger != null) {
            float targetYaw = mainPassenger.getYRot();
            float deltaYaw = Mth.wrapDegrees(targetYaw - this.getYRot());
            this.setYRot(this.getYRot() + deltaYaw * 0.3f);
        }
        this.setDeltaMovement(velocity.x, verVelocity, velocity.z);
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void autoFall() {
        if (this.getControllingPassenger() == null) {
            this.setDeltaMovement(0, -0.05, 0);
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
    }

    public boolean hasEnoughSpaceFor(Entity entity) {
        return entity.getBbWidth() < this.getBbWidth();
    }

    @Override
    protected void positionRider(Entity entity, MoveFunction moveFunction) {
        super.positionRider(entity, moveFunction);
        var yOffset = 0.15 + floatingValue;
        if (this.getPassengers().size() > 1) {
            var yawRad = (float) Math.toRadians(-this.getYRot());
            if (this.getPassengers().indexOf(entity) == 1) {
                var xOffset = -Math.sin(yawRad) * 0.375;
                var zOffset = -Math.cos(yawRad) * 0.375;
                entity.setPos(entity.getX() + xOffset, entity.getY() + yOffset, entity.getZ() + zOffset);
            }
            if (this.getPassengers().indexOf(entity) == 0) {
                var xOffset = Math.sin(yawRad) * 0.375;
                var zOffset = Math.cos(yawRad) * 0.375;
                entity.setPos(entity.getX() + xOffset, entity.getY() + yOffset, entity.getZ() + zOffset);
            }
        }
        else {
            entity.setPos(entity.getX(), entity.getY() + yOffset, entity.getZ());
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return canVehicleCollide(this, entity);
    }

    public static boolean canVehicleCollide(Entity vehicle, Entity entity) {
        return (entity.canBeCollidedWith() || entity.isPushable()) && !vehicle.isPassengerOfSameVehicle(entity);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        if (!this.level().isClientSide && reason.shouldDestroy() && this.isLeashed()) {
            this.dropLeash(true, true);
        }

        super.remove(reason);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().size() < this.getMaxPassengers();
    }

    protected int getMaxPassengers() {
        return 2;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity livingentity ? livingentity : super.getControllingPassenger();
    }

    public void setInput(boolean inputForward, boolean inputBackward, boolean inputLeft, boolean inputRight, boolean inputUp, boolean inputDown) {
        this.inputForward = inputForward;
        this.inputBackward = inputBackward;
        this.inputLeft = inputLeft;
        this.inputRight = inputRight;
        this.inputUp = inputUp;
        this.inputDown = inputDown;
    }

    @OnlyIn(Dist.CLIENT)
    private void updateKeyStates() {
        var mc = Minecraft.getInstance();

        setInput(mc.options.keyUp.isDown(),
                mc.options.keyDown.isDown(),
                mc.options.keyLeft.isDown(),
                mc.options.keyRight.isDown(),
                PSKeyBoardInput.UP_KEY.isDown(),
                PSKeyBoardInput.DOWN_KEY.isDown());

        boolean isSprintingNow = mc.options.keySprint.isDown();
        if (isSprintingNow) inputFast = true;
        if (!isMoving()) inputFast = false;
    }

    private boolean isMoving() {
        return inputForward || inputBackward || inputLeft || inputRight || inputUp || inputDown;
    }

    @Override
    protected Component getTypeName() {
        return Component.translatable(this.getDropItem().getDescriptionId());
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.getDropItem());
    }

    @Override
    protected Item getDropItem() {
        return PSItems.Toilet_Plug.get();
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source.is(DamageTypeTags.IS_FIRE);
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return true;
    }

    @Override
    public Vec3 getRelativePortalPosition(Direction.Axis axis, BlockUtil.FoundRectangle portal) {
        return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(axis, portal));
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player && player.isCrouching()) {
            this.kill();
            if (!player.getAbilities().instabuild) {
                if (!player.getInventory().add(new ItemStack(PSItems.Toilet_Plug.get()))) {
                    this.spawnAtLocation(PSItems.Toilet_Plug.get());
                }
            }
            return true;
        }
        return super.hurt(source, amount);
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnParticles() {
        var speed = this.getDeltaMovement().length();
        if (speed > 0.1) {
            var yawRad = (float) Math.toRadians(this.getYRot());
            var offsetX = Math.sin(yawRad) * 0.55;
            var offsetZ = -Math.cos(yawRad) * 0.55;

            var adjustedX = this.getX() + offsetX - this.getDeltaMovement().x;
            var adjustedY = this.getY() + 0.3 + floatingValue - this.getDeltaMovement().y;
            var adjustedZ = this.getZ() + offsetZ - this.getDeltaMovement().z;

            this.level().addParticle(new DustParticleOptions(new Vector3f(0.4f, 0.25f, 0f), 2.0f),
                    adjustedX,
                    adjustedY,
                    adjustedZ,
                    this.getDeltaMovement().x * -0.1,
                    this.getDeltaMovement().y * 0.1,
                    this.getDeltaMovement().z * -0.1);
        }
    }

    private void updateFloatingValue() {
        this.prevFloatingValue = this.floatingValue;
        this.floatingCounts += 0.05f;
        if (this.floatingCounts > 6.28f) this.floatingCounts -= 6.28f;
        this.floatingValue = 0.1f * Mth.sin(2 * this.floatingCounts);
    }

    public float getFloatingValue(float partialTick) {
        return Mth.lerp(partialTick, this.prevFloatingValue, this.floatingValue);
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity livingEntity) {
        var vec3 = getCollisionHorizontalEscapeVector(this.getBbWidth() * Mth.SQRT_OF_TWO, livingEntity.getBbWidth(), livingEntity.getYRot());
        var d0 = this.getX() + vec3.x;
        var d1 = this.getZ() + vec3.z;
        var blockpos = BlockPos.containing(d0, this.getBoundingBox().maxY, d1);
        var blockpos1 = blockpos.below();
        if (!this.level().isWaterAt(blockpos1)) {
            var list = Lists.newArrayList();
            var d2 = this.level().getBlockFloorHeight(blockpos);
            if (DismountHelper.isBlockFloorValid(d2)) {
                list.add(new Vec3(d0, (double)blockpos.getY() + d2, d1));
            }

            var d3 = this.level().getBlockFloorHeight(blockpos1);
            if (DismountHelper.isBlockFloorValid(d3)) {
                list.add(new Vec3(d0, (double)blockpos1.getY() + d3, d1));
            }

            for (Pose pose : livingEntity.getDismountPoses()) {
                for (var vec31 : list) {
                    if (DismountHelper.canDismountTo(this.level(), (Vec3)vec31, livingEntity, pose)) {
                        livingEntity.setPose(pose);
                        return (Vec3)vec31;
                    }
                }
            }
        }

        return super.getDismountLocationForPassenger(livingEntity);
    }

    protected void clampRotation(Entity entityToUpdate) {
        entityToUpdate.setYBodyRot(this.getYRot());
        float f = Mth.wrapDegrees(entityToUpdate.getYRot() - this.getYRot());
        float f1 = Mth.clamp(f, -105.0F, 105.0F);
        entityToUpdate.yRotO += f1 - f;
        entityToUpdate.setYRot(entityToUpdate.getYRot() + f1 - f);
        entityToUpdate.setYHeadRot(entityToUpdate.getYRot());
    }

    @Override
    public void onPassengerTurned(Entity entityToUpdate) {
        this.clampRotation(entityToUpdate);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        this.writeLeashData(compound, this.leashData);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.leashData = this.readLeashData(compound);
    }

    @Nullable
    @Override
    public Leashable.LeashData getLeashData() {
        return this.leashData;
    }

    @Override
    public void setLeashData(@Nullable Leashable.LeashData leashData) {
        this.leashData = leashData;
    }

    @NotNull
    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.88F * this.getEyeHeight(), this.getBbWidth() * 0.64F);
    }

    @Override
    public void elasticRangeLeashBehaviour(Entity leashHolder, float distance) {
        var vec3 = leashHolder.position().subtract(this.position()).normalize().scale((double)distance - 6.0);
        var vec31 = this.getDeltaMovement();
        var flag = vec31.dot(vec3) > 0.0;
        this.setDeltaMovement(vec31.add(vec3.scale(flag ? 0.15F : 0.2F)));
    }
}
