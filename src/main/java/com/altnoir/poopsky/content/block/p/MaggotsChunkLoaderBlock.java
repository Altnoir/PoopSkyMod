package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.block.entity.MaggotsChunkLoaderBlockEntity;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MaggotsChunkLoaderBlock extends BaseEntityBlock {
    public static final MapCodec<MaggotsChunkLoaderBlock> CODEC = simpleCodec(MaggotsChunkLoaderBlock::new);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(3.0, 3.0, 3.0, 13.0, 14.0, 13.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0)
    );

    public MaggotsChunkLoaderBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(POWERED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MaggotsChunkLoaderBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide()
                ? null
                : createTickerHelper(type, PoBlockEntityType.MAGGOTS_CHUNK_LOADER.get(), (level1, pos, state1, blockEntity) -> MaggotsChunkLoaderBlockEntity.tick(level1, state1, blockEntity));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, Orientation orientation, boolean movedByPiston) {
        if (level instanceof ServerLevel serverLevel) {
            boolean powered = level.hasNeighborSignal(pos);
            boolean poweredChanged = state.getValue(POWERED) != powered;
            if (poweredChanged) {
                state = state.setValue(POWERED, powered);
                level.setBlock(pos, state, Block.UPDATE_ALL);
            }
            if (!powered && level.getBlockEntity(pos) instanceof MaggotsChunkLoaderBlockEntity blockEntity) {
                blockEntity.refreshLoading(serverLevel, state, 0);
            }
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof MaggotsChunkLoaderBlockEntity blockEntity) {
            blockEntity.releaseAllChunks(level);
        }
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }
}
