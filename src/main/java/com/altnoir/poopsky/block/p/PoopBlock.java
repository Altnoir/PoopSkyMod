package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.worldgen.PSConfigureFeatures;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class PoopBlock extends Block implements BonemealableBlock {
    protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);

    public PoopBlock(Properties properties) {
        super(properties);
    }


    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    private static boolean doesEntityDoPoopBlockSlideEffects(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof AbstractMinecart || entity instanceof PrimedTnt || entity instanceof Boat;
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
                level.broadcastEntityEvent(entity, (byte) 53);
            }
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos blockPos = pos.below();
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.isCollisionShapeFullBlock(level, blockPos) && level.getBlockState(blockPos.below()).is(Blocks.POINTED_DRIPSTONE)) {
            level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
            level.playSound(null, pos, SoundEvents.ROOTED_DIRT_PLACE, SoundSource.BLOCKS, 0.5F, 1.0F);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return level.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.registryAccess()
                .registry(Registries.CONFIGURED_FEATURE)
                .flatMap(holder -> holder.getHolder(PSConfigureFeatures.POOP_PATCH_BONEMEAL))
                .ifPresent(reference -> reference.value().place(level, level.getChunkSource().getGenerator(), random, pos.above()));
    }

    @Override
    public BonemealableBlock.@NotNull Type getType() {
        return BonemealableBlock.Type.NEIGHBOR_SPREADER;
    }
}
