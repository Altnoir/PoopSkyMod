package com.altnoir.poopsky.block;

import com.altnoir.poopsky.worldgen.PSConfigureFeatures;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class DriedPoopBlock extends Block implements Fertilizable {


    public DriedPoopBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return world.getBlockState(pos.up()).isAir();
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        world.getRegistryManager()
                .getOptional(RegistryKeys.CONFIGURED_FEATURE)
                .flatMap(key -> key.getEntry(PSConfigureFeatures.DRIED_POOP_PATCH_BONEMEAL))
                .ifPresent(entry -> ((ConfiguredFeature<?, ?>)entry.value()).generate(world, world.getChunkManager().getChunkGenerator(), random, pos.up()));
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (hasHot(world, pos)) {
            world.setBlockState(pos, Blocks.SAND.getDefaultState());
            world.playSound(null, pos, SoundEvents.BLOCK_SAND_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
    }
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        super.onBlockAdded(state, world, pos, oldState, moved);
        if (hasHot((ServerWorld) world, pos)) {
            world.scheduleBlockTick(pos, this, 100);
        }
    }
    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        if (sourcePos.equals(pos.up()) && hasHot((ServerWorld) world, pos)) {
            world.scheduleBlockTick(pos, this, 100);
        }
    }
    private boolean hasHot (ServerWorld world, BlockPos pos) {
        BlockState aboveState = world.getBlockState(pos.up());
        return aboveState.isOf(Blocks.FIRE) || aboveState.isOf(Blocks.LAVA);
    }
}
