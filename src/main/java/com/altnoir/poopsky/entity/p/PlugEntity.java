package com.altnoir.poopsky.entity.p;

import com.altnoir.poopsky.event.KeyBoardInput;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.utill.Config;
import net.minecraft.BlockUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class PlugEntity extends Boat {
    public PlugEntity(EntityType<? extends Boat> type, Level level) {
        super(type, level);
    }

    private float fl = 0;
    private float serials = 0;
    private float lastfl = 0;

    private float playerSpeed =  0.9f;

    private boolean forward = false;
    private boolean backward = false;
    public boolean left = false;
    private boolean right = false;
    private boolean up = false;
    private boolean down = false;

    private static final EntityDataAccessor<Boolean> controlMode = SynchedEntityData.defineId(PlugEntity.class,EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor <Integer> playerLevel = SynchedEntityData.defineId(PlugEntity.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor <Integer> configSpeed = SynchedEntityData.defineId(PlugEntity.class,EntityDataSerializers.INT);
    private static final EntityDataAccessor <Boolean> configAdvancedMode = SynchedEntityData.defineId(PlugEntity.class, EntityDataSerializers.BOOLEAN);


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        this.entityData.define(controlMode, false);
        this.entityData.define(playerLevel, 0);
        this.entityData.define(configSpeed, 100);
        this.entityData.define(configAdvancedMode, true);
        super.defineSynchedData(builder);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

        entityData.set(controlMode,compound.getBoolean("controlMode"));
        entityData.set(playerLevel,compound.getInt("playerLevel"));
        super.readAdditionalSaveData(compound);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("controlMode", entityData.get(controlMode));
        compound.putInt("playerLevel",entityData.get(playerLevel));
        super.addAdditionalSaveData(compound);
    }

    public boolean getControlMode() {
        return entityData.get(controlMode);
    }

    public void setControlMode(boolean controlMode) {
        entityData.set(PlugEntity.controlMode, controlMode);
    }

    private void superTick(){
        this.oldStatus = this.status;
        this.status = Status.IN_WATER;
        this.outOfControlTicks = 0.0F;
        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);

        }
        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }
        if (!this.level().isClientSide) {
            this.setSharedFlag(6, this.isCurrentlyGlowing());
        }
        this.baseTick();
        this.tickLerp();
    }




    public void floatBoat() {

        double d1 = 0.0D;
        if (getPassengers().isEmpty()){
            d1 = -0.01f;
        }
        double momentum = playerSpeed;
        Vec3 vector3d = this.getDeltaMovement();
        Vec3 v = new Vec3(vector3d.x * momentum, vector3d.y + d1, vector3d.z * momentum);
        this.setDeltaMovement(v);
        this.deltaRotation *= 0.85;


    }

    @Override
    public @NotNull Item getDropItem() {
        return PSItems.Toilet_Plug.get();
    }

    @Override
    public void controlBoat() {
        if (this.isVehicle()) {
            float f = 0.0F;
            if (this.inputLeft) {
                --this.deltaRotation;
            }

            if (this.inputRight) {
                ++this.deltaRotation;
            }

            if (this.inputRight != this.inputLeft && !this.inputUp && !this.inputDown) {
                f += 0.005F;
            }
            double percent = entityData.get(configSpeed)/100.0;

            this.yRot += this.deltaRotation;
            if (this.inputUp) {
                f += 0.04F*playerSpeed*2;
            }

            if (this.inputDown) {
                f -= 0.01F*playerSpeed*4;
            }

            Vec3 v3d = this.getDeltaMovement().add((double)(Mth.sin(-this.yRot * ((float)Math.PI / 180F)) * f), 0.0D, (double)(Mth.cos(this.yRot * ((float)Math.PI / 180F)) * f));
            float currentY = (float) v3d.y;
            float maxYspeed = 0.3f*playerSpeed*2;
            float yacc = 0.05f*playerSpeed*1.5f;
            float ydec = 0.02f;
            if (currentY >0){
                if (up&&down){
                }else if(up){
                    if(currentY+yacc>maxYspeed){
                        currentY = maxYspeed;
                    }else {
                        currentY += yacc;
                    }
                }else if (down){
                    currentY -= yacc;
                }else {
                    currentY -= ydec;
                }
            }else {
                if (up&&down){
                }else if(down){
                    if(currentY-yacc<-maxYspeed){
                        currentY = -maxYspeed;
                    }else {
                        currentY -= yacc;
                    }
                }else if (up){
                    currentY += yacc;
                }else {
                    currentY += ydec;
                }
            }

            if(Math.abs(currentY)<=0.03){
                currentY = 0;
            }
            this.setDeltaMovement(v3d.x,currentY,v3d.z);

            this.setPaddleState(this.inputRight && !this.inputLeft || this.inputUp, this.inputLeft && !this.inputRight || this.inputUp);
        }

    }
    public boolean hasPassenger = false;
    public char summonParticle = 0;

    public void collision(){
        this.checkInsideBlocks();
        List<Entity> list = this.level().getEntities(this, this.getBoundingBox().inflate((double)0.2F, (double)-0.01F, (double)0.2F), EntitySelector.pushableBy(this));
        if (!list.isEmpty()) {
            boolean flag = !this.level().isClientSide && !(this.getControllingPassenger() instanceof Player);

            for(int j = 0; j < list.size(); ++j) {
                Entity entity = list.get(j);
                if (!entity.hasPassenger(this)) {
                    if (flag && this.getPassengers().size() < 2 && !entity.isPassenger() && entity.getBbWidth() < this.getBbWidth() && entity instanceof LivingEntity && !(entity instanceof WaterAnimal) && !(entity instanceof Player)) {
                        entity.startRiding(this);
                    } else {
                        this.push(entity);
                    }
                }
            }
        }
    }

    public boolean checkBlockCollision(AABB axisalignedbb) {
        BlockPos blockpos = new BlockPos((int) (axisalignedbb.minX + 0.001D), (int) (axisalignedbb.minY + 0.001D), (int) (axisalignedbb.minZ + 0.001D));
        BlockPos blockpos1 = new BlockPos((int) (axisalignedbb.maxX - 0.001D), (int) (axisalignedbb.maxY - 0.001D), (int) (axisalignedbb.maxZ - 0.001D));
        BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();
        for (int i = blockpos.getX(); i <= blockpos1.getX(); ++i) {
            for (int j = blockpos.getY(); j <= blockpos1.getY(); ++j) {
                for (int k = blockpos.getZ(); k <= blockpos1.getZ(); ++k) {
                    blockpos$mutable.set(i, j, k);
                    BlockState blockstate = this.level().getBlockState(blockpos$mutable);
                    if (blockstate.canOcclude()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isAdvancedMode(){
        return entityData.get(configAdvancedMode);
    }

    @Override
    public void tick() {
        Entity passenger = this.getFirstPassenger();
        if (passenger instanceof Player player && !Config.SHIFT_TO_DISMOUNT.get()){
            player.setShiftKeyDown(false);
        }
        if (!level().isClientSide){
            entityData.set(configSpeed, Config.MAX_SPEED.get());
            entityData.set(configAdvancedMode,Config.ADVANCED_MODE.get());
        }
//        System.out.println();
        if(level().isClientSide){
            updateControl();

        }
        summonParticle++;
        if(summonParticle == 3) {
            addParticle(this.level(), getX() - 0.5, getY() + 0.5, getZ() - 0.4, 1, 0, 1, ParticleTypes.CRIMSON_SPORE);
            summonParticle = 0;
        }
        superTick();
        if (this.isControlledByLocalInstance()) {

            if (this.getPassengers().isEmpty() || !(this.getPassengers().get(0) instanceof Player)) {
                this.setPaddleState(false, false);
            }

            this.floatBoat();
            if (this.level().isClientSide) {
                this.controlBoat();
                this.level().sendPacketToServer(new ServerboundPaddleBoatPacket(this.getPaddleState(0), this.getPaddleState(1)));
            }
            Vec3 vector3d = new Vec3(-this.getDeltaMovement().z,this.getDeltaMovement().y,this.getDeltaMovement().x);
            double percent = entityData.get(configSpeed)/100.0;
            vector3d = vector3d.multiply(percent,1,percent);

            Vec3 v1 = this.position();
            boolean flag1 = checkBlockCollision(this.getBoundingBox().move(0,-0.1,0));
            boolean flag2 =  checkBlockCollision(this.getBoundingBox().move(vector3d.add(0,-0.1,0)));
            boolean flag3 =  checkBlockCollision(this.getBoundingBox().move(vector3d.add(0,0.2,0)));
            if(!flag1 && flag2 && !flag3){
//                System.out.println(1111);
                vector3d = vector3d.add(0,0.1,0);

            }
            if (this.getStatus() == Status.IN_WATER){
                if (vector3d.y<0){
                    vector3d = new Vec3(vector3d.x,0,vector3d.z);
                }
            }
            this.move(MoverType.SELF, vector3d);
            Vec3 v2 = this.position().subtract(v1);


            if(v2.subtract(vector3d).lengthSqr()>0.0001){
                this.setDeltaMovement(v2.z, v2.y, -v2.x);
//                System.out.println(this.level.getBlockState(this.getPosition()).isSolid()+" "+this.getPosition());
            }else {

            }


        }

        else {
            this.setDeltaMovement(Vec3.ZERO);
        }

//        collision();


        if (!this.getPassengers().isEmpty()){
            Entity entity = this.getPassengers().get(0);


            playerSpeed = (float) (0.98-(0.7/((60/3)-9)));



            hasMajoWearable = false;
            entity.getArmorSlots().forEach((a)->{
                if (a.getItem() instanceof MajoWearableItem){
                    hasMajoWearable = true;
                }
            });
            if (!hasMajoWearable){
                playerSpeed = Math.max(0,playerSpeed-0.06f);
            }

        }else {
            playerSpeed = 0.9f;
        }
        serials += 0.05;
        if (serials > 6.28){
            serials -=  6.28;
        }

        lastfl = fl;
        fl = (float) Math.sin(2*serials);

    }
    private boolean hasMajoWearable;
    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public Vec3 getRelativePortalPosition(Direction.Axis axis, BlockUtil.FoundRectangle portal) {
        return LivingEntity.resetForwardDirectionOfRelativePortalPosition(super.getRelativePortalPosition(axis, portal));
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        if(!getControlMode()) {
            passenger.yRotO += deltaRotation;
            passenger.yRot += deltaRotation;
            passenger.setYHeadRot(passenger.yRot);
            passenger.setYBodyRot(this.yRot);



        }else {
            float f = Mth.wrapDegrees(passenger.yRot - this.yRot);
            float f1 = Mth.clamp(f, -105.0F, 105.0F);
            passenger.yRotO += f1 - f;
            passenger.yRot += f1 - f;
            passenger.setYHeadRot(passenger.yRot);
            passenger.setYBodyRot(this.yRot);


        }
        passenger.setPos(this.getX(), this.getY() + this.getMyRidingOffset() + passenger.getMyRidingOffset() +fl*0.1 + 0.5, this.getZ());
        super.positionRider(passenger, callback);
    }

    @Override
    protected void checkInsideBlocks() {
        super.checkInsideBlocks();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof Player player) {
            if(level().isClientSide){
                if (Minecraft.getInstance().player != null) {
                    if(Minecraft.getInstance().options.keyShift.isDown() && player.getUUID() == Minecraft.getInstance().player.getUUID()) {
                        Networking.INSTANCE.sendToServer(new RidePack(this.getId(), false));
                        this.level().playSound(player, player.blockPosition(), SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 10F, 1f);
                        addParticle(this.level(), getX() - 0.5, getY() + 0.3, getZ() - 0.5, 30, 2, 1, ParticleTypes.SMOKE);
                    }else {
                        //                    Networking.INSTANCE.sendToServer(new ChangeBroomStatePack(this.getId(), true));
                    }
                }
            }

            return false;
        }
        return false;
    }


    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public float getRealFl(float partialTicks) {
        return (1-partialTicks) *lastfl+fl*partialTicks;
    }

    @Override
    protected boolean canRide(Entity entityIn) {
        return true;
    }

    public void addParticle(Level level, double tx, double ty, double tz, int number, double width, double height, ParticleOptions iParticleData){
        for (int i = 0; i<number; i++){
            double x = Math.random()*width-0.5*width;
            double y = Math.random()*height-0.5*height;
            double z = Math.random()*width-0.5*width;
            level.addParticle(iParticleData,tx+x+0.5, ty+y, tz+z+0.5, 0,0,0);
        }
    }
//




    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {

        if(this.level().isClientSide){
            if(Minecraft.getInstance().options.keyShift.isDown()){
            }else {
                hasPassenger = true;
                if (this.getPassengers().isEmpty()) {
                    assert Minecraft.getInstance().player != null;
                    Networking.INSTANCE.sendToServer(new RidePack(this.getId(), true));
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void setControls(boolean forward, boolean backward, boolean left, boolean right, boolean up,boolean down){
//        this.forward = forward;
//        this.backward = backward;
//        this.left = left;
//        this.right = right;
//        this.up = up;
//        this.down = down;
    }

    @OnlyIn(Dist.CLIENT)
    private void updateControl(){
        if(!getPassengers().isEmpty()){

            assert Minecraft.getInstance().player != null;
            if (getPassengers().get(0).getUUID() == Minecraft.getInstance().player.getUUID()){
                forward = Minecraft.getInstance().options.keyUp.isDown();
                backward = Minecraft.getInstance().options.keyDown.isDown();
                left = Minecraft.getInstance().options.keyLeft.isDown();
                right = Minecraft.getInstance().options.keyRight.isDown();
                up = KeyBoardInput.up;
                down = KeyBoardInput.down;
                if (KeyBoardInput.DOWN_KEY.getKey().getValue() == GLFW.GLFW_KEY_LEFT_CONTROL && Minecraft.getInstance().options.keySprint.getKey().getValue() == GLFW.GLFW_KEY_LEFT_CONTROL){
                    down = Minecraft.getInstance().options.keySprint.isDown();
                }

            }else {
                forward = backward = left = right = up = down =false;
            }
        }else {

            forward = backward = left = right = up = down =false;
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

//    @Override
//    protected boolean canTriggerWalking() {
//        return false;
//    }

    @Override
    public void playerTouch(Player entityIn) {
        super.playerTouch(entityIn);
    }


    @Override
    public boolean canCollideWith(Entity entity) {
        return super.canCollideWith(entity);
    }



}
