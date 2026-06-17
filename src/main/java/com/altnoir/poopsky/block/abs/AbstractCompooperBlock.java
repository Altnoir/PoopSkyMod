package com.altnoir.poopsky.block.abs;

import com.altnoir.poopsky.Config;
import com.altnoir.poopsky.block.PBlocks;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class AbstractCompooperBlock extends Block {
    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 3;
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
        builder.add(LEVEL);
    }

    protected ItemInteractionResult BucketUse(ItemStack stack, Level level, BlockPos pos, Player player, InteractionHand hand, SoundEvent sound, ItemStack item) {
        return BucketUse(stack, level, pos, player, hand, sound, 1.0F, item);
    }

    protected ItemInteractionResult BucketUse(ItemStack stack, Level level, BlockPos pos, Player player, InteractionHand hand, SoundEvent sound, float pitch, ItemStack item) {
        var newState = defaultBlockState().setValue(LEVEL, MIN_LEVEL);
        setBlock(newState, level, pos, player, sound, pitch);
        //if (!player.getAbilities().instabuild) // 检测玩家是否有无限的方块
        ItemStack itemStack = ItemUtils.createFilledResult(stack, player, item);
        player.setItemInHand(hand, itemStack);

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    protected ItemInteractionResult liquidBottleUse(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, SoundEvent sound) {
        return liquidBottleUse(stack, state, level, pos, player, hand, sound, 1.0F);
    }

    protected ItemInteractionResult liquidBottleUse(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, SoundEvent sound, float pitch) {
        int currentLevel = state.getValue(LEVEL);
        int newLevel = currentLevel + 1;

        BlockState newState = state.setValue(LEVEL, newLevel);
        setBlock(newState, level, pos, player, sound, pitch);

        ItemStack itemStack = ItemUtils.createFilledResult(stack, player, Items.GLASS_BOTTLE.getDefaultInstance());
        player.setItemInHand(hand, itemStack);

        if (newLevel == MAX_LEVEL) {
            level.scheduleTick(pos, this, 20);
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    protected ItemInteractionResult glassBottleUse(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, SoundEvent sound, ItemStack item) {
        return glassBottleUse(stack, state, level, pos, player, hand, sound, 1.0F, item);
    }

    protected ItemInteractionResult glassBottleUse(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, SoundEvent sound, float pitch, ItemStack item) {
        int currentLevel = state.getValue(LEVEL);
        int newLevel = currentLevel - 1;

        BlockState newState = state.setValue(LEVEL, newLevel);
        setBlock(newState, level, pos, player, sound, pitch);

        ItemStack itemStack = ItemUtils.createFilledResult(stack, player, item);
        player.setItemInHand(hand, itemStack);

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    protected void setBlock(BlockState state, Level level, BlockPos pos, Player player, SoundEvent sound, float pitch) {
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, pitch);
        var newState = state.getValue(LEVEL) == MIN_LEVEL ? PBlocks.COMPOOPER.get().defaultBlockState() : state;
        level.setBlockAndUpdate(pos, newState);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
    }

    protected boolean isHot(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos.below());
        return state.is(Blocks.FIRE)
                || state.is(Blocks.MAGMA_BLOCK)
                || (state.is(Blocks.CAMPFIRE) && state.getValue(CampfireBlock.LIT))
                || (state.is(Blocks.SOUL_CAMPFIRE) && state.getValue(CampfireBlock.LIT));
    }

    protected boolean isEntityInsideContent(BlockPos pos, BlockState state, Entity entity) {
        double height = getLiquidHeight(state);
        return entity.getY() < (double) pos.getY() + height && entity.getBoundingBox().maxY > (double) pos.getY() + 0.125;
    }

    protected void catalyst(ItemEntity itemEntity, BlockState state, Level level, BlockPos pos, int count, ItemLike item) {
        if (Config.stickyCrafting) {
            itemEntity.setItem(new ItemStack(item, count));
        } else {
            double height = getLiquidHeight(state);

            var vec3 = Vec3.atLowerCornerWithOffset(pos, 0.5, 0.0725 + height, 0.5).offsetRandom(level.random, 0.7F);
            int newLevel = state.getValue(LEVEL) - 1;
            int newCount = count - 1;

            if (newCount > 0) {
                itemEntity.setItem(new ItemStack(Items.STICK, newCount));
                ItemEntity newItemEntity = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), new ItemStack(item));
                newItemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(newItemEntity);
            } else {
                itemEntity.setItem(new ItemStack(item));
            }

            if (newLevel > MIN_LEVEL) {
                level.setBlockAndUpdate(pos, state.setValue(LEVEL, newLevel));
            } else {
                BlockState compooperBlock = PBlocks.COMPOOPER.get().defaultBlockState();
                level.setBlockAndUpdate(pos, compooperBlock);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(itemEntity, compooperBlock));
            }

        }
        for (int i = 0; i < 8; i++) {
            level.addParticle(ParticleTypes.FIREWORK, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
                    level.random.nextDouble() * 0.2 - 0.1, level.random.nextDouble() * 0.2, level.random.nextDouble() * 0.2 - 0.1);
        }
    }

    private double getLiquidHeight(BlockState state) {
        int i = state.getValue(LEVEL);
        return switch (i) {
            case 3 -> 0.9375;
            case 2 -> 0.625;
            case 1 -> 0.3125;
            default -> 0;
        };
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}