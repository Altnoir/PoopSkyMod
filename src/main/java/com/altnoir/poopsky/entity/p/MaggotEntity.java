package com.altnoir.poopsky.entity.p;

import com.altnoir.poopsky.PTags;
import com.altnoir.poopsky.init.PSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class MaggotEntity extends PathfinderMob {
    private static final double POOP_BLOCK_TOP = 0.95;

    public MaggotEntity(EntityType<? extends MaggotEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 0.3));
        this.goalSelector.addGoal(2, new TemptGoal(this, 0.8, stack -> stack.is(PTags.Items.POOPS), false));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.25));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 4.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.setJumping(false);
        this.standOnPoopBlock();
    }

    @Override
    public void jumpFromGround() {
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return PSoundEvents.ENTITY_MAGGOT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return PSoundEvents.ENTITY_MAGGOT_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return 0.25F;
    }

    public static boolean checkMaggotSpawnRules(LevelAccessor level, MobSpawnType spawnType, BlockPos pos) {
        if (MobSpawnType.ignoresLightRequirements(spawnType)) {
            return isStableSurface(level, pos.below());
        }
        return level.getRawBrightness(pos, 0) < 12 && isStableSurface(level, pos.below());
    }

    private static boolean isStableSurface(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isFaceSturdy(level, pos, Direction.UP);
    }

    private void standOnPoopBlock() {
        BlockPos pos = this.blockPosition();
        BlockState state = this.level().getBlockState(pos);
        if (!state.is(PTags.Blocks.POOP_BLOCKS) && !state.is(PTags.Blocks.POOP_BLOCK)) {
            return;
        }
        double top = pos.getY() + POOP_BLOCK_TOP;
        if (this.getY() >= top - 1.0E-4) {
            return;
        }

        this.setPos(this.getX(), top, this.getZ());
        this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0));
        this.resetFallDistance();
    }
}
