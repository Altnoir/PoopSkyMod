package com.altnoir.poopsky.entity.p;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.entity.PSEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Map;

public class PoopTntEntity extends Entity implements TraceableEntity {
    private static final EntityDataAccessor<Integer> DATA_FUSE_ID =
            SynchedEntityData.defineId(PoopTntEntity.class, EntityDataSerializers.INT);

    private static final int DEFAULT_FUSE_TIME = 80;
    private static final double MOMENTUM_PER_TICK = 1.25;
    private static final double MAX_VELOCITY_FOR_FULL_BLAST = 25.0;
    private static final int MIN_EXPLOSION_RADIUS = 1;
    private static final int MAX_EXPLOSION_RADIUS = 4;
    private static final double INSTANT_EXPLOSION_THRESHOLD = 0.1;

    private static final Map<Block, ItemStack> EXPLOSION_RECIPES = Map.of(
            Blocks.COBBLESTONE, new ItemStack(Items.GRAVEL),
            Blocks.GRAVEL, new ItemStack(Items.SAND)
    );

    @javax.annotation.Nullable
    private LivingEntity owner;

    public PoopTntEntity(EntityType<? extends PoopTntEntity> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    public PoopTntEntity(Level level, double x, double y, double z, @javax.annotation.Nullable LivingEntity owner) {
        this(PSEntityType.POOP_TNT.get(), level);
        this.setPos(x, y, z);
        this.setDeltaMovement(0, 0, 0);
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
        this.applyGravity();

        Vec3 movement = this.getDeltaMovement();
        double impactSpeed = movement.length();
        this.move(MoverType.SELF, movement);

        if (!this.level().isClientSide
                && impactSpeed > INSTANT_EXPLOSION_THRESHOLD
                && (this.horizontalCollision || this.verticalCollision)) {
            this.setDeltaMovement(movement);
            this.discard();
            this.explode();
            return;
        }

        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
        }
        this.applyLogPushing();
        int fuse = this.getFuse() - 1;
        this.setFuse(fuse);
        if (fuse <= 0) {
            this.discard();
            if (!this.level().isClientSide) {
                this.explode();
            }
        } else {
            this.updateInWaterStateAndDoFluidPushing();
            if (this.level().isClientSide) {
                this.level().addParticle(ParticleTypes.SMOKE,
                        this.getX(), this.getY() + 0.5, this.getZ(),
                        0.0, 0.0, 0.0);
            }
        }
    }

    private void applyLogPushing() {
        if (this.level().isClientSide) return;

        double midY = this.getY() + this.getBbHeight() / 2.0;
        Vec3 midPos = new Vec3(this.getX(), midY, this.getZ());

        for (int dy = -1; dy <= 1; dy++) {
            BlockPos checkPos = BlockPos.containing(midPos.x, midPos.y + dy, midPos.z);
            BlockState state = this.level().getBlockState(checkPos);
            if (state.is(PSBlocks.POOP_EMPTY_LOG) && state.hasProperty(BlockStateProperties.AXIS)) {
                var axis = state.getValue(BlockStateProperties.AXIS);
                Vec3 motion = this.getDeltaMovement();
                switch (axis) {
                    case X -> motion = motion.add(MOMENTUM_PER_TICK, 0.0, 0.0);
                    case Y -> motion = motion.add(0.0, MOMENTUM_PER_TICK, 0.0);
                    case Z -> motion = motion.add(0.0, 0.0, MOMENTUM_PER_TICK);
                }
                this.setDeltaMovement(motion);
                break;
            }
        }
    }

    protected void explode() {
        Level level = this.level();
        if (level.isClientSide) return;

        double velocity = this.getDeltaMovement().length();
        int radius = calculateExplosionRadius(velocity);

        level.explode(this, Explosion.getDefaultDamageSource(level, this), null,
                this.getX(), this.getY() + 0.0625, this.getZ(),
                0.0F, false, Level.ExplosionInteraction.NONE);

        BlockPos center = this.blockPosition();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (!state.isAir() && state.getBlock() != Blocks.BEDROCK) {
                        ItemStack recipeOutput = EXPLOSION_RECIPES.get(state.getBlock());
                        if (recipeOutput != null) {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                            Block.popResource(level, pos, recipeOutput.copy());
                        } else {
                            level.destroyBlock(pos, true);
                        }
                    }
                }
            }
        }

        AABB damageBox = new AABB(center).inflate(radius);
        for (Entity entity : level.getEntities(this, damageBox)) {
            if (entity.isAlive() && entity != this && !entity.isSpectator()) {
                BlockPos ePos = entity.blockPosition();
                if (Math.abs(ePos.getX() - center.getX()) <= radius
                        && Math.abs(ePos.getY() - center.getY()) <= radius
                        && Math.abs(ePos.getZ() - center.getZ()) <= radius) {
                    double dist = entity.distanceTo(this);
                    float damage = Math.max(1.0F, (float) ((radius * 2 + 1 - dist) * 2.0));
                    entity.hurt(level.damageSources().explosion(null, this.getOwner()), damage);
                }
            }
        }
    }

    private static int calculateExplosionRadius(double velocity) {
        double t = Math.min(velocity / MAX_VELOCITY_FOR_FULL_BLAST, 1.0);
        return Mth.clamp((int) Math.round(MIN_EXPLOSION_RADIUS + t * (MAX_EXPLOSION_RADIUS - MIN_EXPLOSION_RADIUS)),
                MIN_EXPLOSION_RADIUS, MAX_EXPLOSION_RADIUS);
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