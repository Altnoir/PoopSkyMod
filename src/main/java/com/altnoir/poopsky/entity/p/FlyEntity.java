package com.altnoir.poopsky.entity.p;

import com.altnoir.poopsky.init.PEntityType;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.tag.PSBlockTags;
import com.altnoir.poopsky.tag.PSItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import com.altnoir.poopsky.init.PSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class FlyEntity extends Bee {
    private static final int TOILET_SEARCH_RANGE = 8;
    private static final int TOILET_ATTRACTION_TICKS = 200;

    public int eggTime = this.random.nextInt(6000) + 6000;

    public FlyEntity(EntityType<? extends Bee> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.25, itemStack -> itemStack.is(PSItemTags.POOPS), false));
        this.goalSelector.addGoal(4, new FlyGoToToiletGoal());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Bee.createAttributes();
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide && this.isAlive() && !this.isBaby() && --this.eggTime <= 0) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.spawnAtLocation(PSItems.MAGGOTS_SEEDS.get());
            this.gameEvent(GameEvent.ENTITY_PLACE);
            this.eggTime = this.random.nextInt(6000) + 6000;
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(PSItemTags.POOPS);
    }

    @Override
    protected int getBaseExperienceReward() {
        return super.getBaseExperienceReward();
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
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return PSoundEvents.ENTITY_FLY_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return PSoundEvents.ENTITY_FLY_DEATH.get();
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        if (sound == SoundEvents.BEE_STING) {
            super.playSound(PSoundEvents.ENTITY_FLY_STING.get(), volume, pitch);
        } else if (sound == SoundEvents.BEE_POLLINATE) {
            super.playSound(PSoundEvents.ENTITY_FLY_POLLINATE.get(), volume, pitch);
        } else {
            super.playSound(sound, volume, pitch);
        }
    }

    @Override
    public @Nullable FlyEntity getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return PEntityType.FLY.get().create(level);
    }

    public static boolean checkFlySpawnRules(EntityType<FlyEntity> fly, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return MobSpawnType.ignoresLightRequirements(spawnType);
    }

    private class FlyGoToToiletGoal extends Goal {
        @Nullable
        private BlockPos targetPos;
        private int travellingTicks;
        private int cooldownTicks;

        FlyGoToToiletGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (this.cooldownTicks > 0) {
                this.cooldownTicks--;
                return false;
            }
            if (FlyEntity.this.isAngry() || FlyEntity.this.random.nextInt(20) != 0) {
                return false;
            }

            this.targetPos = this.findNearestToilet();
            return this.targetPos != null;
        }

        @Override
        public boolean canContinueToUse() {
            return this.targetPos != null
                    && this.travellingTicks < TOILET_ATTRACTION_TICKS
                    && FlyEntity.this.level().isLoaded(this.targetPos)
                    && FlyEntity.this.level().getBlockState(this.targetPos).is(PSBlockTags.TOILET_BLOCKS)
                    && !FlyEntity.this.isAngry();
        }

        @Override
        public void start() {
            this.travellingTicks = 0;
            this.moveToTarget();
        }

        @Override
        public void stop() {
            this.targetPos = null;
            this.cooldownTicks = Mth.nextInt(FlyEntity.this.random, 80, 160);
            FlyEntity.this.navigation.stop();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (this.targetPos == null) {
                return;
            }

            this.travellingTicks++;
            Vec3 hoverPos = Vec3.atCenterOf(this.targetPos).add(0.0, 1.1, 0.0);
            FlyEntity.this.getLookControl().setLookAt(hoverPos.x, hoverPos.y, hoverPos.z);
            if (FlyEntity.this.position().distanceToSqr(hoverPos) > 1.0) {
                this.moveToTarget();
            } else {
                FlyEntity.this.getMoveControl().setWantedPosition(hoverPos.x, hoverPos.y, hoverPos.z, 0.35);
            }
        }

        @Nullable
        private BlockPos findNearestToilet() {
            BlockPos origin = FlyEntity.this.blockPosition();
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            BlockPos nearestPos = null;
            double nearestDistance = Double.MAX_VALUE;

            for (int y = -3; y <= 3; y++) {
                for (int x = -TOILET_SEARCH_RANGE; x <= TOILET_SEARCH_RANGE; x++) {
                    for (int z = -TOILET_SEARCH_RANGE; z <= TOILET_SEARCH_RANGE; z++) {
                        mutablePos.setWithOffset(origin, x, y, z);
                        if (!FlyEntity.this.level().isLoaded(mutablePos)
                                || !FlyEntity.this.level().getBlockState(mutablePos).is(PSBlockTags.TOILET_BLOCKS)) {
                            continue;
                        }

                        double distance = mutablePos.distSqr(origin);
                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearestPos = mutablePos.immutable();
                        }
                    }
                }
            }

            return nearestPos;
        }

        private void moveToTarget() {
            if (this.targetPos != null) {
                FlyEntity.this.navigation.moveTo(
                        this.targetPos.getX() + 0.5,
                        this.targetPos.getY() + 1.1,
                        this.targetPos.getZ() + 0.5,
                        1.0
                );
            }
        }
    }
}
