package com.altnoir.poopsky.block.p;

import net.minecraft.world.level.block.SnowLayerBlock;

public class PoopPiece extends SnowLayerBlock {
    public PoopPiece(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, Integer.valueOf(1)));
    }
}
