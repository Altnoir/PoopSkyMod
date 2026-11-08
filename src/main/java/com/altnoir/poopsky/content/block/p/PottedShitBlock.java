package com.altnoir.poopsky.content.block.p;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

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

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return new ItemStack(this.flower);
    }
}
