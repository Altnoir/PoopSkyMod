package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.client.sound.FlyBuzzSoundWrapper;
import com.altnoir.poopsky.content.block.p.ShitBlock;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.impl.type.damageType.PoDamageTypes;
import com.altnoir.poopsky.init.FlyTypes;
import com.altnoir.poopsky.init.PoEntityType;
import com.altnoir.poopsky.init.PoItems;
import com.altnoir.poopsky.init.PoSoundEvents;
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
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class FlyEntity extends Animal implements FlyingAnimal {
    public static final int TICKS_PER_FLAP = Mth.ceil(1.4959966F);
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(FlyEntity.class, EntityDataSerializers.BYTE);

    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    public float flapping = 1.0F;
    public int eggTime = this.random.nextInt(6000) + 6000;

    private FlyBuzzSoundWrapper buzzSound;

    public FlyEntity(EntityType<FlyEntity> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        this.setPathfindingMalus(PathType.DANGER_FIRE, -1.0F);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 16.0F);
        this.setPathfindingMalus(PathType.COCOA, -1.0F);
        this.setPathfindingMalus(PathType.FENCE, -1.0F);
        if (level.isClientSide()) {
            initClient();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void initClient() {
        buzzSound = new FlyBuzzSoundWrapper(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this)); // 防止溺水
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4)); // 受到伤害时逃跑
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0)); // 繁殖行为
        this.goalSelector.addGoal(3, new AttractedByShitGoal(this, 1.25));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, itemStack -> itemStack.is(PoTags.Items.FLY_LIKE), false));
        this.goalSelector.addGoal(4, new FlyToToiletGoal(this, 1.1)); // 主动寻找厕所
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.25)); // 跟随父母
        this.goalSelector.addGoal(6, new WaterAvoidingRandomFlyingGoal(this, 1.0)); // 随机飞行，避开水面
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F)); // 看向附近的玩家
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this)); // 随机环顾四周
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
        if (this.level().isClientSide() && buzzSound != null) {
            buzzSound.tick();
        }
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
        if (!this.level().isClientSide() && this.isAlive() && !this.isBaby() && --this.eggTime <= 0) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.spawnAtLocation(PoItems.MAGGOTS_SEEDS.get());
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
        return stack.is(PoTags.Items.FLY_LIKE);
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
    public void remove(Entity.RemovalReason reason) {
        if (this.level().isClientSide() && buzzSound != null) {
            buzzSound.stop();
        }
        super.remove(reason);
    }

    @Override
    public int getAmbientSoundInterval() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return PoSoundEvents.ENTITY_FLY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return PoSoundEvents.ENTITY_FLY_DEATH.get();
    }

    @Override
    public @Nullable FlyEntity getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return PoEntityType.FLY.get().create(level);
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide() && !this.isBaby()) {
            if (source.is(DamageTypes.DROWN)) {
                var blueFlyItem = FlyItem.withType(FlyTypes.BLUE.get());
                this.spawnAtLocation(blueFlyItem);
            }
            if (source.is(PoDamageTypes.ROUNDWORM)) {
                var whiteFlyItem = FlyItem.withType(FlyTypes.WHITE.get());
                this.spawnAtLocation(whiteFlyItem);
            }
            if (source.is(DamageTypes.CACTUS)) {
                var greenFlyItem = FlyItem.withType(FlyTypes.GREEN.get());
                var itemEntity = this.spawnAtLocation(greenFlyItem);
                if (itemEntity != null) {
                    itemEntity.setInvulnerable(true);
                }
            }
            if (source.is(PoDamageTypes.POOP_BALL)) {
                var brownFlyItem = FlyItem.withType(FlyTypes.BROWN.get());
                this.spawnAtLocation(brownFlyItem);
            }
        }
        super.die(source);
    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt lightning) {
        if (!this.level().isClientSide() && !this.isBaby()) {
            var blackFlyItem = FlyItem.withType(FlyTypes.BLACK.get());
            var itemEntity = this.spawnAtLocation(blackFlyItem);
            if (itemEntity != null) {
                itemEntity.setInvulnerable(true);
            }
            this.kill();
        }
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource damageSource) {
        return false;
    }

    public static boolean checkFlySpawnRules(EntityType<FlyEntity> fly, LevelAccessor level, EntitySpawnReason spawnType, BlockPos pos, RandomSource random) {
        return EntitySpawnReason.isSpawner(spawnType);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLAGS_ID, (byte) 0);
    }

    private static class AttractedByShitGoal extends Goal {
        private static final double STOP_DISTANCE_SQR = 6.25;
        private static final TargetingConditions TARGETING = TargetingConditions.forNonCombat()
                .range(12.0)
                .ignoreLineOfSight()
                .selector(ShitBlock::isWearing);

        private final FlyEntity fly;
        private final double speedModifier;
        @Nullable
        private Player player;

        private AttractedByShitGoal(FlyEntity fly, double speedModifier) {
            this.fly = fly;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            this.player = this.fly.level().getNearestPlayer(TARGETING, this.fly);
            return this.player != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.player != null && TARGETING.test(this.fly, this.player);
        }

        @Override
        public void stop() {
            this.player = null;
            this.fly.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (this.player == null) {
                return;
            }
            this.fly.getLookControl().setLookAt(
                    this.player,
                    this.fly.getMaxHeadYRot() + 20,
                    this.fly.getMaxHeadXRot()
            );
            if (this.fly.distanceToSqr(this.player) < STOP_DISTANCE_SQR) {
                this.fly.getNavigation().stop();
            } else {
                this.fly.getNavigation().moveTo(this.player, this.speedModifier);
            }
        }
    }

    private static class FlyToToiletGoal extends Goal {
        private static final int SEARCH_RADIUS = 12;
        private static final int VERTICAL_SEARCH_RADIUS = 6;
        private static final int SEARCH_COOLDOWN = 40;

        private final FlyEntity fly;
        private final double speedModifier;
        private BlockPos targetPos;
        private long nextSearchTick;

        private FlyToToiletGoal(FlyEntity fly, double speedModifier) {
            this.fly = fly;
            this.speedModifier = speedModifier;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.fly.level().getGameTime() < this.nextSearchTick) {
                return false;
            }
            this.nextSearchTick = this.fly.level().getGameTime() + SEARCH_COOLDOWN + this.fly.getRandom().nextInt(SEARCH_COOLDOWN);
            this.targetPos = this.findNearestToilet();
            return this.targetPos != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.targetPos != null
                    && !this.fly.getNavigation().isDone()
                    && this.fly.level().getBlockState(this.targetPos).is(PoTags.Blocks.FLY_LOVE)
                    && this.fly.blockPosition().distSqr(this.targetPos) > 4.0;
        }

        @Override
        public void start() {
            this.moveToTarget();
        }

        @Override
        public void tick() {
            if (this.targetPos != null && this.fly.tickCount % 20 == 0) {
                this.moveToTarget();
            }
        }

        private void moveToTarget() {
            this.fly.getNavigation().moveTo(
                    this.targetPos.getX() + 0.5,
                    this.targetPos.getY() + 1.0,
                    this.targetPos.getZ() + 0.5,
                    this.speedModifier
            );
        }

        @Nullable
        private BlockPos findNearestToilet() {
            BlockPos origin = this.fly.blockPosition();
            BlockPos nearest = null;
            double nearestDistance = Double.MAX_VALUE;
            for (BlockPos pos : BlockPos.withinManhattan(origin, SEARCH_RADIUS, VERTICAL_SEARCH_RADIUS, SEARCH_RADIUS)) {
                if (!this.fly.level().getBlockState(pos).is(PoTags.Blocks.FLY_LOVE)) {
                    continue;
                }
                double distance = origin.distSqr(pos);
                if (distance < nearestDistance) {
                    nearest = pos.immutable();
                    nearestDistance = distance;
                }
            }
            return nearest;
        }
    }
}
