package com.altnoir.poopsky.util;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.block.p.PoopTntBlock;
import com.altnoir.poopsky.init.PParticles;
import com.altnoir.poopsky.tag.PSBlockTags;
import net.minecraft.core.BlockPos;
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

    //TODO 暂时把爆炸改为替换方块，后续解决破坏方块不掉落物品再改回去
    public static void triggerExplosion(Entity entity, int radius) {
        Level level = entity.level();
        BlockPos center = entity.blockPosition();

        Explosion explosion = level.explode(entity, Explosion.getDefaultDamageSource(level, entity), null,
                entity.getX(), entity.getY() + 0.0625, entity.getZ(), 1.0F, false, Level.ExplosionInteraction.NONE);

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
                        if (state.canBeReplaced() || state.is(PSBlockTags.POOP_TNT_DESTROY)) {
                            level.destroyBlock(pos, true, null);
                        } else if (state.is(PSBlockTags.POOP_TNT_REPLACEABLE)) {
                            level.setBlockAndUpdate(pos, PSBlocks.POOP_BLOCK.get().defaultBlockState());
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
    }
}
