package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.impl.util.PoopTntUtil;
import com.altnoir.poopsky.init.PoEntityType;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class ExplosiveChickenEntity extends Chicken {
    private static final EntityDataAccessor<Integer> DATA_FUSE =
            SynchedEntityData.defineId(ExplosiveChickenEntity.class, EntityDataSerializers.INT);
    private static final int FUSE_TIME = 40;
    private static final int EXPLOSION_RADIUS = 3;
    private static final double IGNITION_DISTANCE_SQR = 9.0;
    private static final double DEFUSE_DISTANCE_SQR = 49.0;

    private boolean ignited;
    private boolean swelling;
    private int previousFuse;

    public ExplosiveChickenEntity(EntityType<? extends Chicken> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Chicken.createAttributes()
                .add(Attributes.ATTACK_DAMAGE, 1.0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.removeAllGoals(goal -> goal instanceof TemptGoal);
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0, this::isFood, false));
        this.goalSelector.addGoal(0, new SwellGoal());
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.35, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FUSE, 0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isBaby()) {
            if (!this.level().isClientSide() && (this.ignited || this.getFuse() > 0)) {
                this.ignited = false;
                this.setFuse(0);
            }
            return;
        }
        if (this.level().isClientSide()) {
            if (this.getFuse() > 0) {
                this.level().addParticle(ParticleTypes.SMOKE,
                        this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
            }
            return;
        }

        boolean advancing = this.ignited || this.swelling;
        int fuse = this.getFuse();
        if (advancing) {
            this.getNavigation().stop();
            this.setXxa(0.0F);
            this.setZza(0.0F);
            Vec3 velocity = this.getDeltaMovement();
            this.setDeltaMovement(0.0, velocity.y, 0.0);
            if (fuse == 0) {
                this.playSound(PoSoundEvents.ENTITY_POP_PRIMED.get(), 1.0F, 1.0F);
                this.gameEvent(GameEvent.PRIME_FUSE);
            }
        }
        fuse = Mth.clamp(fuse + (advancing ? 1 : -1), 0, FUSE_TIME);
        this.setFuse(fuse);
        if (fuse >= FUSE_TIME) {
            this.discard();
            PoopTntUtil.triggerExplosion(this, EXPLOSION_RADIUS);
        }
    }

    @Override
    public void tick() {
        this.previousFuse = this.getFuse();
        super.tick();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide() && source.getEntity() instanceof Player player) {
            this.setTarget(player);
        }
        return hurt;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        return !this.isBaby();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.isBaby() && stack.is(Items.FLINT_AND_STEEL)) {
            if (!this.level().isClientSide()) {
                this.ignite();
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        return super.mobInteract(player, hand);
    }

    private void ignite() {
        if (!this.isBaby() && !this.ignited) {
            this.ignited = true;
        }
    }

    public int getFuse() {
        return this.entityData.get(DATA_FUSE);
    }

    public float getSwelling(float partialTick) {
        return Mth.lerp(partialTick, (float) this.previousFuse, (float) this.getFuse()) / (FUSE_TIME - 2);
    }

    private void setFuse(int fuse) {
        this.entityData.set(DATA_FUSE, fuse);
    }

    @Override
    public @Nullable ItemEntity spawnAtLocation(ItemLike item) {
        return super.spawnAtLocation(item == Items.EGG ? Items.GUNPOWDER : item);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(PoTags.Items.POP_CHICKEN_LIKE);
    }

    @Nullable
    @Override
    public Chicken getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return PoEntityType.POP_CHICKEN.get().create(level);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("ignited", this.ignited);
        compound.putInt("fuse", this.getFuse());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.ignited = compound.getBoolean("ignited");
        this.setFuse(compound.getInt("fuse"));
    }

    private class SwellGoal extends Goal {
        private SwellGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = ExplosiveChickenEntity.this.getTarget();
            return !ExplosiveChickenEntity.this.isBaby()
                    && (ExplosiveChickenEntity.this.ignited
                    || target != null && target.isAlive()
                    && ExplosiveChickenEntity.this.distanceToSqr(target) < IGNITION_DISTANCE_SQR);
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = ExplosiveChickenEntity.this.getTarget();
            return !ExplosiveChickenEntity.this.isBaby()
                    && (ExplosiveChickenEntity.this.ignited
                    || target != null && target.isAlive()
                    && ExplosiveChickenEntity.this.distanceToSqr(target) <= DEFUSE_DISTANCE_SQR
                    && ExplosiveChickenEntity.this.getSensing().hasLineOfSight(target));
        }

        @Override
        public void start() {
            ExplosiveChickenEntity.this.getNavigation().stop();
            this.tick();
        }

        @Override
        public void stop() {
            ExplosiveChickenEntity.this.swelling = false;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            ExplosiveChickenEntity.this.swelling = this.canContinueToUse();
        }
    }
}
