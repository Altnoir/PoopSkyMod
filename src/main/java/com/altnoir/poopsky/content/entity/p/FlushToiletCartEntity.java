package com.altnoir.poopsky.content.entity.p;

import java.util.List;
import java.util.Map;

import com.altnoir.poopsky.impl.network.FlushToiletCartInputPayload;
import com.altnoir.poopsky.init.PoEffects;
import com.altnoir.poopsky.init.PoEntityType;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class FlushToiletCartEntity extends VehicleEntity {
    private static final Map<Pose, List<Integer>> POSE_DISMOUNT_HEIGHTS = Map.of(
            Pose.STANDING, List.of(0, 1, -1),
            Pose.CROUCHING, List.of(0, 1, -1),
            Pose.SWIMMING, List.of(0, 1)
    );

    private static final EntityDataAccessor<Float> WHEEL_LEFT_ROTATION =
            SynchedEntityData.defineId(FlushToiletCartEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> WHEEL_RIGHT_ROTATION =
            SynchedEntityData.defineId(FlushToiletCartEntity.class, EntityDataSerializers.FLOAT);

    private boolean inputForward;
    private boolean inputBackward;
    private boolean inputLeft;
    private boolean inputRight;
    private boolean inputFast;

    private float deltaRotation;
    private float currentSpeed;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private float lerpYRot;
    private float lerpXRot;

    public float wheelLeftRotation;
    public float wheelLeftRotationO;
    public float wheelRightRotation;
    public float wheelRightRotationO;

    public FlushToiletCartEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    @Override
    public float maxUpStep() {
        LivingEntity driver = this.getControllingPassenger();
        if (driver != null && driver.hasEffect(PoEffects.OMENER)) {
            return 1.0F;
        }
        return 0.5F;
    }

    @Override
    public Item getDropItem() {
        return this.getType().equals(PoEntityType.GOLDEN_FLUSH_TOILET_CART.get())
                ? PoItems.GOLDEN_FLUSH_TOILET_CART.get()
                : PoItems.FLUSH_TOILET_CART.get();
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(this.getDropItem());
    }

    @Override
    public void animateHurt(float yaw) {
        this.setHurtDir(-this.getHurtDir());
        this.setHurtTime(10);
        this.setDamage(this.getDamage() * 11.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(WHEEL_LEFT_ROTATION, 0.0F);
        builder.define(WHEEL_RIGHT_ROTATION, 0.0F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
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
    public boolean isVehicle() {
        return true;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        return player.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty();
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        super.positionRider(passenger, callback);
        if (passenger instanceof LivingEntity) {
            float turnDelta = this.getYRot() - this.yRotO;
            passenger.setYRot(passenger.getYRot() + turnDelta);
            passenger.setYHeadRot(passenger.getYHeadRot() + turnDelta);
            this.clampRotation(passenger);
        }
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        Direction direction = this.getMotionDirection();
        if (direction.getAxis() == Direction.Axis.Y) {
            return super.getDismountLocationForPassenger(passenger);
        }

        int[][] offsets = DismountHelper.offsetsForDirection(direction);
        BlockPos blockPos = this.blockPosition();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        List<Pose> poses = passenger.getDismountPoses();

        for (Pose pose : poses) {
            EntityDimensions dimensions = passenger.getDimensions(pose);
            float halfWidth = Math.min(dimensions.width(), 1.0F) / 2.0F;
            for (int heightOffset : POSE_DISMOUNT_HEIGHTS.get(pose)) {
                for (int[] offset : offsets) {
                    mutableBlockPos.set(blockPos.getX() + offset[0], blockPos.getY() + heightOffset, blockPos.getZ() + offset[1]);
                    double floorHeight = this.level().getBlockFloorHeight(
                            DismountHelper.nonClimbableShape(this.level(), mutableBlockPos),
                            () -> DismountHelper.nonClimbableShape(this.level(), mutableBlockPos.below())
                    );
                    if (DismountHelper.isBlockFloorValid(floorHeight)) {
                        AABB aabb = new AABB(
                                -halfWidth,
                                0.0,
                                -halfWidth,
                                halfWidth,
                                dimensions.height(),
                                halfWidth
                        );
                        Vec3 position = Vec3.upFromBottomCenterOf(mutableBlockPos, floorHeight);
                        if (DismountHelper.canDismountTo(this.level(), passenger, aabb.move(position))) {
                            passenger.setPose(pose);
                            return position;
                        }
                    }
                }
            }
        }

        double topY = this.getBoundingBox().maxY;
        mutableBlockPos.set((double) blockPos.getX(), topY, (double) blockPos.getZ());
        for (Pose pose : poses) {
            double passengerHeight = passenger.getDimensions(pose).height();
            int ceilingSearch = Mth.ceil(topY - mutableBlockPos.getY() + passengerHeight);
            double ceiling = DismountHelper.findCeilingFrom(
                    mutableBlockPos,
                    ceilingSearch,
                    pos -> this.level().getBlockState(pos).getCollisionShape(this.level(), pos)
            );
            if (topY + passengerHeight <= ceiling) {
                passenger.setPose(pose);
                break;
            }
        }

        return super.getDismountLocationForPassenger(passenger);
    }

    @Override
    public void onPassengerTurned(Entity entityToUpdate) {
        this.clampRotation(entityToUpdate);
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
        return this.lerpSteps > 0 ? this.lerpXRot : this.getXRot();
    }

    @Override
    public float lerpTargetYRot() {
        return this.lerpSteps > 0 ? this.lerpYRot : this.getYRot();
    }

    @Override
    public void tick() {
        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }
        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        super.tick();
        this.tickLerp();
        this.tickWheelRotationSync();

        boolean shouldProcessInput = this.isControlledByLocalInstance()
                || (!this.level().isClientSide && this.getControllingPassenger() != null);
        if (shouldProcessInput) {
            if (this.level().isClientSide) {
                this.updateKeyStates();
            }
            this.moveByInput();
            Vec3 deltaMovement = this.getDeltaMovement();
            if (!this.onGround()) {
                deltaMovement = deltaMovement.add(0, -0.08, 0);
            }
            this.setDeltaMovement(deltaMovement);
            this.move(MoverType.SELF, this.getDeltaMovement());
            if (this.horizontalCollision) {
                this.currentSpeed *= 0.6F;
            }
            this.updateWheelRotations();
        } else if (!this.level().isClientSide) {
            Vec3 deltaMovement = this.getDeltaMovement().multiply(0.9, 1, 0.9).add(0, -0.08, 0);
            if (this.onGround()) {
                deltaMovement = deltaMovement.multiply(0.9, 0, 0.9);
            }
            this.setDeltaMovement(deltaMovement);
            this.move(MoverType.SELF, deltaMovement);
            if (this.horizontalCollision) {
                this.currentSpeed *= 0.6F;
            }
            this.updateWheelRotations();
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }
        this.resetPassengerFallDistance();
    }

    private void resetPassengerFallDistance() {
        if (!this.isVehicle()) {
            return;
        }
        this.resetFallDistance();
        for (Entity passenger : this.getPassengers()) {
            passenger.resetFallDistance();
        }
    }

    public void setInput(boolean forward, boolean backward, boolean left, boolean right, boolean fast) {
        this.inputForward = forward;
        this.inputBackward = backward;
        this.inputLeft = left;
        this.inputRight = right;
        this.inputFast = fast;
    }

    @OnlyIn(Dist.CLIENT)
    private void updateKeyStates() {
        var mc = Minecraft.getInstance();
        boolean forward = mc.options.keyUp.isDown();
        boolean backward = mc.options.keyDown.isDown();
        boolean left = mc.options.keyLeft.isDown();
        boolean right = mc.options.keyRight.isDown();
        boolean fast = mc.options.keySprint.isDown();

        this.setInput(forward, backward, left, right, fast);
        PacketDistributor.sendToServer(new FlushToiletCartInputPayload(forward, backward, left, right, fast));
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

    private void moveByInput() {
        if (this.getControllingPassenger() instanceof Player driver) {
            float movementFriction = this.isInWater() ? 0.3F : 0.9F;
            this.deltaRotation *= movementFriction;

            if (this.inputLeft) {
                this.deltaRotation--;
            }
            if (this.inputRight) {
                this.deltaRotation++;
            }

            float targetSpeed = 0.0F;
            if (this.inputRight != this.inputLeft && !this.inputForward && !this.inputBackward) {
                targetSpeed = 0.005F / (1.0F - movementFriction);
            }
            if (this.inputForward) {
                targetSpeed = 0.04F / (1.0F - movementFriction);
            } else if (this.inputBackward) {
                targetSpeed = -0.04F / (1.0F - movementFriction);
            }

            this.setYRot(this.getYRot() + this.deltaRotation);
            if (this.inputFast) {
                targetSpeed *= 2.0F;
            }
            if (driver.hasEffect(PoEffects.OMENER)) {
                targetSpeed *= 2.0F;
            }

            if (targetSpeed == 0.0F) {
                float coastFriction = this.isInWater() ? 0.7F : 0.98F;
                this.currentSpeed *= coastFriction;
            } else {
                this.currentSpeed += (targetSpeed - this.currentSpeed) * 0.08F;
            }

            float yawRad = (float) Math.toRadians(-this.getYRot());
            Vec3 forward = new Vec3(Math.sin(yawRad), 0, Math.cos(yawRad));
            this.setDeltaMovement(new Vec3(forward.x * this.currentSpeed, this.getDeltaMovement().y, forward.z * this.currentSpeed));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 1, 0.9));
        }
    }

    private void updateWheelRotations() {
        float yawRad = (float) Math.toRadians(-this.getYRot());
        Vec3 forward = new Vec3(Math.sin(yawRad), 0, Math.cos(yawRad));
        float forwardSpeed = (float) this.getDeltaMovement().dot(forward);
        float turnDelta = this.getYRot() - this.yRotO;

        float wheelScale = 180.0F / (float) Math.PI / 0.5F;
        float leftWheelSpeed = forwardSpeed * wheelScale + turnDelta * 2.0F;
        float rightWheelSpeed = forwardSpeed * wheelScale - turnDelta * 2.0F;

        this.wheelLeftRotationO = this.wheelLeftRotation;
        this.wheelRightRotationO = this.wheelRightRotation;
        this.wheelLeftRotation += leftWheelSpeed;
        this.wheelRightRotation += rightWheelSpeed;
        this.entityData.set(WHEEL_LEFT_ROTATION, this.wheelLeftRotation);
        this.entityData.set(WHEEL_RIGHT_ROTATION, this.wheelRightRotation);
    }

    private void tickWheelRotationSync() {
        this.wheelLeftRotationO = this.wheelLeftRotation;
        this.wheelRightRotationO = this.wheelRightRotation;
        this.wheelLeftRotation = this.entityData.get(WHEEL_LEFT_ROTATION);
        this.wheelRightRotation = this.entityData.get(WHEEL_RIGHT_ROTATION);
    }

    public float getWheelLeftRotation(float partialTick) {
        return Mth.lerp(partialTick, this.wheelLeftRotationO, this.wheelLeftRotation);
    }

    public float getWheelRightRotation(float partialTick) {
        return Mth.lerp(partialTick, this.wheelRightRotationO, this.wheelRightRotation);
    }
}