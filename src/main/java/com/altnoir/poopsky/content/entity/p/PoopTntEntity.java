package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.impl.util.PoopTntUtil;
import com.altnoir.poopsky.init.PoEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PoopTntEntity extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID = SynchedEntityData.defineId(PoopTntEntity.class, EntityDataSerializers.INT);

    private static final int DEFAULT_FUSE_TIME = 80;
    private static final double MOMENTUM_PER_TICK = 1.0;
    private static final int MIN_EXPLOSION_RADIUS = 1;
    private static final int MAX_EXPLOSION_RADIUS = 9;
    private static final double INSTANT_EXPLOSION_THRESHOLD = 0.5;

    @Nullable
    private LivingEntity owner;

    public PoopTntEntity(EntityType<? extends PoopTntEntity> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    public PoopTntEntity(Level level, double x, double y, double z, @Nullable LivingEntity owner) {
        this(PoEntityType.POOP_TNT.get(), level);
        this.setPos(x, y, z);
        double d = level.getRandom().nextDouble() * (double) ((float) Math.PI * 2F);
        this.setDeltaMovement(-Math.sin(d) * 0.02, 0.2F, -Math.cos(d) * 0.02);
        this.setFuse(DEFAULT_FUSE_TIME);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.owner = owner;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_FUSE_ID, DEFAULT_FUSE_TIME);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public void tick() {
        this.handlePortal();

        Direction logFacing = this.getLogFacing();
        if (logFacing == null || logFacing == Direction.DOWN) {
            this.applyGravity();
        }

        Vec3 movement = this.getDeltaMovement();
        double impactSpeed = movement.length();
        this.move(MoverType.SELF, movement);
        int radius = calculateExplosionRadius(impactSpeed);

        if (!this.level().isClientSide()) {
            var state = this.level().getBlockState(this.getOnPos());

            if (impactSpeed > INSTANT_EXPLOSION_THRESHOLD && !state.is(PoTags.Blocks.EMPTY_LOGS) && (this.horizontalCollision || this.verticalCollision)) {
                this.setDeltaMovement(movement);
                this.discard();
                PoopTntUtil.triggerExplosion(this, radius + 1);
                return;
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
        }
        this.applyLogPushing(logFacing);
        int fuse = this.getFuse() - 1;
        this.setFuse(fuse);
        if (fuse <= 0) {
            this.discard();
            if (!this.level().isClientSide()) {
                PoopTntUtil.triggerExplosion(this, radius + 1);
            }
        } else {
            this.updateInWaterStateAndDoFluidPushing();
            if (this.level().isClientSide()) {
                this.level().addParticle(ParticleTypes.SMOKE,
                        this.getX(), this.getY() + 0.5, this.getZ(),
                        0.0, 0.0, 0.0);
            }
        }
    }

    @Nullable
    private Direction getLogFacing() {
        if (this.level().isClientSide()) return null;

        double midY = this.getY() + this.getBbHeight() / 2.0;
        Vec3 midPos = new Vec3(this.getX(), midY, this.getZ());
        BlockPos checkPos = BlockPos.containing(midPos.x, midPos.y, midPos.z);
        BlockState state = this.level().getBlockState(checkPos);

        if (state.is(PoTags.Blocks.EMPTY_LOGS) && state.hasProperty(DirectionalBlock.FACING)) {
            return state.getValue(DirectionalBlock.FACING);
        }
        return null;
    }

    private void applyLogPushing(@Nullable Direction logFacing) {
        if (logFacing == null) return;

        Vec3 motion = getMotion(logFacing);
        this.setDeltaMovement(motion);
    }

    private Vec3 getMotion(Direction axis) {
        Vec3 motion = this.getDeltaMovement();
        switch (axis) {
            case NORTH -> motion = motion.add(0.0, 0.0, -MOMENTUM_PER_TICK);
            case SOUTH -> motion = motion.add(0.0, 0.0, MOMENTUM_PER_TICK);
            case WEST -> motion = motion.add(-MOMENTUM_PER_TICK, 0.0, 0.0);
            case EAST -> motion = motion.add(MOMENTUM_PER_TICK, 0.0, 0.0);
            case UP -> motion = motion.add(0.0, MOMENTUM_PER_TICK, 0.0);
            case DOWN -> motion = motion.add(0.0, -MOMENTUM_PER_TICK, 0.0);
        }
        return motion;
    }

    private static int calculateExplosionRadius(double velocity) {
        int radius = MIN_EXPLOSION_RADIUS + (int) (velocity / 0.5);
        return Mth.clamp(radius, MIN_EXPLOSION_RADIUS, MAX_EXPLOSION_RADIUS);
    }

    public void setFuse(int fuse) {
        this.entityData.set(DATA_FUSE_ID, fuse);
    }

    public int getFuse() {
        return this.entityData.get(DATA_FUSE_ID);
    }

    @Nullable
    public LivingEntity getOwner() {
        return owner;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putShort("fuse", (short) this.getFuse());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setFuse(compound.getShort("fuse"));
    }

    @Override
    public void restoreFrom(Entity entity) {
        super.restoreFrom(entity);
        if (entity instanceof PoopTntEntity tnt) {
            this.owner = tnt.owner;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }
}