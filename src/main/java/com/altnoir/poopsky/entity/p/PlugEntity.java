package com.altnoir.poopsky.entity.p;

import com.altnoir.poopsky.event.PSKeyBoardInput;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.sound.TPFlySoundWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class PlugEntity extends Boat {
    public float floatingValue = 0;
    public float floatingCounts = 0;
    public float prevFloatingValue = 0;

    private Player passenger;
    private TPFlySoundWrapper TPFlySound = null;

    private boolean isForward = false;
    private boolean isBackward = false;
    private boolean isLeft = false;
    private boolean isRight = false;
    public boolean isUp = false;
    public boolean isDown = false;
    private boolean isFast = false;

    private float verVelocity = 0;
    private static final float ACCELERATION = 0.1f;

    private int smooth = 0;
    private double smoothX, smoothY, smoothZ;
    private float smoothYaw, smoothPitch;

    public PlugEntity(EntityType<? extends Boat> type, Level level) {
        super(type, level);
        if (level.isClientSide) {
            TPFlySound = new TPFlySoundWrapper(this);
        }
    }

    @Override
    public void tick() {
        super.tick();
        autoFall();

        if (this.level().isClientSide) {
            handleClientTick();
        }

        updateFloatingValue();
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClientTick() {
        var mc = Minecraft.getInstance();
        var clientPlayer = mc.player;
        if (clientPlayer == null) return;

        if (passenger != null) {
            if (!this.hasPassenger(passenger)) {
                TPFlySound.stop();
                passenger = null;
            }
        } else {
            if (this.hasPassenger(clientPlayer)) {
                TPFlySound.play();
                passenger = clientPlayer;
            }
        }

        updateKeyStates(mc);

        if (isLogicalSideForUpdatingMovement()){
            moveByInput();
        }

        if (this.hasPassengers()) {
            spawnParticles();
        }
        smoothMovementFromOtherPlayer();
    }

    public boolean hasPassengers() {
        return !this.getPassengers().isEmpty();
    }

    private boolean isLogicalSideForUpdatingMovement() {
        if (this.level().isClientSide) {
            return isClientControllingThis();
        }
        return this.isEffectiveAi();
    }

    @OnlyIn(Dist.CLIENT)
    private boolean isClientControllingThis() {
        var player = Minecraft.getInstance().player;
        return player != null && player.isPassenger() && player.getVehicle() == this;
    }

    @OnlyIn(Dist.CLIENT)
    private void updateKeyStates(Minecraft mc) {
        isForward = mc.options.keyUp.isDown();
        isBackward = mc.options.keyDown.isDown();
        isLeft = mc.options.keyLeft.isDown();
        isRight = mc.options.keyRight.isDown();
        isUp = PSKeyBoardInput.UP_KEY.isDown();
        isDown = PSKeyBoardInput.DOWN_KEY.isDown();

        boolean isSprintingNow = mc.options.keySprint.isDown();
        if (isSprintingNow) isFast = true;
        if (!isMoving()) isFast = false;
    }

    private boolean isMoving() {
        return isForward || isBackward || isLeft || isRight || isUp || isDown;
    }

    private void moveByInput() {
        var MAX_SPEED = isFast ? 0.7f : 0.35f;
        var FBSpeed = isForward ? MAX_SPEED : isBackward ? -MAX_SPEED : 0f;
        var LRSpeed = isLeft ? MAX_SPEED : isRight ? -MAX_SPEED : 0f;

        var yawRad = (float) Math.toRadians(-this.getYRot());
        var forward = new Vec3(Math.sin(yawRad), 0, Math.cos(yawRad));
        var right = new Vec3(forward.z, 0, -forward.x);
        var movement = forward.scale(FBSpeed).add(right.scale(LRSpeed));

        var velocity = this.getDeltaMovement().add(movement.scale(ACCELERATION)).scale(0.975f);
        var targetY = (isUp ? MAX_SPEED : 0) + (isDown ? -MAX_SPEED : 0);
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
        if (!this.hasPassengers()) {
            this.setDeltaMovement(0, -0.1, 0);
            this.move(MoverType.SELF, this.getDeltaMovement());
        }
    }

    private void updateFloatingValue() {
        this.prevFloatingValue = this.floatingValue;
        this.floatingCounts += 0.05f;
        if (this.floatingCounts > 6.28f) this.floatingCounts -= 6.28f;
        this.floatingValue = 0.1f * Mth.sin(2 * this.floatingCounts);
    }

    private void smoothMovementFromOtherPlayer() {
        if (this.isLogicalSideForUpdatingMovement()) {
            this.smooth = 0;
        }

        if (this.smooth > 0) {
            double x = this.getX() + (this.smoothX - this.getX()) / this.smooth;
            double y = this.getY() + (this.smoothY - this.getY()) / this.smooth;
            double z = this.getZ() + (this.smoothZ - this.getZ()) / this.smooth;
            float yaw = this.getYRot() + (this.smoothYaw - this.getYRot()) / this.smooth;
            float pitch = this.getXRot() + (this.smoothPitch - this.getXRot()) / this.smooth;

            --this.smooth;
            this.setPos(x, y, z);
            this.setRot(yaw, pitch);
        }
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int steps) {
        super.lerpTo(x, y, z, yaw, pitch, steps);
        this.smoothX = x;
        this.smoothY = y;
        this.smoothZ = z;
        this.smoothYaw = yaw;
        this.smoothPitch = pitch;
        this.smooth = steps;
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
    public boolean isInvulnerableTo(DamageSource source) {
        return super.isInvulnerableTo(source) || source.is(DamageTypeTags.IS_FIRE);
    }

    @Override
    public boolean ignoreExplosion(Explosion explosion) {
        return true;
    }

    @Override
    public boolean isPushable() {
        return super.isPushable();
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
        double speed = this.getDeltaMovement().length();
        if (speed > 0.1) {
            float yawRad = (float) Math.toRadians(this.getYRot());
            double offsetX = Math.sin(yawRad) * 0.55;
            double offsetZ = -Math.cos(yawRad) * 0.55;

            this.level().addParticle(new DustParticleOptions(
                    new Vector3f(0.4f, 0.25f, 0f), 2.0f),
                    this.getX() + offsetX,
                    this.getY() + 0.3 + floatingValue,
                    this.getZ() + offsetZ,
                    this.getDeltaMovement().x * -0.1,
                    this.getDeltaMovement().y * 0.1,
                    this.getDeltaMovement().z * -0.1);
        }
    }

    public float getFloatingValue(float partialTick) {
        return Mth.lerp(partialTick, this.prevFloatingValue, this.floatingValue);
    }

    @Override
    protected boolean canAddPassenger(Entity entity) {
        return this.getPassengers().size() < 2;
    }

    @Override
    protected void positionRider(Entity entity, MoveFunction moveFunction) {
        super.positionRider(entity, moveFunction);
        if (this.getPassengers().indexOf(entity) == 1) {
            float yawRad = (float) Math.toRadians(-this.getYRot());
            double xOffset = Math.sin(yawRad) * 0.375;
            double zOffset = Math.cos(yawRad) * 0.375;
            entity.setPos(entity.getX() + xOffset, entity.getY() + 0.15 + floatingValue, entity.getZ() + zOffset);
        } else {
            entity.setPos(entity.getX(), entity.getY() + 0.15 + floatingValue, entity.getZ());
        }
    }

    @Override
    public Item getDropItem() {
        return PSItems.Toilet_Plug.get();
    }

    @Override
    protected @Nullable SoundEvent getPaddleSound() {
        return null;
    }
}