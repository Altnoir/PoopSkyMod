package com.altnoir.poopsky.common.block.p;

import com.altnoir.poopsky.init.PBlocks;
import com.google.common.collect.ImmutableMap;
import com.google.common.base.Suppliers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public class PoopLogBlock extends RotatedPillarBlock {
    private static final Supplier<Map<Block, Block>> STRIPPABLES = Suppliers.memoize(() ->
            ImmutableMap.<Block, Block>builder()
                    .put(PBlocks.POOP_LOG.get(), PBlocks.STRIPPED_POOP_LOG.get())
                    .build()
    );

    public PoopLogBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        boolean allSolid = true;

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            BlockState neighborState = level.getBlockState(neighborPos);

            if (!neighborState.isSolidRender(level, neighborPos) || neighborState.is(BlockTags.LEAVES)) {
                allSolid = false;
                break;
            }
        }
        if (allSolid) {
            level.setBlockAndUpdate(pos, Blocks.COAL_BLOCK.defaultBlockState());
        }
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility == ItemAbilities.AXE_STRIP) {
            Block stripped = STRIPPABLES.get().get(state.getBlock());
            if (stripped != null) {
                return stripped.defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            }
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }
}