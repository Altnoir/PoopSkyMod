package com.altnoir.poopsky.entity.p;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.init.PParticles;
import com.altnoir.poopsky.init.PSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.NotNull;

public class PoolimeEntity extends Slime {
    public PoolimeEntity(EntityType<PoolimeEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes();
    }

    @Override
    protected @NotNull ParticleOptions getParticleType() {
        return PParticles.POOP_PARTICLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return this.isTiny() ? PSoundEvents.ENTITY_POOLIME_HURT_SMALL.get() : PSoundEvents.ENTITY_POOLIME_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isTiny() ? PSoundEvents.ENTITY_POOLIME_DEATH_SMALL.get() : PSoundEvents.ENTITY_POOLIME_DEATH.get();
    }

    @Override
    protected SoundEvent getSquishSound() {
        return this.isTiny() ? PSoundEvents.ENTITY_POOLIME_SQUISH_SMALL.get() : PSoundEvents.ENTITY_POOLIME_SQUISH.get();
    }

    @Override
    protected SoundEvent getJumpSound() {
        return this.isTiny() ? PSoundEvents.ENTITY_POOLIME_JUMP_SMALL.get() : PSoundEvents.ENTITY_POOLIME_JUMP.get();
    }

    @Override
    protected void dealDamage(LivingEntity livingEntity) {
        if (this.isAlive() && this.isWithinMeleeAttackRange(livingEntity) && this.hasLineOfSight(livingEntity)) {
            DamageSource damageSource = this.damageSources().mobAttack(this);
            if (livingEntity.hurt(damageSource, this.getAttackDamage())) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 0));
                this.playSound(PSoundEvents.ENTITY_POOLIME_ATTACK.get(), 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    EnchantmentHelper.doPostAttackEffects(serverLevel, livingEntity, damageSource);
                }
            }
        }
    }

    public static boolean checkPoolimeSpawnRules(EntityType<PoolimeEntity> poolime, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        if (level.getDifficulty() == Difficulty.PEACEFUL || !Mob.checkMobSpawnRules(poolime, level, spawnType, pos, random)) {
            return false;
        }

        if (MobSpawnType.ignoresLightRequirements(spawnType)) {
            return true;
        }

        return level.getBlockState(pos.below()).is(PBlocks.POOLIME_POOP_BLOCK.get()) || isInPoopIsland(level, pos);
    }

    private static boolean isInPoopIsland(LevelAccessor level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }

        Structure structure = serverLevel.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .get(PoopSky.loc("poop_island"));
        return structure != null && serverLevel.structureManager().getStructureAt(pos, structure).isValid();
    }
}