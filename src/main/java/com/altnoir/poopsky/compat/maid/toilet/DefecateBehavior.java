package com.altnoir.poopsky.compat.maid.toilet;

import com.altnoir.poopsky.compat.maid.MaidPlugin;
import com.altnoir.poopsky.entity.p.ToiletEntity;
import com.altnoir.poopsky.init.PEntityType;
import com.altnoir.poopsky.tag.PBlockTags;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class DefecateBehavior extends Behavior<EntityMaid> {
    public DefecateBehavior() {
        super(Map.of());
    }

    @Override
    protected boolean canStillUse(@NotNull ServerLevel level, EntityMaid entity, long gameTime) {
        return entity.getTask() instanceof DefecateTask;
    }

    @Override
    protected void start(@NotNull ServerLevel level, @NotNull EntityMaid entity, long gameTime) {
        super.start(level, entity, gameTime);
        entity.getSchedulePos().setIdlePos(entity.getOnPos());
    }

    @Override
    protected void tick(@NotNull ServerLevel level, @NotNull EntityMaid maid, long gameTime) {
        super.tick(level, maid, gameTime);
        if (!(maid.getTask() instanceof DefecateTask)) {
            stop(level, maid, gameTime);
            return;
        }

        if (maid.getVehicle() instanceof ToiletEntity toiletEntity) {
            if (!level.getBlockState(toiletEntity.blockPosition()).is(PBlockTags.TOILET_BLOCKS)) {
                maid.stopRiding();
                maid.getBrain().eraseMemory(MaidPlugin.TOILET_MEMORY.get());
            }
            return;
        }

        var p = maid.getBrain().getMemory(MaidPlugin.TOILET_MEMORY.get());
        if (p.isPresent()) {
            var pos = p.get();
            if (!pos.closerThan(maid.blockPosition(), 30)) {
                return;
            }
            if (!level.getBlockState(pos).is(PBlockTags.TOILET_BLOCKS)) {
                maid.getBrain().eraseMemory(MaidPlugin.TOILET_MEMORY.get());
                return;
            }
            maid.restrictTo(maid.getOnPos(), 50);
            maid.getSchedulePos().setWorkPos(maid.getOnPos());
            maid.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 0.5f, 1));

            if (pos.closerThan(maid.blockPosition(), 2)) {
                rideToilet(level, maid, pos);
            }
        }
    }

    private void rideToilet(ServerLevel level, EntityMaid maid, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (!state.is(PBlockTags.TOILET_BLOCKS)) {
            return;
        }

        List<ToiletEntity> entities = level.getEntities(PEntityType.TOILET.get(), new AABB(pos), e -> true);
        ToiletEntity toiletEntity;
        if (entities.isEmpty()) {
            Entity entity = PEntityType.TOILET.get().spawn(level, pos, MobSpawnType.TRIGGERED);
            if (!(entity instanceof ToiletEntity te)) {
                return;
            }
            toiletEntity = te;
        } else {
            toiletEntity = entities.getFirst();
        }

        if (toiletEntity.getPassengers().isEmpty()) {
            toiletEntity.setGoldenPoop(state.is(PBlockTags.GOLDEN_TOILET_BLOCKS));
            maid.startRiding(toiletEntity);
        }
    }

    @Override
    protected boolean timedOut(long gameTime) {
        return false;
    }

    @Override
    protected void stop(@NotNull ServerLevel level, @NotNull EntityMaid maid, long gameTime) {
        if (maid.getVehicle() instanceof ToiletEntity) {
            maid.stopRiding();
        }
        maid.getBrain().eraseMemory(MaidPlugin.TOILET_MEMORY.get());
    }
}