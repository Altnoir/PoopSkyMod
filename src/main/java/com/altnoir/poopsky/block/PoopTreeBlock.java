package com.altnoir.poopsky.block;

import com.altnoir.poopsky.tag.PSBlockTags;
import com.altnoir.poopsky.worldgen.PSSaplingGenerators;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class PoopTreeBlock extends PlantBlock implements Fertilizable {
    public static final MapCodec<PoopTreeBlock> CODEC = createCodec(PoopTreeBlock::new);
    private static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0.0, 8.0, 0.0, 16.0, 16.0, 16.0), Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 8.0, 10.0)
    );

    @Override
    public MapCodec<PoopTreeBlock> getCodec() {
        return CODEC;
    }

    public PoopTreeBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isIn(PSBlockTags.TOILET_BLOCKS) || super.canPlantOnTop(floor, world, pos);
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return world.getFluidState(pos.up()).isEmpty();
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return world.random.nextFloat() < 0.45;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        PSSaplingGenerators.POOP_TREE.generate(world, world.getChunkManager().getChunkGenerator(), pos, state, random);
    }
}
