package com.altnoir.poopsky.impl.util;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FireSafetyUtil {
    public static boolean isFlammable(BlockState state) {
        return !isWaterlogged(state);
    }

    public static int getFlammability(BlockState state, int flammability) {
        return isWaterlogged(state) ? 0 : flammability;
    }

    public static int getFireSpreadSpeed(BlockState state, int fireSpreadSpeed) {
        return isWaterlogged(state) ? 0 : fireSpreadSpeed;
    }

    private static boolean isWaterlogged(BlockState state) {
        return state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED);
    }
}