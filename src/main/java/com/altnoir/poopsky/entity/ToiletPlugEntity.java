package com.altnoir.poopsky.entity;

import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.sound.TPFlySoundWrapper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

public class ToiletPlugEntity extends BoatEntity {
    //悬浮动画参数
    public float floatingValue = 0;
    public float floatingCounts = 0;
    public float prevFloatingValue = 0;

    private Entity passenger;
    private TPFlySoundWrapper TPFlySound = null;

    public ToiletPlugEntity(EntityType<? extends BoatEntity> entityType, World world) {
        super(entityType, world);
        initializeSound();
    }
    private void initializeSound(){
        TPFlySound = new TPFlySoundWrapper(this);
    }

    @Override
    public Item asItem() {
        return PSItems.TOILET_PLUG;
    }
    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() < 2;
    }
    @Override
    protected void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        super.updatePassengerPosition(passenger, positionUpdater);
        passenger.setPosition(passenger.getX(),passenger.getY()+0.6 +floatingValue,passenger.getZ());
    }

    @Environment(EnvType.CLIENT)
    private void clientTick(){
        if (passenger != null){
            if ((!this.hasPassengers()) && this.getWorld().isClient && MinecraftClient.getInstance().player.getId() == passenger.getId()){
                TPFlySound.stop();
                passenger = null;
            }
        }
        else{
            if(this.hasPassengers() && this.getFirstPassenger().getId()==MinecraftClient.getInstance().player.getId()){
                TPFlySound.play();
                passenger = MinecraftClient.getInstance().player;
            }
        }
    }
    private boolean isForward = false;
    private boolean isBackward = false;
    private boolean isLeft = false;
    private boolean isRight = false;
    private boolean isUp = false;
    private boolean isDown = false;
    private boolean isFast = false;
    private void updateKeyStates(){
        isForward = MinecraftClient.getInstance().options.forwardKey.isPressed();
        isBackward = MinecraftClient.getInstance().options.backKey.isPressed();
        isLeft = MinecraftClient.getInstance().options.leftKey.isPressed();
        isRight = MinecraftClient.getInstance().options.rightKey.isPressed();
        isUp = MinecraftClient.getInstance().options.jumpKey.isPressed();
        isDown = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL);

        boolean isSprintingNow = MinecraftClient.getInstance().options.sprintKey.isPressed();

        if(isSprintingNow){
            isFast = true;
        }
        if(!isMoving()){
            isFast = false;
        }
    }
    private boolean isMoving(){
        return isForward || isBackward || isLeft || isRight || isUp || isDown;
    }
    // 下落
    private void autoFall() {
        if (!this.hasPassengers()) {
            this.setVelocity(0, -0.1, 0);
            this.move(MovementType.SELF, this.getVelocity());
        }
    }
    private float yawVelocity = 0;    // 当前转向速度
    private float verVelocity = 0;    // 当前垂直速度
    private static final float ACCELERATION = 0.1f;// 加速度系数
    @Override
    public void tick() {
        autoFall();
        if (this.getWorld().isClient()){
            clientTick();
            updateKeyStates();
            if(this.hasPassengers() && MinecraftClient.getInstance().player.getId() == this.getFirstPassenger().getId()){
                TPFlySound.tick();
            }
            if(isLogicalSideForUpdatingMovement()){
                //最大速度
                float MAX_SPEED;
                if (isFast){
                    MAX_SPEED = 0.7f;
                }else {
                    MAX_SPEED = 0.35f;
                }
                //前后运动
                float FBSpeed = 0f;
                if (isForward) FBSpeed = MAX_SPEED;
                if (isBackward) FBSpeed = -MAX_SPEED;
                //左右运动
                float LRSpeed = 0f;
                if (isLeft) LRSpeed = MAX_SPEED;
                if (isRight) LRSpeed = -MAX_SPEED;

                float yawRad = (float) Math.toRadians(-this.getYaw());
                Vec3d forwardVector = new Vec3d(Math.sin( yawRad ), 0, Math.cos( yawRad )).normalize();
                Vec3d rightVector = new Vec3d(forwardVector.z, 0, -forwardVector.x).normalize();

                Vec3d movement = forwardVector.multiply( FBSpeed ).add(rightVector.multiply( LRSpeed ));
                Vec3d newVelocity = this.getVelocity().add(movement.multiply(ACCELERATION)).multiply(0.975f);// 阻力

                //上下运动
                float target_g = 0;
                if (isUp) target_g += MAX_SPEED;
                if (isDown) target_g -= MAX_SPEED;
                verVelocity = (float) (verVelocity * (1 - ACCELERATION) + target_g * ACCELERATION);

                /* this.setYaw(this.getYaw()+yawVelocity);
                if (this.getFirstPassenger() !=null){
                Entity passenger = this.getFirstPassenger();
                passenger.setYaw(passenger.getYaw()+yawVelocity);
                }*/
                Entity passenger = this.getFirstPassenger();
                float targetYaw = passenger.getYaw(); // 目标朝向角度
                final float ROTATION_SPEED = 0.3f; // 旋转速度系数

                float deltaYaw = MathHelper.wrapDegrees(targetYaw - this.getYaw());
                this.setYaw(this.getYaw() + deltaYaw * ROTATION_SPEED);

                this.setVelocity(newVelocity.x, verVelocity, newVelocity.z);
                this.move(MovementType.SELF, this.getVelocity());
            }
            smoothMovementFromOtherPlayer();
        }
        updateFloatingValue();
    }

    private int smooth = 0;
    private double smoothX,smoothY,smoothZ;
    float smoothYaw,smoothPitch;
    private void smoothMovementFromOtherPlayer() {
        if (this.isLogicalSideForUpdatingMovement()) {
            this.smooth = 0;
            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
        }

        if (this.smooth > 0) {
            double d = this.getX() + (this.smoothX - this.getX()) / (double)this.smooth;
            double e = this.getY() + (this.smoothY - this.getY()) / (double)this.smooth;
            double f = this.getZ() + (this.smoothZ - this.getZ()) / (double)this.smooth;
            double g = MathHelper.wrapDegrees(this.smoothYaw - (double)this.getYaw());
            this.setYaw(this.getYaw() + (float)g / (float)this.smooth);
            this.setPitch(this.getPitch() + (float)(this.smoothPitch - (double)this.getPitch()) / (float)this.smooth);
            --this.smooth;
            this.setPosition(d, e, f);
            this.setRotation(this.getYaw(), this.getPitch());
        }
    }
    // 悬浮动画
    private void updateFloatingValue(){
        this.prevFloatingValue = this.floatingValue;
        this.floatingCounts += 0.05f;
        if (this.floatingCounts > 6.28f){
            this.floatingCounts -= 6.28f;
        }
        this.floatingValue = 0.1f * MathHelper.sin(2*this.floatingCounts);
    }
    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
        super.updateTrackedPositionAndAngles(x, y, z, yaw, pitch, interpolationSteps);
        this.smoothX = x;
        this.smoothY = y;
        this.smoothZ = z;
        this.smoothYaw = yaw;
        this.smoothPitch = pitch;
        this.smooth = 3;
    }
    @Override
    public boolean isCollidable() {
        // 设为false为无碰撞
        return super.isCollidable();
    }
    @Override
    protected void refreshPosition() {
        super.refreshPosition();
    }

    // 交互逻辑
    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient) {
            return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
        } else {
            return ActionResult.SUCCESS;
        }
    }
    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getSource() instanceof PlayerEntity player){
            this.kill();
            if (!player.isCreative()) {
                if(!player.getInventory().insertStack(PSItems.TOILET_PLUG.getDefaultStack())){
                    dropItem(asItem());
                }
            }
            return true;
        }
        return false;
    }
    public float getFloatingValue(float tickDelta){
        return MathHelper.lerp(tickDelta, this.prevFloatingValue, this.floatingValue);
    }
}
