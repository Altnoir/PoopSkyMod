package com.altnoir.poopsky.content.block.abs;

import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.content.block.p.BaseToiletLavaBlock;
import com.altnoir.poopsky.content.item.p.ToiletBlockItem;
import com.altnoir.poopsky.data.sound.PoSoundEvents;
import com.altnoir.poopsky.fabric.port.util.ItemAbilities;
import com.altnoir.poopsky.fabric.port.util.ParticleOptionUtils;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.impl.util.ToiletUtil;
import com.altnoir.poopsky.init.*;
import com.altnoir.poopsky.worldgen.PoConfigureFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractToiletBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<ToiletState> CONNECTION = EnumProperty.create("connection", ToiletState.class);

    private static final float EXPLOSION_POWER = 4.0F;
    private static final int EXPLOSION_POOP_COUNT = 88;
    private static final int ANVIL_POOP_COUNT = 8;
    private static final double TOILET_USE_Y = 1.5;
    private static final float MIN_TELEPORT_FALL_DISTANCE = 1.0F;

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
        ToiletTypes.init();
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
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return ToiletBlockItem.withType(this, getToiletTypeOrDefault(level, pos));
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        Block sourceBlock = getVariantSourceBlock(level, pos);
        return sourceBlock != null ? sourceBlock.defaultBlockState().getSoundType(level, pos, entity) : super.getSoundType(state, level, pos, entity);
    }

    public BlockState getParticleState(BlockState state, BlockGetter level, BlockPos pos) {
        Block sourceBlock = getVariantSourceBlock(level, pos);
        return sourceBlock != null ? sourceBlock.defaultBlockState() : state;
    }

    @Override
    public boolean addLandingEffects(BlockState state, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        BlockState particleState = getParticleState(state, level, pos);
        if (particleState == state) {
            return false;
        }

        level.sendParticles(ParticleOptionUtils.setBlockPos(new BlockParticleOption(ParticleTypes.BLOCK, particleState), pos),
                entity.getX(), entity.getY(), entity.getZ(), numberOfParticles, 0.0, 0.0, 0.0, 0.15F);
        return true;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!stack.canPerformAction(ItemAbilities.FIRESTARTER_LIGHT)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        Item item = stack.getItem();
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            explodeToilet(serverLevel, pos);
        }

        consumeFireStarter(stack, player, hand);
        player.awardStat(Stats.ITEM_USED.get(item));
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    private void consumeFireStarter(ItemStack stack, Player player, InteractionHand hand) {
        if (stack.is(Items.FIRE_CHARGE)) {
            stack.consume(1, player);
            return;
        }
        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
    }

    @Nullable
    protected ItemInteractionResult handleVariantReplacement(ItemStack stack, Level level, BlockPos pos, Player player, ToiletType.Category acceptedCategory) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return null;
        }

        ToiletType newType = ToiletType.bySourceBlock(blockItem.getBlock());
        if (newType == null || newType == getToiletTypeOrDefault(level, pos) || newType.category() != acceptedCategory) {
            return null;
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        if (!(level.getBlockEntity(pos) instanceof ToiletBlockEntity blockEntity)) {
            return null;
        }

        replaceVariant(blockItem, stack, level, pos, player, blockEntity, newType);
        return ItemInteractionResult.sidedSuccess(false);
    }

    private void replaceVariant(BlockItem blockItem, ItemStack stack, Level level, BlockPos pos, Player player, ToiletBlockEntity blockEntity, ToiletType newType) {
        ToiletType currentType = blockEntity.getToiletType();
        if (currentType == newType) {
            return;
        }

        blockEntity.setToiletType(newType);
        playVariantReplaceSound(blockItem, level, pos, player);

        stack.consume(1, player);
        if (!player.getAbilities().instabuild && currentType != null) {
            dropVariantSource(level, pos, currentType);
        }
    }

    private void playVariantReplaceSound(BlockItem blockItem, Level level, BlockPos pos, Player player) {
        SoundType sound = blockItem.getBlock().defaultBlockState().getSoundType(level, pos, player);
        level.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
    }

    private void dropVariantSource(Level level, BlockPos pos, ToiletType type) {
        Block oldBlock = type.sourceBlock();
        if (oldBlock == null) {
            return;
        }

        ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.72, pos.getZ() + 0.5, new ItemStack(oldBlock));
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
    }

    @Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!oldState.is(newState.getBlock())) {
            if (!level.isClientSide) {
                updateAdjacentConnections(level, pos, oldState);
            }
        }
        super.onRemove(oldState, level, pos, newState, isMoving);
    }

    @Override
    public void fallOn(Level level, BlockState blockState, BlockPos pos, Entity entity, float fallDistance) {
        if (!level.isClientSide) {
            if (entity instanceof FallingBlockEntity falling && isAnvil(falling.getBlockState())) {
                poopAnvil(level, pos, entity);
            }

            if (ToiletUtil.tryTeleportFromFall(level, pos, entity, fallDistance)) {
                return;
            }
        }
        super.fallOn(level, blockState, pos, entity, fallDistance);
    }

    protected boolean isAnvil(BlockState state) {
        return state.is(Blocks.ANVIL) || state.is(Blocks.CHIPPED_ANVIL) || state.is(Blocks.DAMAGED_ANVIL);
    }

    private void poopAnvil(Level level, BlockPos pos, Entity entity) {
        Item poopItem = ToiletUtil.isGoldenToilet(level, pos) ? PoItems.GOLDEN_POOP.get() : PoItems.POOP.get();
        var poop = new ItemEntity(level, entity.getX(), entity.getY() + 0.1, entity.getZ(), new ItemStack(poopItem, ANVIL_POOP_COUNT));
        poop.setDefaultPickUpDelay();
        level.addFreshEntity(poop);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide && entity instanceof Player player) {
            if (player.isShiftKeyDown() && ToiletUtil.isEntityCentered(pos, player)) {
                var playerData = player.getCustomData();
                long lastPoopTime = playerData.getLong("poopTime");
                ToiletUtil.canPoop(level, player, player.hasEffect(PoEffects.holder(PoEffects.INTESTINAL_SPASM)), false, 0.1F, 0.5F, lastPoopTime,
                        time -> playerData.putLong("poopTime", time));
            }
        }
    }


    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockState(pos.below()).is(Blocks.MAGMA_BLOCK)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5;
            var pitch = level.random.nextFloat() - 0.4F;
            level.sendParticles(PoParticles.TOILET_PARTICLE.get(), x, y - 0.5, z, 100, 0.05, 0.05, 0.05, 0.5);
            level.playSound(null, x, y, z, PoSoundEvents.FART, SoundSource.BLOCKS, 1.0F, pitch);

            AABB area = new AABB(pos.getX(), y, pos.getZ(), pos.getX() + 1, y + 2, pos.getZ() + 1);
            for (Entity entity : level.getEntitiesOfClass(Entity.class, area)) {
                entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 1.6, 0.0));
                entity.hurtMarked = true;
                entity.hasImpulse = true;
            }
        }

        if (state.getBlock() instanceof BaseToiletLavaBlock && state.getValue(BaseToiletLavaBlock.LAVA)) {
            return;
        }

        if (level.getBlockState(pos.below()).is(PoTags.Blocks.POOP_BLOCKS)) {
            level.registryAccess()
                    .registry(Registries.CONFIGURED_FEATURE)
                    .flatMap(holder -> holder.getHolder(PoConfigureFeatures.SALTPETER_PATCH))
                    .ifPresent(reference -> reference.value().place(level, level.getChunkSource().getGenerator(), random, pos.above()));
        }
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
                    capUnlessConnected(connection, ToiletState.BACK, WEST_CAP_SHAPE),
                    capUnlessConnected(connection, ToiletState.FRONT, EAST_CAP_SHAPE));
            case WEST -> Shapes.or(EAST_WEST_BASE_SHAPE,
                    capUnlessConnected(connection, ToiletState.FRONT, WEST_CAP_SHAPE),
                    capUnlessConnected(connection, ToiletState.BACK, EAST_CAP_SHAPE));
            case SOUTH -> Shapes.or(NORTH_SOUTH_BASE_SHAPE,
                    capUnlessConnected(connection, ToiletState.BACK, NORTH_CAP_SHAPE),
                    capUnlessConnected(connection, ToiletState.FRONT, SOUTH_CAP_SHAPE));
            default -> Shapes.or(NORTH_SOUTH_BASE_SHAPE,
                    capUnlessConnected(connection, ToiletState.FRONT, NORTH_CAP_SHAPE),
                    capUnlessConnected(connection, ToiletState.BACK, SOUTH_CAP_SHAPE));
        };
    }

    private VoxelShape capUnlessConnected(ToiletState connection, ToiletState side, VoxelShape cap) {
        return connection == side || connection == ToiletState.BOTH ? Shapes.empty() : cap;
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
        BlockPos above = pos.above();
        Item poopItem = ToiletUtil.isGoldenToilet(level, pos) ? PoItems.GOLDEN_POOP.get() : PoItems.POOP.get();
        level.explode(null, pos.getX() + 0.5, pos.getY() + TOILET_USE_Y, pos.getZ() + 0.5, EXPLOSION_POWER, Level.ExplosionInteraction.BLOCK);
        if (level.getBlockState(above).is(Blocks.FIRE)) {
            level.setBlock(above, Blocks.AIR.defaultBlockState(), 3);
        }
        ItemEntity poop = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + TOILET_USE_Y, pos.getZ() + 0.5, new ItemStack(poopItem, EXPLOSION_POOP_COUNT));
        level.addFreshEntity(poop);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        super.onPlace(state, level, pos, oldState, moved);
        if (level instanceof ServerLevel serverLevel) {
            updateConnection(serverLevel, pos, state);
            updateAdjacentConnections(serverLevel, pos, state);
            if (hasHot(serverLevel, pos)) {
                level.scheduleTick(pos, this, 1);
            }
        }
        if (level.getBlockEntity(pos) instanceof ToiletBlockEntity be && be.getToiletType() == null) {
            be.setToiletType(getDefaultToiletType());
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean moved) {
        if (level instanceof ServerLevel serverLevel) {
            if (neighborPos.equals(pos.above()) && hasHot(serverLevel, pos)) {
                level.scheduleTick(pos, this, 1);
            }

            updateConnection(serverLevel, pos, state);
        }
    }

    private void updateConnection(Level level, BlockPos pos, BlockState state) {
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

    @FunctionalInterface
    public interface ToiletExplosionConsumer {
        void accept(AbstractToiletBlock toilet, ServerLevel level, BlockPos pos, ItemStack stack);
    }

    public static void dispenserToiletExplosion(ItemLike item, ToiletExplosionConsumer onToilet) {
        DispenseItemBehavior original = DispenserBlock.DISPENSER_REGISTRY.get(item.asItem());
        DispenserBlock.registerBehavior(item, new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                ServerLevel level = source.level();
                Direction direction = source.state().getValue(DispenserBlock.FACING);
                BlockPos pos = source.pos().relative(direction);

                if (level.getBlockState(pos).getBlock() instanceof AbstractToiletBlock toilet) {
                    this.setSuccess(true);
                    toilet.explodeToilet(level, pos);
                    onToilet.accept(toilet, level, pos, stack);
                    return stack;
                }

                return original.dispense(source, stack);
            }
        });
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, PoBlockEntityType.TOILET_BLOCK_ENTITY.get(), ToiletBlockEntity::tick);
    }
}
