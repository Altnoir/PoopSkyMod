package com.altnoir.poopsky.entity.p;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.init.PParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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

    public static boolean checkPoolimeSpawnRules(
            EntityType<PoolimeEntity> poolime, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random
    ) {
        boolean flag = MobSpawnType.ignoresLightRequirements(spawnType);
        return level.getBlockState(pos.below()).is(PSBlocks.POOLIME_POOP_BLOCK.get()) && flag;
    }
}