package com.altnoir.poopsky.content.block.p;

import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class PottedShitBlock extends ShitBlock {
    private final Supplier<? extends Block> base;
    private final Block flower;

    public PottedShitBlock(Supplier<? extends Block> base, Block flower, Properties properties) {
        super(properties);
        this.base = base;
        this.flower = flower;
    }

    public Block getBase() {
        return base.get();
    }

    public Block getFlower() {
        return flower;
    }
}
