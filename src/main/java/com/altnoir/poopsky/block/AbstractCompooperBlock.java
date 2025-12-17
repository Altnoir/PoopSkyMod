package com.altnoir.poopsky.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public abstract class AbstractCompooperBlock extends Block {
    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 3;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", MIN_LEVEL, MAX_LEVEL);

    public AbstractCompooperBlock(Properties properties) {
        super(properties);
    }
}
