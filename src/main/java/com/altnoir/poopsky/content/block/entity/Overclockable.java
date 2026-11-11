package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface Overclockable {
    OverclockState getOverclockState();

    default int updateOverclock(Level level, BlockPos pos) {
        return getOverclockState().tick(level, pos);
    }

    final class OverclockState {
        private int cooldown;
        private int index;
        private int count;
        private int multiplier = 1;

        public int tick(Level level, BlockPos pos) {
            if (level.isClientSide) return 1;
            if (cooldown > 0) {
                cooldown--;
                return multiplier;
            }
            int end = Math.min(index + 10, 27);
            while (index < end) {
                int i = index++;
                BlockPos target = pos.offset(i / 9 - 1, i / 3 % 3 - 1, i % 3 - 1);
                if (!target.equals(pos) && level.hasChunkAt(target)
                        && level.getBlockState(target).is(PoBlocks.OVERCLOCK_UNIT.get())) {
                    count++;
                }
            }
            if (index == 27) {
                multiplier = 1 << count;
                index = 0;
                count = 0;
                cooldown = 77;
            }
            return multiplier;
        }
    }
}
