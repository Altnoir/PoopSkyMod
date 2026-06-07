package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.entity.PSEntityType;
import com.altnoir.poopsky.entity.p.PoolimeEntity;
import com.altnoir.poopsky.particle.PSParticles;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PooplimePoopBlock extends Block {
    public static final MapCodec<PooplimePoopBlock> CODEC = simpleCodec(PooplimePoopBlock::new);
    protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);

    public PooplimePoopBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.block();
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (this.isSlidingDown(pos, entity)) {
            this.maybeDoSlideAchievement(entity, pos);
            this.doSlideMovement(entity);
            this.maybeDoSlideEffects(level, entity);
        }

        super.entityInside(state, level, pos, entity);
    }

    private boolean isSlidingDown(BlockPos pos, Entity entity) {
        if (entity.onGround()) {
            return false;
        } else if (entity.getY() > (double) pos.getY() + 0.9375 - 1.0E-7) {
            return false;
        } else if (entity.getDeltaMovement().y >= -0.08) {
            return false;
        } else {
            double d0 = Math.abs((double) pos.getX() + 0.5 - entity.getX());
            double d1 = Math.abs((double) pos.getZ() + 0.5 - entity.getZ());
            double d2 = 0.4375 + (double) (entity.getBbWidth() / 2.0F);
            return d0 + 1.0E-7 > d2 || d1 + 1.0E-7 > d2;
        }
    }

    private void maybeDoSlideAchievement(Entity entity, BlockPos pos) {
        if (entity instanceof ServerPlayer && entity.level().getGameTime() % 20L == 0L) {
            CriteriaTriggers.HONEY_BLOCK_SLIDE.trigger((ServerPlayer) entity, entity.level().getBlockState(pos));
        }
    }

    private void doSlideMovement(Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        if (vec3.y < -0.13) {
            double d0 = -0.05 / vec3.y;
            entity.setDeltaMovement(new Vec3(vec3.x * d0, -0.05, vec3.z * d0));
        } else {
            entity.setDeltaMovement(new Vec3(vec3.x, -0.05, vec3.z));
        }

        entity.resetFallDistance();
    }

    private void maybeDoSlideEffects(Level level, Entity entity) {
        if (doesEntityDoPoopBlockSlideEffects(entity)) {
            if (level.random.nextInt(5) == 0) {
                entity.playSound(SoundEvents.HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
            }

            if (!level.isClientSide && level.random.nextInt(5) == 0) {
                ((ServerLevel) level).sendParticles(
                        PSParticles.POOP_PARTICLE.get(),
                        entity.getX(), entity.getY() + 0.1, entity.getZ(),
                        8,
                        0.0, -0.1, 0.0,
                        3.0
                );
            }
        }
    }

    private static boolean doesEntityDoPoopBlockSlideEffects(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof AbstractMinecart || entity instanceof PrimedTnt || entity instanceof Boat;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        PoolimeEntity poolime = PSEntityType.POOLIME.get().create(level);

        if (poolime != null) {
            int count = level.getEntitiesOfClass(PoolimeEntity.class, poolime.getBoundingBox().inflate(64.0D)).size();

            if (count < 40 && random.nextInt(2) == 0) {
                BlockPos spawnPos = pos.above();
                if (level.getBlockState(spawnPos).canBeReplaced() && checkRange(level, pos)) {

                    int size = random.nextInt(4) + 1;
                    poolime.setSize(size, true);

                    poolime.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);

                    if (poolime.checkSpawnRules(level, MobSpawnType.NATURAL) && level.noCollision(poolime, poolime.getBoundingBox())) {
                        level.addFreshEntity(poolime);
                    }

                }
            }
        }
    }

    private boolean checkRange(Level level, BlockPos centerPos) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;

                BlockPos checkPos = centerPos.offset(x, 0, z);
                BlockState checkState = level.getBlockState(checkPos);

                if (!checkState.is(this) && !(checkState.getBlock() instanceof PoopBlock)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 0.2F;
    }
}
