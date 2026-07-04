package com.altnoir.poopsky.common.block.p;

import com.altnoir.poopsky.common.entity.p.PoolimeEntity;
import com.altnoir.poopsky.init.PEntityType;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PoolimeMaggotsBlock extends Block {
    public static final MapCodec<PoolimeMaggotsBlock> CODEC = simpleCodec(PoolimeMaggotsBlock::new);
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);

    public PoolimeMaggotsBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.block();
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        PoolimeEntity poolime = PEntityType.POOLIME.get().create(level);

        if (poolime != null) {
            int count = level.getEntitiesOfClass(PoolimeEntity.class, poolime.getBoundingBox().inflate(64.0D)).size();

            if (count < 40 && random.nextInt(2) == 0) {
                BlockPos spawnPos = pos.above();
                if (level.getBlockState(spawnPos).canBeReplaced() && checkRange(level, pos)) {

                    int size = random.nextInt(4) + 1;
                    poolime.setSize(size, true);

                    poolime.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);

                    if (poolime.checkSpawnRules(level, MobSpawnType.NATURAL) && level.noCollision(poolime, poolime.getBoundingBox())) {
                        level.addFreshEntity(poolime);
                    }

                }
            }
        }
    }

    private boolean checkRange(Level level, BlockPos centerPos) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;

                BlockPos checkPos = centerPos.offset(x, 0, z);
                BlockState checkState = level.getBlockState(checkPos);

                if (!checkState.is(this) && !(checkState.getBlock() instanceof PoopBlock)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 0.2F;
    }
}
