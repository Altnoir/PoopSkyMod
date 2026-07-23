package com.altnoir.poopsky.fabric.port.util;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ItemUtils {
    @Nullable
    public static BlockState getAxeStrippingState(BlockState originalState) {
        Block block = AxeItem.STRIPPABLES.get(originalState.getBlock());
        return block != null ? block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, originalState.getValue(RotatedPillarBlock.AXIS)) : null;
    }

    @Nullable
    public static BlockState getShovelPathingState(BlockState originalState) {
        return ShovelItem.FLATTENABLES.get(originalState.getBlock());
    }
}
