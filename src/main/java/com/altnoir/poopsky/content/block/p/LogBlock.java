package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.init.PoBlocks;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public class LogBlock extends RotatedPillarBlock {
    protected static final Supplier<Map<Block, Block>> STRIPPABLES = Suppliers.memoize(() ->
            ImmutableMap.<Block, Block>builder()
                    .put(PoBlocks.POOP_LOG.get(), PoBlocks.STRIPPED_POOP_LOG.get())
                    .put(PoBlocks.GINKGO_LOG.get(), PoBlocks.STRIPPED_GINKGO_LOG.get())
                    .build()
    );

    public LogBlock(Properties properties) {
        super(properties);
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
