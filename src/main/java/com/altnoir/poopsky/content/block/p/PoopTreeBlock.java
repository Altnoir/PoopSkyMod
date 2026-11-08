package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.worldgen.PoConfigureFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Optional;

public class PoopTreeBlock extends SaplingBlock {
    public static final AbstractMegaTreeGrower treeGrower = new AbstractMegaTreeGrower() {
        @Override protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean flowers) { return PoConfigureFeatures.POOP_TREE; }
        @Override protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource random) { return PoConfigureFeatures.MEGA_POOP_TREE; }
    };
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(3.0, 8.0, 3.0, 13.0, 15.0, 13.0),
            Block.box(6.0, 0.0, 6.0, 10.0, 8.0, 10.0)
    );

    public PoopTreeBlock(Properties properties) {
        super(treeGrower, properties);
    }


    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        var offset = state.getOffset(level, pos);
        return SHAPE.move(offset.x, offset.y, offset.z);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean client) {
        return levelReader.getFluidState(blockPos.above()).isEmpty();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return random.nextFloat() < 0.45F;
    }
}