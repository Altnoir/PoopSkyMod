package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.impl.util.PoopTntUtil;
import com.altnoir.poopsky.init.PoEntityType;
import com.altnoir.poopsky.init.PoItems;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

public class ExplosiveChickenEntity extends Chicken {
    private static final EntityDataAccessor<Integer> DATA_FUSE =
            SynchedEntityData.defineId(ExplosiveChickenEntity.class, EntityDataSerializers.INT);
    private static final int FUSE_TIME = 40;
    private static final int EXPLOSION_RADIUS = 3;
    private static final double IGNITION_DISTANCE_SQR = 9.0;

    private boolean ignited;

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
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.35, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FUSE, 0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            if (this.getFuse() > 0) {
                this.level().addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE,
                        this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
            }
            return;
        }

        LivingEntity target = this.getTarget();
        if (!this.ignited && target != null && target.isAlive() && this.distanceToSqr(target) <= IGNITION_DISTANCE_SQR) {
            this.ignite();
        }

        if (this.ignited) {
            int fuse = this.getFuse() + 1;
            this.setFuse(fuse);
            if (fuse >= FUSE_TIME) {
                this.discard();
                PoopTntUtil.triggerExplosion(this, EXPLOSION_RADIUS);
            }
        }
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
        this.ignite();
        return true;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.FLINT_AND_STEEL)) {
            if (!this.level().isClientSide()) {
                this.ignite();
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
                this.gameEvent(GameEvent.PRIME_FUSE, player);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        return super.mobInteract(player, hand);
    }

    private void ignite() {
        if (!this.ignited) {
            this.ignited = true;
            this.playSound(PoSoundEvents.ENTITY_POP_PRIMED.get(), 1.0F, 1.0F);
        }
    }

    public int getFuse() {
        return this.entityData.get(DATA_FUSE);
    }

    private void setFuse(int fuse) {
        this.entityData.set(DATA_FUSE, fuse);
    }

    @Override
    public ItemEntity spawnAtLocation(ItemLike item) {
        return super.spawnAtLocation(item == Items.EGG ? PoItems.EXPLOSIVE_CHICKEN_EGG.get() : item);
    }

    @Nullable
    @Override
    public Chicken getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return PoEntityType.EXPLOSIVE_CHICKEN.get().create(level);
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
}
