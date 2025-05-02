package com.altnoir.poopsky.block;

import com.altnoir.poopsky.entity.ChairEntity;
import com.altnoir.poopsky.entity.PSEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChairBlock extends HorizontalFacingBlock {
    public static final MapCodec<ChairBlock> CODEC = createCodec(ChairBlock::new);
    private static final VoxelShape SHAPE = createCuboidShape(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
    protected ChairBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            Entity entity = null;
            List<ChairEntity> entities = world.getEntitiesByType(PSEntities.STOOL_ENTITY, new Box(pos), chair -> true);
            if (entities.isEmpty()) {
                entity = PSEntities.STOOL_ENTITY.spawn((ServerWorld) world, pos, SpawnReason.TRIGGERED);
            } else {
                entity = entities.get(0);
            }
            player.startRiding(entity);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            List<ChairEntity> entities = world.getEntitiesByType(PSEntities.STOOL_ENTITY, new Box(pos), ChairEntity::isAlive);
            for (ChairEntity entity : entities) {
                entity.discard();
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
