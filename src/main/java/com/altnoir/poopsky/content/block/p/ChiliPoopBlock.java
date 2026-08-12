package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.worldgen.PoConfigureFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ChiliPoopBlock extends PoopBlock implements BonemealableBlock {
    public ChiliPoopBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.registryAccess()
                .lookupOrThrow(Registries.CONFIGURED_FEATURE)
                .get(PoConfigureFeatures.CHILI_POOP_PATCH_BONEMEAL)
                .ifPresent(reference -> reference.value().place(level, level.getChunkSource().getGenerator(), random, pos.above()));
    }
}
