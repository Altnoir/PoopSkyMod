package com.altnoir.poopsky.entity.p;

import com.altnoir.poopsky.init.PEntityType;
import com.altnoir.poopsky.item.PItems;
import com.altnoir.poopsky.PTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlyEntity extends Animal implements FlyingAnimal {
    public static final int TICKS_PER_FLAP = Mth.ceil(1.4959966F);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(FlyEntity.class, EntityDataSerializers.BYTE);

    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    public float flapping = 1.0F;
    public int eggTime = this.random.nextInt(6000) + 6000;

    private float rollAmount;
    private float rollAmountO;

    public FlyEntity(EntityType<FlyEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this)); // 防止溺水
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4)); // 受到伤害时逃跑
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0)); // 繁殖行为
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, itemStack -> itemStack.is(PTags.Items.POOPS), false));// 被便便引诱
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.25)); // 跟随父母
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 1.0)); // 随机飞行，避开水面
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F)); // 看向附近的玩家
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this)); // 随机环顾四周
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingPathNavigation = new FlyingPathNavigation(this, level);
        flyingPathNavigation.setCanOpenDoors(false);
        flyingPathNavigation.setCanFloat(true);
        flyingPathNavigation.setCanPassDoors(true);
        return flyingPathNavigation;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.FLYING_SPEED, 0.6F)
                .add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        
        // 根据是否在飞行调整翅膀扇动速度
        if (this.isFlying()) {
            this.flapSpeed = Mth.clamp(this.flapSpeed + 0.2F, 0.0F, 1.0F);
            if (this.flapping < 1.0F) {
                this.flapping = 1.0F;
            }
        } else {
            this.flapSpeed = Mth.clamp(this.flapSpeed - 0.2F, 0.0F, 1.0F);
            this.flapping *= 0.9F;
        }
        
        // 减缓下落速度
        Vec3 vec3 = this.getDeltaMovement();
        if (!this.onGround() && vec3.y < 0.0) {
            this.setDeltaMovement(vec3.x, vec3.y * 0.6, vec3.z);
        }
        this.flap = this.flap + this.flapping * 2.0F;
        
        // 产卵逻辑
        if (!this.level().isClientSide && this.isAlive() && !this.isBaby() && --this.eggTime <= 0) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.spawnAtLocation(PItems.MAGGOTS_SEEDS.get());
            this.gameEvent(GameEvent.ENTITY_PLACE);
            this.eggTime = this.random.nextInt(6000) + 6000;
        }
    }

    @Override
    public boolean isFlapping() {
        return this.isFlying() && this.tickCount % TICKS_PER_FLAP == 0;
    }

    @Override
    public boolean isFlying() {
        return !this.onGround();
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(PTags.Items.POOPS);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("EggLayTime")) {
            this.eggTime = compound.getInt("EggLayTime");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("EggLayTime", this.eggTime);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BEE_LOOP_AGGRESSIVE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.BEE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BEE_DEATH;
    }


    @Override
    public @Nullable FlyEntity getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return PEntityType.FLY.get().create(level);
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource damageSource) {
        return false;
    }

    public float getRollAmount(float partialTick) {
        return Mth.lerp(partialTick, this.rollAmountO, this.rollAmount);
    }

    private boolean getFlag(int flagId) {
        return (this.entityData.get(DATA_FLAGS_ID) & flagId) != 0;
    }

    public boolean hasStung() {
        return this.getFlag(4);
    }

    public static boolean checkFlySpawnRules(EntityType<FlyEntity> fly, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return MobSpawnType.ignoresLightRequirements(spawnType);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLAGS_ID, (byte) 0);
    }
}