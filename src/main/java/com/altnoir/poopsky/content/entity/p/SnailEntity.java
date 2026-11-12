package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.init.PoEntityType;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SnailEntity extends Animal {
    private static final ResourceLocation SPEED_MODIFIER = PoopSky.loc("snail_movement_speed");
    private static final double MAX_CLIMB_SPEED = 0.025;
    private static final EntityDataAccessor<Boolean> DATA_CLIMBING =
            SynchedEntityData.defineId(SnailEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_WALL_DIRECTION =
            SynchedEntityData.defineId(SnailEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_COLOR =
            SynchedEntityData.defineId(SnailEntity.class, EntityDataSerializers.INT);
    private static final int COLOR_COUNT = Color.values().length;

    public SnailEntity(EntityType<? extends SnailEntity> entityType, Level level) {
        super(entityType, level);
        this.applyMovementSpeedModifier();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.16)
                .add(Attributes.STEP_HEIGHT, 0.0);
    }

    @Override
    public Fallsounds getFallSounds() {
        return new Fallsounds(SoundEvents.GENERIC_SMALL_FALL, PoSoundEvents.ENTITY_SNAIL_HURT.get());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return PoSoundEvents.ENTITY_SNAIL_HURT.get();
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WallClimberNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 0.65));
        this.goalSelector.addGoal(2, new TemptGoal(this, 0.8, this::isFood, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.65));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_CLIMBING, false);
        builder.define(DATA_WALL_DIRECTION, Direction.NORTH.get3DDataValue());
        builder.define(DATA_COLOR, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            Direction wallDirection = this.horizontalCollision ? this.findWallDirection() : null;
            this.setClimbing(wallDirection != null);
            if (wallDirection != null) {
                this.entityData.set(DATA_WALL_DIRECTION, wallDirection.get3DDataValue());
            }
        }
    }

    @Override
    public boolean onClimbable() {
        return this.isClimbing();
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isClimbing() && this.horizontalCollision && !this.isInWaterOrBubble()) {
            Vec3 velocity = this.getDeltaMovement();
            if (velocity.y > MAX_CLIMB_SPEED) {
                this.setDeltaMovement(velocity.x, MAX_CLIMB_SPEED, velocity.z);
            }
        }
        super.travel(travelVector);
    }

    private void applyMovementSpeedModifier() {
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null && !speed.hasModifier(SPEED_MODIFIER)) {
            speed.addTransientModifier(new AttributeModifier(
                    SPEED_MODIFIER, 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    @Override
    public void jumpFromGround() {
    }

    public boolean isClimbing() {
        return this.entityData.get(DATA_CLIMBING);
    }

    public Direction getWallDirection() {
        Direction direction = Direction.from3DDataValue(this.entityData.get(DATA_WALL_DIRECTION));
        return direction.getAxis().isHorizontal() ? direction : Direction.NORTH;
    }

    private void setClimbing(boolean climbing) {
        this.entityData.set(DATA_CLIMBING, climbing);
    }

    @Nullable
    private Direction findWallDirection() {
        Direction facing = this.getDirection();
        if (this.touchesWall(facing)) {
            return facing;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (this.touchesWall(direction)) {
                return direction;
            }
        }
        return null;
    }

    private boolean touchesWall(Direction direction) {
        return !this.level().noCollision(this, this.getBoundingBox().move(
                direction.getStepX() * 0.05,
                0.0,
                direction.getStepZ() * 0.05
        ));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
        this.setColor(Color.byId(this.random.nextInt(COLOR_COUNT)));
        return super.finalizeSpawn(level, difficulty, reason, spawnData);
    }

    public Color getColor() {
        return Color.byId(this.entityData.get(DATA_COLOR));
    }

    public void setColor(Color color) {
        this.entityData.set(DATA_COLOR, color.ordinal());
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(PoTags.Items.SNAIL_FOOD);
    }

    @Nullable
    @Override
    public SnailEntity getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        SnailEntity child = PoEntityType.SNAIL.get().create(level);
        if (child != null) {
            child.setColor(Color.byId(this.random.nextInt(COLOR_COUNT)));
        }
        return child;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Color", this.getColor().ordinal());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.applyMovementSpeedModifier();
        this.setColor(Color.byId(tag.getInt("Color")));
    }

    public enum Color {
        YELLOW,
        RED,
        BROWN,
        BLACK;

        public static Color byId(int id) {
            return values()[Math.floorMod(id, COLOR_COUNT)];
        }
    }
}
