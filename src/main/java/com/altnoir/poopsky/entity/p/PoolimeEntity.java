package com.altnoir.poopsky.entity.p;

import com.altnoir.poopsky.particle.PSParticles;
import com.altnoir.poopsky.tag.PSBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.*;
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
        return PSParticles.POOP_PARTICLE.get();
    }

    public static boolean checkPooplimeSpawnRules(
            EntityType<PoolimeEntity> poolime, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random
    ) {
        boolean flag = MobSpawnType.ignoresLightRequirements(spawnType);
        return level.getBlockState(pos.below()).is(PSBlockTags.POOP_BLOCKS) && flag;
    }
}