package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.worldgen.PSConfigureFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class DriedPoopBlock extends Block implements BonemealableBlock {
    public DriedPoopBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return level.getBlockState(pos.above()).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.registryAccess()
                .registry(Registries.CONFIGURED_FEATURE)
                .flatMap(holder -> holder.getHolder(PSConfigureFeatures.DRIED_POOP_PATCH))
                .ifPresent(reference -> reference.value().place(level, level.getChunkSource().getGenerator(), random, pos.above()));
    }

    @Override
    public @NotNull Type getType() {
        return BonemealableBlock.Type.NEIGHBOR_SPREADER;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (hasHot(level, pos)) {
            level.playSound(null, pos, SoundEvents.SAND_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.setBlockAndUpdate(pos, Blocks.SAND.defaultBlockState());
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (hasHot(level, pos)) {
            level.scheduleTick(pos, this, 100);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (neighborPos.equals(pos.above()) && hasHot(level, pos)) {
            level.scheduleTick(pos, this, 100);
        }
    }

    private boolean hasHot(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (direction == Direction.DOWN) continue;

            BlockState neighborState = level.getBlockState(pos.relative(direction));
            if (isHotBlock(neighborState)) {
                return true;
            }
        }
        return false;
    }

    private boolean isHotBlock(BlockState state) {
        return state.is(Blocks.FIRE) || state.is(Blocks.LAVA);
    }
}
