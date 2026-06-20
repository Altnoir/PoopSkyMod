package com.altnoir.poopsky.block.abs;

import com.altnoir.poopsky.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.block.p.ToiletLavaBlock;
import com.altnoir.poopsky.entity.p.ToiletEntity;
import com.altnoir.poopsky.init.*;
import com.altnoir.poopsky.item.PItems;
import com.altnoir.poopsky.PTags;
import com.altnoir.poopsky.util.toiletUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public abstract class AbstractToiletBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<ToiletState> CONNECTION = EnumProperty.create("connection", ToiletState.class);

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public enum ToiletState implements StringRepresentable {
        DEFAULT("default"),
        FRONT("front"),
        BACK("back"),
        BOTH("both");

        private final String name;

        ToiletState(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getSerializedName() {
            return this.name;
        }
    }

    public AbstractToiletBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(CONNECTION, ToiletState.DEFAULT)
        );
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            Entity entity;
            List<ToiletEntity> entities = level.getEntities(PEntityType.TOILET.get(), new AABB(pos), toiletEntity -> true);

            if (entities.isEmpty()) {
                entity = PEntityType.TOILET.get().spawn((ServerLevel) level, pos, MobSpawnType.TRIGGERED);
            } else {
                entity = entities.getFirst();
            }
            if (entity instanceof ToiletEntity toiletEntity) {
                toiletEntity.setGoldenPoop(state.is(PTags.Blocks.GOLDEN_TOILET_BLOCKS));
            }
            player.startRiding(entity);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!oldState.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ToiletBlockEntity toilet) {
                toilet.clearLinkedBlock();
            }
            for (ToiletEntity toiletEntity : level.getEntities(PEntityType.TOILET.get(), new AABB(pos), e -> true)) {
                toiletEntity.kill();
            }
        }
        super.onRemove(oldState, level, pos, newState, isMoving);
    }

    @Override
    public void fallOn(Level level, BlockState blockState, BlockPos pos, Entity entity, float fallDistance) {
        if (!level.isClientSide) {
            ToiletBlockEntity be = null;

            if (fallDistance >= 1.0f && isEntityCentered(pos, entity)) {
                be = (ToiletBlockEntity) level.getBlockEntity(pos);
            }

            if (be != null && be.getLinkedPos() != null && be.getLinkedDim() != null && !be.getLinkedDim().isBlank()) {
                teleportEntity(level, entity, be, fallDistance);
            } else {
                super.fallOn(level, blockState, pos, entity, fallDistance);
            }

            if (entity instanceof FallingBlockEntity falling && isAnvil(falling.getBlockState())) {
                poopAnvil(level, blockState, entity);
            }
        }
    }

    protected boolean isAnvil(BlockState state) {
        return state.is(Blocks.ANVIL) || state.is(Blocks.CHIPPED_ANVIL) || state.is(Blocks.DAMAGED_ANVIL);
    }

    private void poopAnvil(Level level, BlockState blockState, Entity entity) {
        Item poopItem;
        if (blockState.is(PTags.Blocks.GOLDEN_TOILET_BLOCKS)) {
            poopItem = PItems.GOLDEN_POOP.get();
        } else {
            poopItem = PItems.POOP.get();
        }
        var poop = new ItemEntity(level, entity.getX(), entity.getY() + 0.1, entity.getZ(), new ItemStack(poopItem, 8));
        poop.setDefaultPickUpDelay();
        level.addFreshEntity(poop);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide && entity instanceof Player player) {
            if (player.isShiftKeyDown() && isEntityCentered(pos, player)) {
                var playerData = player.getPersistentData();
                long lastPoopTime = playerData.getLong("poopTime");
                toiletUtil.canPoop(level, player, player.hasEffect(PEffects.INTESTINAL_SPASM), false, 0.1F, 0.5F, lastPoopTime,
                        time -> playerData.putLong("poopTime", time));
            }
        }
    }

    protected boolean isEntityCentered(BlockPos blockPos, Entity entity) {
        var blockAABB = new AABB(blockPos).inflate(0.2);
        return blockAABB.contains(entity.position());
    }

    public void teleportEntity(Level level, Entity entity, ToiletBlockEntity blockEntity, float fallDistance) {
        var server = level.getServer();
        var linkedDim = blockEntity.getLinkedDim();
        var targetPos = blockEntity.getLinkedPos();

        var targetDimension = ResourceLocation.tryParse(linkedDim);
        if (targetDimension == null) return;

        var targetWorld = server.getLevel(ResourceKey.create(Registries.DIMENSION, targetDimension));
        if (targetWorld == null) return;
        targetWorld.getChunk(targetPos);

        if (entity.isVehicle() && !entity.getPassengers().isEmpty()) {
            entity.getControllingPassenger().teleportTo(targetWorld, targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5, Set.of(), entity.getYRot(), entity.getXRot());
            entity.teleportTo(targetWorld, targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5, Set.of(), entity.getYRot(), entity.getXRot());
        } else {
            entity.teleportTo(targetWorld, targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5, Set.of(), entity.getYRot(), entity.getXRot());
        }

        var pitch = targetWorld.random.nextFloat() + 0.1F;
        targetWorld.playSound(null, entity.getX(), entity.getY() + 0.1, entity.getZ(), SoundEvents.MUD_BREAK, SoundSource.PLAYERS, 1.0F, pitch);
        var bounce = Math.sqrt(2 * 0.08 * fallDistance) * 0.85;
        server.tell(new TickTask(server.getTickCount() + 1, () -> {
            entity.setDeltaMovement(entity.getDeltaMovement().x, bounce, entity.getDeltaMovement().z);
            entity.hurtMarked = true;
            entity.hasImpulse = true;
        }));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, CONNECTION);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        var level = ctx.getLevel();
        var pos = ctx.getClickedPos();
        Direction facing = ctx.getHorizontalDirection().getOpposite();

        var forwardPos = pos.relative(facing);
        var forwardConnected = isValidNeighbor(level, forwardPos, pos, facing);

        var backwardPos = pos.relative(facing.getOpposite());
        var backwardConnected = isValidNeighbor(level, backwardPos, pos, facing);

        ToiletState connection;
        if (forwardConnected && backwardConnected) {
            connection = ToiletState.BOTH;
        } else if (forwardConnected) {
            connection = ToiletState.FRONT;
        } else if (backwardConnected) {
            connection = ToiletState.BACK;
        } else {
            connection = ToiletState.DEFAULT;
        }

        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(CONNECTION, connection);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ToiletBlockEntity(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (hasHot(level, pos)) {
            level.explode(null, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 4.0F, Level.ExplosionInteraction.BLOCK);
            level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
            var poop = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, new ItemStack(PItems.POOP.get(), 88));
            level.addFreshEntity(poop);
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        super.onPlace(state, level, pos, oldState, moved);
        if (!level.isClientSide) {
            level.neighborChanged(pos, this, pos);
            if (hasHot((ServerLevel) level, pos)) {
                level.scheduleTick(pos, this, 1);
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved) {
        if (!level.isClientSide) {
            if (neighborPos.equals(pos.above()) && hasHot((ServerLevel) level, pos)) {
                level.scheduleTick(pos, this, 1);
            }

            var facing = state.getValue(FACING);
            var forwardPos = pos.relative(facing);
            var backwardPos = pos.relative(facing.getOpposite());

            var forwardConnected = isValidNeighbor(level, forwardPos, pos, facing);
            var backwardConnected = isValidNeighbor(level, backwardPos, pos, facing);
            ToiletState connection;
            if (forwardConnected && backwardConnected) {
                connection = ToiletState.BOTH;
            } else if (forwardConnected) {
                connection = ToiletState.FRONT;
            } else if (backwardConnected) {
                connection = ToiletState.BACK;
            } else {
                connection = ToiletState.DEFAULT;
            }
            if (connection != state.getValue(CONNECTION)) {
                level.setBlockAndUpdate(pos, state.setValue(CONNECTION, connection));
            }
        }
    }

    protected boolean hasHot(ServerLevel level, BlockPos pos) {
        var above = pos.above();
        if (!level.isInWorldBounds(above)) return false;
        return level.getBlockState(above).is(Blocks.FIRE);
    }

    protected boolean isValidNeighbor(LevelReader level, BlockPos neighborPos, BlockPos pos, Direction facing) {
        var neighbor = level.getBlockState(neighborPos);
        var state = level.getBlockState(pos);

        if (neighbor.getBlock() instanceof AbstractToiletBlock) {
            if (this instanceof ToiletLavaBlock) {
                if (neighbor.getBlock() instanceof ToiletLavaBlock) {
                    return neighbor.getValue(ToiletLavaBlock.LAVA) == state.getValue(ToiletLavaBlock.LAVA) && isFaceConnected(neighbor, facing);
                }
            } else {
                if (!(neighbor.getBlock() instanceof ToiletLavaBlock)) {
                    return isFaceConnected(neighbor, facing);
                }
            }
        }
        return false;
    }

    private boolean isFaceConnected(BlockState state, Direction facing) {
        return state.getValue(FACING) == facing || state.getValue(FACING) == facing.getOpposite();
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, PBlockEntityType.TOILET_BLOCK_ENTITY.get(), ToiletBlockEntity::tick);
    }
}