package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.init.PoEffects;
import com.altnoir.poopsky.init.PoParticles;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BasiliskEntity extends Frog {
    private static final double BREATH_DISTANCE_SQR = 16.0;
    private static final int ATTACK_ANIMATION_DURATION = 15;
    private static final EntityDataAccessor<Integer> ATTACK_ANIMATION_TICKS =
            SynchedEntityData.defineId(BasiliskEntity.class, EntityDataSerializers.INT);
    private int breathCooldown;

    public BasiliskEntity(EntityType<? extends Frog> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Frog.createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.breathCooldown > 0) {
            this.breathCooldown--;
        }
        if (!this.level().isClientSide() && this.getAttackAnimationTicks() > 0) {
            this.entityData.set(ATTACK_ANIMATION_TICKS, this.getAttackAnimationTicks() - 1);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ATTACK_ANIMATION_TICKS, 0);
    }

    public int getAttackAnimationTicks() {
        return this.entityData.get(ATTACK_ANIMATION_TICKS);
    }

    public static int getAttackAnimationDuration() {
        return ATTACK_ANIMATION_DURATION;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        Player target = this.level().getNearestPlayer(this, 24.0);
        if (target == null || target.isCreative() || target.isSpectator()) {
            this.setTarget(null);
            return;
        }

        this.setTarget(target);
        this.getLookControl().setLookAt(target, 30.0F, 30.0F);
        if (this.canBreatheAt(target)) {
            this.getNavigation().stop();
            this.breatheCurse(target);
        } else {
            this.getNavigation().moveTo(target, 1.15);
        }
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return !effectInstance.is(PoEffects.DEATH_BLIGHT) && super.canBeAffected(effectInstance);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return PoSoundEvents.ENTITY_BASILISK_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return PoSoundEvents.ENTITY_BASILISK_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return PoSoundEvents.ENTITY_BASILISK_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        this.playSound(PoSoundEvents.ENTITY_BASILISK_STEP.get(), 0.15F, 1.0F);
    }

    private boolean canBreatheAt(LivingEntity target) {
        return this.breathCooldown == 0
                && this.distanceToSqr(target) <= BREATH_DISTANCE_SQR
                && this.hasLineOfSight(target);
    }

    private void breatheCurse(LivingEntity target) {
        this.entityData.set(ATTACK_ANIMATION_TICKS, ATTACK_ANIMATION_DURATION);
        Vec3 direction = target.position().subtract(this.position()).normalize();
        Vec3 cloudPos = this.position().add(direction.scale(1.5)).add(0.0, 0.35, 0.0);
        this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.45, 0.0));
        this.hasImpulse = true;
        this.playSound(SoundEvents.ENDER_DRAGON_SHOOT, 1.0F, 0.8F + this.random.nextFloat() * 0.2F);

        if (this.level() instanceof ServerLevel serverLevel) {
            AreaEffectCloud cloud = new AreaEffectCloud(serverLevel, cloudPos.x, cloudPos.y, cloudPos.z);
            cloud.setOwner(this);
            cloud.setParticle(PoParticles.DEATH_BLIGHT.get());
            cloud.setRadius(2.25F);
            cloud.setDuration(100);
            cloud.setWaitTime(0);
            cloud.setRadiusOnUse(0.0F);
            cloud.setRadiusPerTick(0.0F);
            cloud.addEffect(new MobEffectInstance(PoEffects.DEATH_BLIGHT, 400, 0));
            serverLevel.addFreshEntity(cloud);
        }
        this.breathCooldown = 80;
    }
}
