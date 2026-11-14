package com.altnoir.poopsky.content.block.entity;

import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public interface Overclockable {
    OverclockState getOverclockState();

    default int updateOverclock(Level level, BlockPos pos) {
        return getOverclockState().tick(level, pos);
    }

    final class OverclockState {
        private static final int SCAN_SIZE = 27;
        private static final int SCAN_BUDGET = 10;
        private static final int SCAN_COOLDOWN = 77;

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
            scan(level, pos, SCAN_BUDGET);
            return multiplier;
        }

        public void refresh(ServerLevel level, BlockPos pos) {
            index = 0;
            count = 0;
            scan(level, pos, SCAN_SIZE);
        }

        private void scan(Level level, BlockPos pos, int budget) {
            int end = Math.min(index + budget, SCAN_SIZE);
            while (index < end) {
                int i = index++;
                BlockPos target = pos.offset(i / 9 - 1, i / 3 % 3 - 1, i % 3 - 1);
                if (!target.equals(pos) && level.hasChunkAt(target)
                        && level.getBlockState(target).is(PoBlocks.OVERCLOCK_UNIT.get())) {
                    count++;
                }
            }
            if (index == SCAN_SIZE) {
                multiplier = 1 << count;
                index = 0;
                count = 0;
                cooldown = SCAN_COOLDOWN;
            }
        }
    }
}
