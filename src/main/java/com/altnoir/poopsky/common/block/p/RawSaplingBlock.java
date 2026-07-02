package com.altnoir.poopsky.common.block.p;

import com.altnoir.poopsky.common.block.abs.AbstractRawBlock;
import com.altnoir.poopsky.worldgen.PSConfigureFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class RawSaplingBlock extends AbstractRawBlock {
    public RawSaplingBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        serverLevel.registryAccess()
                .registry(Registries.CONFIGURED_FEATURE)
                .flatMap(holder -> holder.getHolder(PSConfigureFeatures.RAW_SAPLING_POOP_PATCH_BONEMEAL))
                .ifPresent(reference -> reference.value().place(serverLevel, serverLevel.getChunkSource().getGenerator(), randomSource, blockPos.above()));
    }
}
