package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.block.entity.PlacerBlockEntity;
import com.altnoir.poopsky.init.PoBlockEntityType;
import com.altnoir.poopsky.init.PoStats;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Map;
import java.util.UUID;

public class PlacerBlock extends BaseEntityBlock {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<PlacerBlock> CODEC = simpleCodec(PlacerBlock::new);
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    private static final DefaultDispenseItemBehavior DEFAULT_BEHAVIOR = new DefaultDispenseItemBehavior();
    public static final Map<Item, DispenseItemBehavior> PLACER_REGISTRY = Util.make(new Object2ObjectOpenHashMap<>(), map -> map.defaultReturnValue(DEFAULT_BEHAVIOR));

    public PlacerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PlacerBlockEntity(blockPos, blockState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof PlacerBlockEntity) {
                player.openMenu((PlacerBlockEntity) blockentity);
                player.awardStat(PoStats.INSPECT_PLACER.get());
            }

            return InteractionResult.CONSUME;
        }
    }

    protected void dispenseFrom(ServerLevel level, BlockState state, BlockPos pos) {
        PlacerBlockEntity blockEntity = level.getBlockEntity(pos, PoBlockEntityType.PLACER_BLOCK_ENTITY.get()).orElse(null);
        if (blockEntity == null) {
            LOGGER.warn("Ignoring dispensing attempt for Placer without matching block entity at {}", pos);
            return;
        }

        BlockSource blocksource = new BlockSource(level, pos, state, blockEntity);
        int i = blockEntity.getRandomSlot(level.random);
        if (i < 0) {
            level.levelEvent(1001, pos, 0);
        } else {
            ItemStack itemstack = blockEntity.getItem(i);
            if (!itemstack.isEmpty()) {
                Direction direction = state.getValue(FACING);
                Container container = HopperBlockEntity.getContainerAt(level, pos.relative(direction));

                if (container != null) {
                    ItemStack itemstack1 = HopperBlockEntity.addItem(blockEntity, container, itemstack.copyWithCount(1), direction.getOpposite());
                    if (itemstack1.isEmpty()) {
                        itemstack1 = itemstack.copy();
                        itemstack1.shrink(1);
                    } else {
                        itemstack1 = itemstack.copy();
                    }
                    blockEntity.setItem(i, itemstack1);
                } else if (itemstack.getItem() instanceof BlockItem blockItem) {
                    BlockPos targetPos = pos.relative(direction);
                    BlockState targetState = level.getBlockState(targetPos);

                    if (!level.isOutsideBuildHeight(targetPos) && targetState.canBeReplaced()) {
                        FakePlayer fakePlayer = FakePlayer.get(level, new GameProfile(UUID.randomUUID(), "Placer"));

                        fakePlayer.setPos(Vec3.atCenterOf(pos));
                        fakePlayer.setYRot(direction.toYRot());
                        var down = direction == Direction.DOWN ? 90.0F : 0.0F;
                        fakePlayer.setXRot(direction == Direction.UP ? -90.0F : down);

                        var hitResult = new BlockHitResult(Vec3.atCenterOf(targetPos), direction.getOpposite(), targetPos, false);
                        var placeContext = new BlockPlaceContext(level, fakePlayer, InteractionHand.MAIN_HAND, itemstack, hitResult);
                        var placeResult = blockItem.place(placeContext);

                        if (placeResult.consumesAction()) {
                            blockEntity.setChanged();
                            //playPlacedBlockSound(level, targetPos);
                            //level.gameEvent(GameEvent.BLOCK_PLACE, targetPos, GameEvent.Context.of(state));
                        }
                    }
                } else {
                    ItemStack itemstack1 = getDispenseMethod(level, itemstack).dispense(blocksource, itemstack);
                    blockEntity.setItem(i, itemstack1);
                }
            }
        }
    }

    private void playPlacedBlockSound(Level level, BlockPos pos) {
        BlockState placedState = level.getBlockState(pos);
        SoundType soundType = placedState.getSoundType(level, pos, null);

        level.playSound(null, pos, soundType.getPlaceSound(), SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F,
                soundType.getPitch() * 0.8F
        );
    }

    protected DispenseItemBehavior getDispenseMethod(Level level, ItemStack item) {
        return !item.isItemEnabled(level.enabledFeatures()) ? DEFAULT_BEHAVIOR : PLACER_REGISTRY.get(item.getItem());
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        boolean flag = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
        boolean flag1 = state.getValue(TRIGGERED);
        if (flag && !flag1) {
            level.scheduleTick(pos, this, 4);
            level.setBlock(pos, state.setValue(TRIGGERED, Boolean.TRUE), 2);
        } else if (!flag && flag1) {
            level.setBlock(pos, state.setValue(TRIGGERED, Boolean.FALSE), 2);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.dispenseFrom(level, state, pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    /**
     * Returns the analog signal this block emits. This is the signal a comparator can read from it.
     */
    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only, LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     */
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed blockstate.
     */
    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed blockstate.
     */
    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }
}
