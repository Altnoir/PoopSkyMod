package com.altnoir.poopsky.util;

import com.altnoir.poopsky.block.PBlocks;
import com.altnoir.poopsky.block.p.PoopTntBlock;
import com.altnoir.poopsky.init.PParticles;
import com.altnoir.poopsky.tag.PBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class PoopTntUtil {
    public static final Map<Block, Block> EXPLOSION_RECIPES = Map.of(
            Blocks.COBBLESTONE, Blocks.GRAVEL,
            Blocks.GRAVEL, Blocks.SAND
    );

    public static void triggerExplosion(Entity entity, int radius) {
        Level level = entity.level();
        ServerLevel serverLevel = (ServerLevel) level;
        BlockPos center = entity.blockPosition();
        Explosion explosion = serverLevel.explode(entity, entity.getX(), entity.getY(), entity.getZ(), radius, Level.ExplosionInteraction.NONE);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (state.isAir() || state.getBlock() == Blocks.BEDROCK) continue;
                    if (state.getBlock() instanceof PoopTntBlock poopTntBlock) {
                        level.removeBlock(pos, false);
                        poopTntBlock.wasExploded(level, pos, explosion);
                        continue;
                    }

                    Block recipeOutput = EXPLOSION_RECIPES.get(state.getBlock());
                    if (recipeOutput != null) {
                        level.setBlockAndUpdate(pos, recipeOutput.defaultBlockState());
                        //Block.popResource(level, pos, recipeOutput.copy());
                    } else {
                        if (state.canBeReplaced() || state.is(PBlockTags.POOP_TNT_DESTROY)) {
                            level.destroyBlock(pos, true, null);
                        } else if (state.is(PBlockTags.POOP_TNT_REPLACEABLE)) {
                            level.setBlockAndUpdate(pos, PBlocks.POOP_BLOCK.get().defaultBlockState());
                        }
                    }
                }
            }
        }
/*
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
*/
        spawnPoopParticle((ServerLevel) level, entity.getX(), entity.getY(), entity.getZ(), radius);
    }

    private static void spawnPoopParticle(ServerLevel level, double x, double y, double z, int radius) {
        int particleCount = radius * 30;
        double offset = radius * 0.5;
        double speed = 0.4 + level.random.nextDouble() * 0.4;
        level.sendParticles(PParticles.POOP_PARTICLE.get(), x, y, z, particleCount, offset, offset, offset, speed);
        if (radius < 2) {
            level.sendParticles(ParticleTypes.EXPLOSION, x, y, z, radius, offset, offset, offset, speed);
        } else {
            level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, radius, offset, offset, offset, speed);
        }
    }
}
