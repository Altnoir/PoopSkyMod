package com.altnoir.poopsky.util;

import com.altnoir.poopsky.PTags;
import com.altnoir.poopsky.common.block.p.PoopTntBlock;
import com.altnoir.poopsky.init.PBlocks;
import com.altnoir.poopsky.init.PParticles;
import com.altnoir.poopsky.init.PRecipes;
import com.altnoir.poopsky.common.recipe.POPExplosionRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PoopTntUtil {
    public static void triggerExplosion(Entity entity, int radius) {
        if (!(entity.level() instanceof ServerLevel level)) return;

        BlockPos center = entity.blockPosition();
        RandomSource random = level.getRandom();
        Explosion explosion = level.explode(entity, entity.getX(), entity.getY(0.0625), entity.getZ(), radius, Level.ExplosionInteraction.NONE);

        double radiusSq = (double) radius * radius;
        double innerRadiusSq = radiusSq * 0.64;

        for (int x = -radius; x <= radius; x++) {
            double xSq = (double) x * x;
            if (xSq > radiusSq) continue;

            for (int y = -radius; y <= radius; y++) {
                double xySq = xSq + (double) y * y;
                if (xySq > radiusSq) continue;

                for (int z = -radius; z <= radius; z++) {
                    double distSq = xySq + (double) z * z;

                    if (distSq > radiusSq) continue;
                    if (shouldSkipEdgeBlock(random, distSq, innerRadiusSq, radiusSq)) continue;

                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (state.isAir() || state.getBlock() == Blocks.BEDROCK) continue;

                    handleExplosionBlock(level, pos, state, explosion, radius, distSq <= innerRadiusSq);
                }
            }
        }

        spawnPoopParticle(level, entity.getX(), entity.getY(), entity.getZ(), radius);
    }

    private static boolean shouldSkipEdgeBlock(RandomSource random, double distSq, double innerRadiusSq, double radiusSq) {
        if (distSq <= innerRadiusSq) return false;

        double edgeFactor = (distSq - innerRadiusSq) / (radiusSq - innerRadiusSq);
        return random.nextDouble() < edgeFactor * 0.6;
    }

    private static void handleExplosionBlock(ServerLevel level, BlockPos pos, BlockState state, Explosion explosion, int radius, boolean inner) {
        if (state.getBlock() instanceof PoopTntBlock poopTntBlock) {
            level.removeBlock(pos, false);
            poopTntBlock.wasExploded(level, pos, explosion);
            return;
        }

        POPExplosionRecipe.Output recipeOutput = findExplosionTransformOutput(level, state.getBlock(), radius);
        if (recipeOutput != null) {
            applyRecipeOutput(level, pos, recipeOutput);
            return;
        }

        if (inner) {
            handleInnerExplosionBlock(level, pos, state);
        } else {
            handleOuterExplosionBlock(level, pos, state);
        }
    }

    private static void handleInnerExplosionBlock(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.canBeReplaced() || state.is(PTags.Blocks.POOP_TNT_DESTROY)) {
            level.destroyBlock(pos, true, null);
        } else if (state.is(PTags.Blocks.POOP_TNT_REPLACEABLE)) {
            level.setBlockAndUpdate(pos, PBlocks.POOP_BLOCK.get().defaultBlockState());
        }
    }

    private static void handleOuterExplosionBlock(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.is(PTags.Blocks.POOP_TNT_REPLACEABLE)) {
            level.setBlockAndUpdate(pos, PBlocks.POOP_BLOCK.get().defaultBlockState());
        } else if (state.canBeReplaced()) {
            level.destroyBlock(pos, true, null);
        }
    }

    private static void applyRecipeOutput(Level level, BlockPos pos, POPExplosionRecipe.Output output) {
        if (output.isBlock() && output.block() != null) {
            level.setBlockAndUpdate(pos, output.block().defaultBlockState());
        } else if (output.item() != null) {
            level.destroyBlock(pos, false);
            Block.popResource(level, pos, new ItemStack(output.item()));
        }
    }

    private static POPExplosionRecipe.@Nullable Output findExplosionTransformOutput(Level level, Block block, int explosionRadius) {
        if (level.isClientSide) return null;
        ItemStack itemStack = new ItemStack(block.asItem());
        SingleRecipeInput input = new SingleRecipeInput(itemStack);

        for (RecipeHolder<POPExplosionRecipe> holder : level.getRecipeManager().getAllRecipesFor(PRecipes.POP_EXPLOSION.type().get())) {
            if (holder.value().matches(input, explosionRadius)) {
                return holder.value().output();
            }
        }
        return null;
    }

    private static void spawnPoopParticle(ServerLevel level, double x, double y, double z, int radius) {
        int particleCount = radius * 30;
        double offset = radius * 0.5;
        double speed = 0.4 + level.random.nextDouble() * 0.4;
        level.sendParticles(PParticles.POOP_PARTICLE.get(), x, y, z, particleCount, offset, offset, offset, speed);
        if (radius <= 2) {
            level.sendParticles(ParticleTypes.EXPLOSION, x, y, z, radius, offset, offset, offset, speed);
        } else {
            level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, radius, offset, offset, offset, speed);
        }
    }
}
