package com.altnoir.poopsky.content.block.abs;

import com.altnoir.poopsky.content.block.ToiletType;
import com.altnoir.poopsky.content.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.content.block.p.BaseToiletLavaBlock;
import com.altnoir.poopsky.content.entity.p.ToiletEntity;
import com.altnoir.poopsky.init.PBlockEntityType;
import com.altnoir.poopsky.init.PEffects;
import com.altnoir.poopsky.init.PEntityType;
import com.altnoir.poopsky.init.PItems;
import com.altnoir.poopsky.init.PToiletTypes;
import com.altnoir.poopsky.content.item.p.ToiletBlockItem;
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
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class AbstractToiletBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<ToiletState> CONNECTION = EnumProperty.create("connection", ToiletState.class);

    private static final VoxelShape NORTH_SOUTH_BASE_SHAPE = Shapes.or(
            Block.box(0.0, 0.0, 0.0, 5.0, 16.0, 16.0),
            Block.box(11.0, 0.0, 0.0, 16.0, 16.0, 16.0),
            Block.box(5.0, 0.0, 0.0, 11.0, 12.0, 16.0)
    );
    private static final VoxelShape NORTH_CAP_SHAPE = Block.box(5.0, 12.0, 0.0, 11.0, 16.0, 1.0);
    private static final VoxelShape SOUTH_CAP_SHAPE = Block.box(5.0, 12.0, 15.0, 11.0, 16.0, 16.0);
    private static final VoxelShape EAST_WEST_BASE_SHAPE = Shapes.or(
            Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 5.0),
            Block.box(0.0, 0.0, 11.0, 16.0, 16.0, 16.0),
            Block.box(0.0, 0.0, 5.0, 16.0, 12.0, 11.0)
    );
    private static final VoxelShape WEST_CAP_SHAPE = Block.box(0.0, 12.0, 5.0, 1.0, 16.0, 11.0);
    private static final VoxelShape EAST_CAP_SHAPE = Block.box(15.0, 12.0, 5.0, 16.0, 16.0, 11.0);

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

        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    public AbstractToiletBlock(Properties properties) {
        super(properties);
        PToiletTypes.init();
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(CONNECTION, ToiletState.DEFAULT)
        );
    }

    public abstract ToiletType getDefaultToiletType();

    @Nullable
    protected ToiletType getToiletType(BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ToiletBlockEntity be) {
            return be.getToiletType();
        }
        return null;
    }

    public ToiletType getToiletTypeOrDefault(BlockGetter level, BlockPos pos) {
        ToiletType type = getToiletType(level, pos);
        return type != null ? type : getDefaultToiletType();
    }

    @Nullable
    protected Block getVariantSourceBlock(BlockGetter level, BlockPos pos) {
        ToiletType type = getToiletType(level, pos);
        return type != null ? type.sourceBlock() : null;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return ToiletBlockItem.withType(this, getToiletTypeOrDefault(level, pos));
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        Block sourceBlock = getVariantSourceBlock(level, pos);
        return sourceBlock != null ? sourceBlock.defaultBlockState().getSoundType(level, pos, entity) : super.getSoundType(state, level, pos, entity);
    }

    public BlockState getParticleState(BlockState state, BlockGetter level, BlockPos pos) {
        ToiletType type = getToiletType(level, pos);
        Block sourceBlock = type != null ? type.sourceBlock() : null;
        return sourceBlock != null ? sourceBlock.defaultBlockState() : state;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!player.getMainHandItem().isEmpty()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            ToiletEntity entity = level.getEntities(PEntityType.TOILET.get(), new AABB(pos), e -> true)
                    .stream()
                    .findFirst()
                    .orElseGet(() -> PEntityType.TOILET.get().spawn((ServerLevel) level, pos, MobSpawnType.TRIGGERED));

            if (entity != null) {
                entity.setGoldenPoop(toiletUtil.isGoldenToilet(level, pos));
                player.startRiding(entity);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
    ) {
        if (!stack.is(Items.FLINT_AND_STEEL) && !stack.is(Items.FIRE_CHARGE)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        Item item = stack.getItem();
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            explodeToilet(serverLevel, pos);
        }

        if (stack.is(Items.FLINT_AND_STEEL)) {
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        } else {
            stack.consume(1, player);
        }
        player.awardStat(Stats.ITEM_USED.get(item));
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    protected ItemInteractionResult handleVariantReplacement(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, ToiletType.Category acceptedCategory) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            ToiletType newType = ToiletType.bySourceBlock(blockItem.getBlock());
            if (newType != null && newType.category() == acceptedCategory) {
                if (level.isClientSide) {
                    return ItemInteractionResult.SUCCESS;
                }
                if (level.getBlockEntity(pos) instanceof ToiletBlockEntity be) {
                    ToiletType currentType = be.getToiletType();
                    if (currentType != newType) {
                        be.setToiletType(newType);

                        SoundType sound = blockItem.getBlock().defaultBlockState().getSoundType(level, pos, player);
                        level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);

                        stack.consume(1, player);
                        if (!player.getAbilities().instabuild) {
                            Block oldBlock = currentType.sourceBlock();
                            if (oldBlock != null) {
                                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.72, pos.getZ() + 0.5, new ItemStack(oldBlock));
                                itemEntity.setDefaultPickUpDelay();
                                level.addFreshEntity(itemEntity);
                            }
                        }
                    }
                    return ItemInteractionResult.sidedSuccess(false);
                }
            }
        }
        return null;
    }

    @Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!oldState.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof ToiletBlockEntity toilet) {
                toilet.clearLinkedBlock();
            }
            level.getEntities(PEntityType.TOILET.get(), new AABB(pos), e -> true).forEach(Entity::kill);
            if (!level.isClientSide) {
                updateAdjacentConnections(level, pos, oldState);
            }
        }
        super.onRemove(oldState, level, pos, newState, isMoving);
    }

    @Override
    public void fallOn(Level level, BlockState blockState, BlockPos pos, Entity entity, float fallDistance) {
        if (!level.isClientSide) {
            if (fallDistance >= 1.0f && isEntityCentered(pos, entity)) {
                if (level.getBlockEntity(pos) instanceof ToiletBlockEntity be &&
                        be.getLinkedPos() != null && be.getLinkedDim() != null && !be.getLinkedDim().isBlank()) {
                    teleportEntity(level, entity, be, fallDistance);
                    return; // 如果传送了，就不再触发默认掉落逻辑
                }
            }

            super.fallOn(level, blockState, pos, entity, fallDistance);

            if (entity instanceof FallingBlockEntity falling && isAnvil(falling.getBlockState())) {
                poopAnvil(level, pos, entity);
            }
        } else {
            super.fallOn(level, blockState, pos, entity, fallDistance);
        }
    }

    protected boolean isAnvil(BlockState state) {
        return state.is(Blocks.ANVIL) || state.is(Blocks.CHIPPED_ANVIL) || state.is(Blocks.DAMAGED_ANVIL);
    }

    private void poopAnvil(Level level, BlockPos pos, Entity entity) {
        Item poopItem = toiletUtil.isGoldenToilet(level, pos) ? PItems.GOLDEN_POOP.get() : PItems.POOP.get();
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
        return new AABB(blockPos).inflate(0.2).contains(entity.position());
    }

    public void teleportEntity(Level level, Entity entity, ToiletBlockEntity blockEntity, float fallDistance) {
        var server = level.getServer();
        if (server == null) return;

        var targetDimension = ResourceLocation.tryParse(blockEntity.getLinkedDim());
        if (targetDimension == null) return;

        var targetWorld = server.getLevel(ResourceKey.create(Registries.DIMENSION, targetDimension));
        if (targetWorld == null) return;

        var targetPos = blockEntity.getLinkedPos();
        targetWorld.getChunk(targetPos);

        double destX = targetPos.getX() + 0.5;
        double destY = targetPos.getY() + 1.0;
        double destZ = targetPos.getZ() + 0.5;

        if (entity.isVehicle() && entity.getControllingPassenger() != null) {
            entity.getControllingPassenger().teleportTo(targetWorld, destX, destY, destZ, Set.of(), entity.getYRot(), entity.getXRot());
        }
        entity.teleportTo(targetWorld, destX, destY, destZ, Set.of(), entity.getYRot(), entity.getXRot());

        var pitch = targetWorld.random.nextFloat() + 0.1F;
        targetWorld.playSound(null, destX, destY, destZ, SoundEvents.MUD_BREAK, SoundSource.PLAYERS, 1.0F, pitch);

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
        Direction facing = ctx.getHorizontalDirection().getOpposite();
        BlockState state = this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(CONNECTION, ToiletState.DEFAULT);
        ToiletState connection = calculateConnection(ctx.getLevel(), ctx.getClickedPos(), state);

        return state.setValue(CONNECTION, connection);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return getToiletShape(state);
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.block();
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getToiletShape(state);
    }

    private VoxelShape getToiletShape(BlockState state) {
        Direction facing = state.getValue(FACING);
        ToiletState connection = state.getValue(CONNECTION);

        return switch (facing) {
            case EAST -> Shapes.or(EAST_WEST_BASE_SHAPE,
                    connection == ToiletState.BACK || connection == ToiletState.BOTH ? Shapes.empty() : WEST_CAP_SHAPE,
                    connection == ToiletState.FRONT || connection == ToiletState.BOTH ? Shapes.empty() : EAST_CAP_SHAPE);
            case WEST -> Shapes.or(EAST_WEST_BASE_SHAPE,
                    connection == ToiletState.FRONT || connection == ToiletState.BOTH ? Shapes.empty() : WEST_CAP_SHAPE,
                    connection == ToiletState.BACK || connection == ToiletState.BOTH ? Shapes.empty() : EAST_CAP_SHAPE);
            case SOUTH -> Shapes.or(NORTH_SOUTH_BASE_SHAPE,
                    connection == ToiletState.BACK || connection == ToiletState.BOTH ? Shapes.empty() : NORTH_CAP_SHAPE,
                    connection == ToiletState.FRONT || connection == ToiletState.BOTH ? Shapes.empty() : SOUTH_CAP_SHAPE);
            default -> Shapes.or(NORTH_SOUTH_BASE_SHAPE,
                    connection == ToiletState.FRONT || connection == ToiletState.BOTH ? Shapes.empty() : NORTH_CAP_SHAPE,
                    connection == ToiletState.BACK || connection == ToiletState.BOTH ? Shapes.empty() : SOUTH_CAP_SHAPE);
        };
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
            explodeToilet(level, pos);
        }
    }

    private void explodeToilet(ServerLevel level, BlockPos pos) {
        level.explode(null, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 4.0F, Level.ExplosionInteraction.BLOCK);
        level.setBlock(pos.above(), Blocks.AIR.defaultBlockState(), 3);
        var poop = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, new ItemStack(PItems.POOP.get(), 88));
        level.addFreshEntity(poop);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        super.onPlace(state, level, pos, oldState, moved);
        if (!level.isClientSide) {
            updateConnection(level, pos, state);
            updateAdjacentConnections(level, pos, state);
            if (hasHot((ServerLevel) level, pos)) {
                level.scheduleTick(pos, this, 1);
            }
        }
        if (level.getBlockEntity(pos) instanceof ToiletBlockEntity be && be.getToiletType() == null) {
            be.setToiletType(getDefaultToiletType());
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved) {
        if (!level.isClientSide) {
            if (neighborPos.equals(pos.above()) && hasHot((ServerLevel) level, pos)) {
                level.scheduleTick(pos, this, 1);
            }

            updateConnection(level, pos, state);
        }
    }

    public void updateConnection(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        ToiletState newConnection = calculateConnection(level, pos, state);
        if (newConnection != state.getValue(CONNECTION)) {
            level.setBlockAndUpdate(pos, state.setValue(CONNECTION, newConnection));
        }
    }

    private void updateAdjacentConnections(Level level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        updateNeighborConnection(level, pos.relative(facing));
        updateNeighborConnection(level, pos.relative(facing.getOpposite()));
    }

    private void updateNeighborConnection(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof AbstractToiletBlock toilet) {
            toilet.updateConnection(level, pos, state);
        }
    }

    private ToiletState calculateConnection(LevelReader level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        boolean forwardConnected = isValidNeighbor(level, pos.relative(facing), state, facing);
        boolean backwardConnected = isValidNeighbor(level, pos.relative(facing.getOpposite()), state, facing);

        if (forwardConnected && backwardConnected) return ToiletState.BOTH;
        if (forwardConnected) return ToiletState.FRONT;
        if (backwardConnected) return ToiletState.BACK;
        return ToiletState.DEFAULT;
    }

    protected boolean hasHot(ServerLevel level, BlockPos pos) {
        var above = pos.above();
        return level.isInWorldBounds(above) && level.getBlockState(above).is(Blocks.FIRE);
    }

    protected boolean isValidNeighbor(LevelReader level, BlockPos neighborPos, BlockState state, Direction facing) {
        BlockState neighbor = level.getBlockState(neighborPos);

        if (!(neighbor.getBlock() instanceof AbstractToiletBlock) || !isFaceConnected(neighbor, facing)) {
            return false;
        }

        boolean selfHasLava = state.hasProperty(BaseToiletLavaBlock.LAVA) && state.getValue(BaseToiletLavaBlock.LAVA);
        boolean neighborHasLava = neighbor.hasProperty(BaseToiletLavaBlock.LAVA) && neighbor.getValue(BaseToiletLavaBlock.LAVA);
        return selfHasLava == neighborHasLava;
    }

    private boolean isFaceConnected(BlockState state, Direction facing) {
        return state.getValue(FACING) == facing || state.getValue(FACING) == facing.getOpposite();
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, PBlockEntityType.TOILET_BLOCK_ENTITY.get(), ToiletBlockEntity::tick);
    }
}