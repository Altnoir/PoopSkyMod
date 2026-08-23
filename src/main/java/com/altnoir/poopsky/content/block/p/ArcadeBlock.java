package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.block.entity.ArcadeBlockEntity;
import com.altnoir.poopsky.content.item.p.GameDiskItem;
import com.altnoir.poopsky.game.util.GameUtils;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoSoundEvents;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public class ArcadeBlock extends Block implements EntityBlock {
    public static final MapCodec<ArcadeBlock> CODEC = simpleCodec(ArcadeBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty GAME = BooleanProperty.create("game");
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    private static final VoxelShape TOP_SCREEN_CUTOUT = Block.box(1.0, 0.0, 7.0, 15.0, 10.0, 9.0);
    private static final VoxelShape BOTTOM_NORTH_SHAPE = Shapes.or(
            Block.box(0.0, 0.0, 1.0, 16.0, 14.0, 16.0),
            Block.box(0.0, 14.0, 8.0, 16.0, 16.0, 16.0)
    );
    private static final VoxelShape TOP_NORTH_SHAPE = Shapes.join(
            Shapes.or(
                    Block.box(0.0, 0.0, 8.0, 16.0, 12.0, 16.0),
                    Block.box(0.0, 12.0, 5.0, 16.0, 16.0, 16.0)
            ),
            TOP_SCREEN_CUTOUT,
            BooleanOp.ONLY_FIRST
    );
    private static final Map<Direction, VoxelShape> BOTTOM_SHAPES = computeShapes(BOTTOM_NORTH_SHAPE);
    private static final Map<Direction, VoxelShape> TOP_SHAPES = computeShapes(TOP_NORTH_SHAPE);

    public ArcadeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(GAME, false)
                .setValue(TRIGGERED, false));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new ArcadeBlockEntity(pos, state) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide || type != PoBlockEntityType.ARCADE_BLOCK_ENTITY.get()) {
            return null;
        }
        return (level1, pos, blockState, blockEntity) -> ArcadeBlockEntity.serverTick(level1, pos, blockState, (ArcadeBlockEntity) blockEntity);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, GAME, TRIGGERED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(context)) {
            return this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection().getOpposite())
                    .setValue(HALF, DoubleBlockHalf.LOWER)
                    .setValue(GAME, false);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (level.isClientSide) {
            return;
        }

        ArcadeBlockEntity arcade = getArcadeEntity(level, pos, state);
        if (arcade == null) {
            return;
        }

        BlockPos lowerPos = arcade.getBlockPos();
        BlockState lowerState = level.getBlockState(lowerPos);
        boolean powered = level.hasNeighborSignal(lowerPos) || level.hasNeighborSignal(pos);

        if (powered && !lowerState.getValue(TRIGGERED)) {
            level.scheduleTick(lowerPos, this, 4);
            level.setBlock(lowerPos, lowerState.setValue(TRIGGERED, true), 2);
        } else if (!powered && lowerState.getValue(TRIGGERED)) {
            level.setBlock(lowerPos, lowerState.setValue(TRIGGERED, false), 2);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof ArcadeBlockEntity arcade) {
            arcade.spillAllRewards();
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (facing.getAxis() != Direction.Axis.Y || half == DoubleBlockHalf.LOWER != (facing == Direction.UP)) {
            return half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)
                    ? Blocks.AIR.defaultBlockState()
                    : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        }
        if (facingState.is(this) && facingState.getValue(HALF) != half) {
            return state;
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && (player.isCreative() || !player.hasCorrectToolForDrops(state, level, pos))) {
            if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
                BlockPos lowerPos = pos.below();
                BlockState lowerState = level.getBlockState(lowerPos);
                if (lowerState.is(this) && lowerState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    level.setBlock(lowerPos, Blocks.AIR.defaultBlockState(), 35);
                    level.levelEvent(player, 2001, lowerPos, Block.getId(lowerState));
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        ArcadeBlockEntity arcade = getArcadeEntity(level, pos, state);
        if (arcade == null) {
            return InteractionResult.PASS;
        }

        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            if (arcade.getRewardCount() > 0) {
                if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                    arcade.giveReward(serverPlayer);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            if (!arcade.hasCartridge()) {
                showNoCartridge(level, player);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            if (arcade.hasCartridge()) {
                ejectCartridge(level, player, arcade);
            } else {
                showNoCartridge(level, player);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (handleControlExit(level, player, arcade)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (arcade.isController(player)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!arcade.hasCartridge()) {
            showNoCartridge(level, player);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        startArcadeControl(level, player, arcade);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ArcadeBlockEntity arcade = getArcadeEntity(level, pos, state);
        if (arcade == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            if (arcade.getRewardCount() > 0) {
                if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                    arcade.giveReward(serverPlayer);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            if (!arcade.hasCartridge()) {
                showNoCartridge(level, player);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (player.isShiftKeyDown()) {
            if (arcade.hasCartridge()) {
                ejectCartridge(level, player, arcade);
            } else {
                showNoCartridge(level, player);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (handleControlExit(level, player, arcade)) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (arcade.isController(player)) {
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (stack.getItem() instanceof GameDiskItem && !arcade.hasCartridge()) {
            if (!level.isClientSide && arcade.insertCartridge(stack)) {
                stack.shrink(1);
                level.playSound(null, arcade.getBlockPos(), PoSoundEvents.CONFIRM.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!arcade.hasCartridge()) {
            showNoCartridge(level, player);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        startArcadeControl(level, player, arcade);
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    private static boolean handleControlExit(Level level, Player player, ArcadeBlockEntity arcade) {
        if (level.isClientSide) {
            return GameUtils.exitArcadeControl(arcade.getBlockPos());
        }
        if (arcade.isControlling(player)) {
            if (player instanceof ServerPlayer serverPlayer) {
                arcade.stopControl(serverPlayer);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && !state.is(newState.getBlock())
                && state.getValue(HALF) == DoubleBlockHalf.LOWER
                && level.getBlockEntity(pos) instanceof ArcadeBlockEntity arcade
                && arcade.hasCartridge()) {
            ItemStack cartridge = arcade.ejectCartridge();
            if (!cartridge.isEmpty()) {
                Block.popResource(level, pos, cartridge);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return true;
        }
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return belowState.is(this);
    }

    @Nullable
    private static ArcadeBlockEntity getArcadeEntity(Level level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof ArcadeBlock)) {
            return null;
        }

        BlockPos lowerPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
        if (level.getBlockEntity(lowerPos) instanceof ArcadeBlockEntity arcade) {
            return arcade;
        }

        return null;
    }

    private static void ejectCartridge(Level level, Player player, ArcadeBlockEntity arcade) {
        if (level.isClientSide) {
            return;
        }

        ItemStack cartridge = arcade.ejectCartridge();
        if (cartridge.isEmpty()) {
            return;
        }

        if (!player.getInventory().add(cartridge) && !cartridge.isEmpty()) {
            Block.popResource(level, arcade.getBlockPos(), cartridge);
        }
        level.playSound(null, arcade.getBlockPos(), PoSoundEvents.SWITCH.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private static void showNoCartridge(Level level, Player player) {
        if (!level.isClientSide) {
            player.displayClientMessage(Component.translatable("message.gamedisks.light_arcade.no_cartridge"), true);
        }
    }

    private static void startArcadeControl(Level level, Player player, ArcadeBlockEntity arcade) {
        if (!arcade.hasCartridge()) {
            return;
        }
        if (level.isClientSide) {
            GameUtils.enterArcadeControl(arcade.getBlockPos());
        } else if (player instanceof ServerPlayer serverPlayer) {
            arcade.startControl(serverPlayer);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getArcadeShape(state);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(GAME) ? 7 : 0;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getArcadeShape(state);
    }

    private static VoxelShape getArcadeShape(BlockState state) {
        Map<Direction, VoxelShape> shapes = state.getValue(HALF) == DoubleBlockHalf.UPPER ? TOP_SHAPES : BOTTOM_SHAPES;
        return shapes.get(state.getValue(FACING));
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return mirror == Mirror.NONE ? state : rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    private static Map<Direction, VoxelShape> computeShapes(VoxelShape northShape) {
        Map<Direction, VoxelShape> map = new EnumMap<>(Direction.class);
        for (Direction direction : BlockStateProperties.HORIZONTAL_FACING.getPossibleValues()) {
            map.put(direction, rotateShape(northShape, direction));
        }
        return map;
    }

    private static VoxelShape rotateShape(VoxelShape shape, Direction direction) {
        if (direction == Direction.NORTH) {
            return shape;
        }
        return shape.toAabbs().stream()
                .map(aabb -> rotateAABB(aabb, direction))
                .reduce(Shapes.empty(), Shapes::or);
    }

    private static VoxelShape rotateAABB(AABB aabb, Direction direction) {
        double minX = aabb.minX * 16;
        double minY = aabb.minY * 16;
        double minZ = aabb.minZ * 16;
        double maxX = aabb.maxX * 16;
        double maxY = aabb.maxY * 16;
        double maxZ = aabb.maxZ * 16;

        return switch (direction) {
            case EAST -> Block.box(16.0 - maxZ, minY, minX, 16.0 - minZ, maxY, maxX);
            case SOUTH -> Block.box(16.0 - maxX, minY, 16.0 - maxZ, 16.0 - minX, maxY, 16.0 - minZ);
            case WEST -> Block.box(minZ, minY, 16.0 - maxX, maxZ, maxY, 16.0 - minX);
            default -> Block.box(minX, minY, minZ, maxX, maxY, maxZ);
        };
    }
}
