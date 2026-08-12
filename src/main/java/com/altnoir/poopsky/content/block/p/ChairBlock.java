package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.entity.p.ChairEntity;
import com.altnoir.poopsky.init.PoEntityType;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class ChairBlock extends Block {
    public static final MapCodec<ChairBlock> CODEC = simpleCodec(ChairBlock::new);
    private static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);

    public ChairBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            Entity entity;
            List<ChairEntity> entities = level.getEntities(PoEntityType.STOOL.get(), new AABB(pos), chairEntity -> true);
            if (entities.isEmpty()) {
                entity = PoEntityType.STOOL.get().spawn((ServerLevel) level, pos, EntitySpawnReason.TRIGGERED);
            } else {
                entity = entities.getFirst();
            }

            player.startRiding(entity);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        for (ChairEntity chairEntity : level.getEntities(PoEntityType.STOOL.get(), new AABB(pos), e -> true)) {
            chairEntity.kill(level);
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
