package com.altnoir.poopsky.block;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractCompooperBlock extends Block {
    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 3;
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", MIN_LEVEL, MAX_LEVEL);
    private static final VoxelShape OUTER_SHAPE = Shapes.block();
    private static final VoxelShape[] SHAPES = Util.make(new VoxelShape[4], shapes -> {
        for (int i = 0; i <= MAX_LEVEL; i++) {
            shapes[i] = Shapes.join(OUTER_SHAPE, Block.box(2.0, Math.max(2, i * 5), 2.0, 14.0, 16.0, 14.0), BooleanOp.ONLY_FIRST);
        }
    });

    public AbstractCompooperBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(LEVEL)];
    }

    @Override
    protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return OUTER_SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[0];
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return blockState.getValue(LEVEL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, LEVEL);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        boolean hasPower = level.hasNeighborSignal(pos);
        if (hasPower != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, hasPower), Block.UPDATE_ALL);

            if (hasPower && !level.isClientSide) {
                var be = level.getBlockEntity(pos.below());

                if (!(be instanceof Container container)) return;

                for (int slot = 0; slot < container.getContainerSize(); slot++) {
                    var stack = container.getItem(slot);
                    if (stack.isEmpty()) continue;

                    var block = Block.byItem(stack.getItem());
                    if (block == null || block.defaultBlockState().isAir()) {
                        if (stack.getItem() instanceof BlockItem bi) {
                            block = bi.getBlock();
                        }
                    }

                    if (block == null || block.defaultBlockState().isAir()) continue;

                    var targetPos = pos.above();
                    var targetState = level.getBlockState(targetPos);

                    if (!level.isOutsideBuildHeight(targetPos) && targetState.canBeReplaced()) {
                        if (level.setBlock(targetPos, block.defaultBlockState(), Block.UPDATE_ALL)) {
                            try {
                                var placeSound = block.defaultBlockState().getSoundType().getPlaceSound();
                                level.playSound(null, targetPos, placeSound, SoundSource.BLOCKS, 1.0F, 1.0F);
                            } catch (Exception e) {
                                level.playSound(null, targetPos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                            }
                            container.removeItem(slot, 1);
                            break;
                        }
                    } else {
                        System.out.println(targetState.getBlock().getName().getString() + " False");
                        break;
                    }
                }
            }
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    }

    protected ItemInteractionResult BucketUse(ItemStack stack, Level level, BlockPos pos, Player player, InteractionHand hand, SoundEvent sound, ItemStack item) {
        var newState = defaultBlockState().setValue(LEVEL, MIN_LEVEL);
        setBlock(newState, level, pos, player, sound);
        //if (!player.getAbilities().instabuild) // 检测玩家是否有无限的方块
        ItemStack itemStack = ItemUtils.createFilledResult(stack, player, item);
        player.setItemInHand(hand, itemStack);

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    protected ItemInteractionResult liquidBottleUse(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, SoundEvent sound) {
        int currentLevel = state.getValue(LEVEL);
        int newLevel = currentLevel + 1;

        BlockState newState = state.setValue(LEVEL, newLevel);
        useItemUtils(newState, level, pos, player, sound);

        ItemStack itemStack = ItemUtils.createFilledResult(stack, player, Items.GLASS_BOTTLE.getDefaultInstance());
        player.setItemInHand(hand, itemStack);

        if (newLevel == MAX_LEVEL) {
            level.scheduleTick(pos, this, 20);
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    protected ItemInteractionResult glassBottleUse(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, SoundEvent sound, ItemStack item) {
        int currentLevel = state.getValue(LEVEL);
        int newLevel = currentLevel - 1;

        BlockState newState = state.setValue(LEVEL, newLevel);
        if (newLevel == MIN_LEVEL) {
            setBlock(newState, level, pos, player, sound);
        } else {
            useItemUtils(newState, level, pos, player, sound);
        }

        ItemStack itemStack = ItemUtils.createFilledResult(stack, player, item);
        player.setItemInHand(hand, itemStack);

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    private void useItemUtils(BlockState state, Level level, BlockPos pos, Player player, SoundEvent sound) {
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.setBlockAndUpdate(pos, state);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
    }

    private void setBlock(BlockState state, Level level, BlockPos pos, Player player, SoundEvent sound) {
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        BlockState compooperBlock = PSBlocks.COMPOOPER.get().defaultBlockState();
        level.setBlockAndUpdate(pos, compooperBlock);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
    }

    protected boolean isHot(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos.below());
        return state.is(Blocks.FIRE)
                || state.is(Blocks.MAGMA_BLOCK)
                || (state.is(Blocks.CAMPFIRE) && state.getValue(CampfireBlock.LIT))
                || (state.is(Blocks.SOUL_CAMPFIRE) && state.getValue(CampfireBlock.LIT));
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
