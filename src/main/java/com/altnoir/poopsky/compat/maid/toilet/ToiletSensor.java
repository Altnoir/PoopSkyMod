package com.altnoir.poopsky.compat.maid.toilet;

import com.altnoir.poopsky.compat.maid.MaidPlugin;
import com.altnoir.poopsky.entity.p.ToiletEntity;
import com.altnoir.poopsky.init.PEntityType;
import com.altnoir.poopsky.tag.PTags;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Set;

public class ToiletSensor extends Sensor<EntityMaid> {
    @Override
    protected void doTick(ServerLevel serverLevel, EntityMaid entityMaid) {
        var b = findToilet(serverLevel, entityMaid.getOnPos());
        if (b != null) {
            entityMaid.getBrain().setMemory(MaidPlugin.TOILET_MEMORY.get(), b);
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(MaidPlugin.TOILET_MEMORY.get());
    }

    private static boolean isToiletOccupied(ServerLevel level, BlockPos pos) {
        List<ToiletEntity> entities = level.getEntities(PEntityType.TOILET.get(), new AABB(pos), e -> true);
        for (ToiletEntity toilet : entities) {
            if (!toilet.getPassengers().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static BlockPos findToilet(ServerLevel level, BlockPos centerPos) {
        final int radius = 10;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int distance = 0; distance <= radius; distance++) {
            for (int dx = -distance; dx <= distance; dx++) {
                for (int dy = -distance; dy <= distance; dy++) {
                    for (int dz = -distance; dz <= distance; dz++) {
                        if (Math.abs(dx) != distance && Math.abs(dy) != distance && Math.abs(dz) != distance) {
                            continue;
                        }

                        mutablePos.set(centerPos.getX() + dx, centerPos.getY() + dy, centerPos.getZ() + dz);

                        if (level.getBlockState(mutablePos).is(PTags.Blocks.TOILET_BLOCKS) && !isToiletOccupied(level, mutablePos)) {
                            return mutablePos.immutable();
                        }
                    }
                }
            }
        }
        return null;
    }
}
