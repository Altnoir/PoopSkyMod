package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.tag.PSBlockTags;
import com.altnoir.poopsky.worldgen.PSConfigureFeatures;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

public class PoopTreeBlock extends SaplingBlock implements BonemealableBlock {
    public static final MapCodec<PoopTreeBlock> CODEC = simpleCodec(PoopTreeBlock::new);
    public static final TreeGrower treeGrower = new TreeGrower(PoopSky.MOD_ID + ":poop_tree",
            Optional.of(PSConfigureFeatures.MEGA_POOP_TREE),
            Optional.of(PSConfigureFeatures.POOP_TREE),
            Optional.empty());
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(3.0, 8.0, 3.0, 13.0, 15.0, 13.0),
            Block.box(6.0, 0.0, 6.0, 10.0, 8.0, 10.0)
    );

    public PoopTreeBlock(Properties properties) {
        super(treeGrower, properties);
    }

    @Override
    public MapCodec<? extends SaplingBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        var offset = state.getOffset(level, pos);
        return SHAPE.move(offset.x, offset.y, offset.z);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(PSBlockTags.TOILET_BLOCKS) || super.mayPlaceOn(state, level, pos);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return levelReader.getFluidState(blockPos.above()).isEmpty();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return random.nextFloat() < 0.45F;
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}