package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.abs.AbstractCompooperBlock;
import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.item.PSItems;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class CompooperBlock extends AbstractCompooperBlock implements WorldlyContainerHolder {
    public static final MapCodec<CompooperBlock> CODEC = simpleCodec(CompooperBlock::new);
    public static final int READY = 8;
    public static final IntegerProperty POOP_LEVEL = IntegerProperty.create("level", MIN_LEVEL, READY);
    public static final Object2FloatMap<ItemLike> COMPOSTABLES = new Object2FloatOpenHashMap<>();
    private static final VoxelShape OUTER_SHAPE = Shapes.block();
    private static final VoxelShape[] SHAPES = Util.make(new VoxelShape[9], shapes -> {
        for (int i = 0; i < READY; i++) {
            shapes[i] = Shapes.join(OUTER_SHAPE, Block.box(2.0, Math.max(2, 1 + i * 2), 2.0, 14.0, 16.0, 14.0), BooleanOp.ONLY_FIRST);
        }

        shapes[READY] = shapes[READY - 1];
    });

    @Override
    public MapCodec<CompooperBlock> codec() {
        return CODEC;
    }

    public static void bootStrap() {
        COMPOSTABLES.defaultReturnValue(-1.0F);
        float f01 = 0.1F;
        float f03 = 0.3F;
        float f05 = 0.5F;
        float f08 = 0.8F;
        float f1 = 1.0F;
        add(f03, PSItems.POOP);
        add(f03, PSItems.POOP_BALL);
        add(f05, PSBlocks.POOP_SAPLING);
        add(f05, PSBlocks.POOP_LEAVES);
        add(f03, PSBlocks.POOP_PIECE);
        add(f1, PSBlocks.POOP_BLOCK);
        add(f1, PSBlocks.POOLIME_POOP_BLOCK);
        add(f08, PSBlocks.POOP_STAIRS);
        add(f05, PSBlocks.POOP_SLAB);
        add(f05, PSBlocks.POOP_VERTICAL_SLAB);
        add(f01, PSBlocks.POOP_BUTTON);
        add(f03, PSBlocks.POOP_PRESSURE_PLATE);
        add(f03, PSBlocks.POOP_FENCE);
        add(f05, PSBlocks.POOP_FENCE_GATE);
        add(f05, PSBlocks.POOP_WALL);
        add(f08, PSBlocks.POOP_DOOR);
        add(f05, PSBlocks.POOP_TRAPDOOR);
        add(f03, PSBlocks.STOOL);
        //add(0.2F, PSItems.BAKED_MAGGOTS);
        add(f01, PSItems.MAGGOTS_SEEDS);
        add(f08, PSItems.POOP_BREAD);
        add(f08, PSItems.POOP_DUMPLINGS);
        add(f08, PSItems.POOP_VEGETABLE_STICKS);
        add(f08, PSItems.POOBURGER_MEAT);
        add(f1, PSItems.POOBURGER);
        add(f08, PSItems.POOP_PASTA);
        add(f08, PSItems.POODDING);
        add(f1, PSBlocks.POOP_CAKE);
    }

    private static void add(float chance, ItemLike item) {
        COMPOSTABLES.put(item.asItem(), chance);
    }

    public CompooperBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(POOP_LEVEL, 0)
                .setValue(POWERED, false));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(POOP_LEVEL)];
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, POOP_LEVEL);
    }


    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
    ) {
        int i = state.getValue(POOP_LEVEL);
        PotionContents potioncontents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

        if (i == MIN_LEVEL) {
            if (stack.getItem() == PSItems.POOP_BUCKET.get()) {
                return liquidUse(stack, level, pos, player, hand, SoundEvents.BUCKET_EMPTY_LAVA, PSBlocks.URINE_COMPOOPER.get(), true);
            } else if (stack.getItem() == Items.WATER_BUCKET) {
                return liquidUse(stack, level, pos, player, hand, SoundEvents.BUCKET_EMPTY, PSBlocks.WATER_COMPOOPER.get(), true);
            } else if (stack.getItem() == Items.LAVA_BUCKET) {
                return liquidUse(stack, level, pos, player, hand, SoundEvents.BUCKET_EMPTY_LAVA, PSBlocks.LAVA_COMPOOPER.get(), true);
            } else if (stack.getItem() == Items.POWDER_SNOW_BUCKET) {
                return liquidUse(stack, level, pos, player, hand, SoundEvents.BUCKET_EMPTY_POWDER_SNOW, PSBlocks.POWER_SNOW_COMPOOPER.get(), true);
            } else if (stack.getItem() == PSItems.URINE_BOTTLE.get()) {
                return liquidUse(stack, level, pos, player, hand, SoundEvents.BOTTLE_EMPTY, 0.6F, PSBlocks.URINE_COMPOOPER.get(), false);
            } else if (potioncontents.is(Potions.WATER)) {
                return liquidUse(stack, level, pos, player, hand, SoundEvents.BOTTLE_EMPTY, PSBlocks.WATER_COMPOOPER.get(), false);
            }
        }
        if (i < READY && getValue(stack) > 0) {
            BlockState newState = addItem(player, state, level, pos, stack);

            level.levelEvent(1500, pos, !state.equals(newState) ? 1 : 0);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            stack.consume(1, player);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private ItemInteractionResult liquidUse(ItemStack stack, Level level, BlockPos pos, Player player, InteractionHand hand, SoundEvent sound, Block newBlock, boolean bucket) {
        return liquidUse(stack, level, pos, player, hand, sound, 1.0F, newBlock, bucket);
    }

    private ItemInteractionResult liquidUse(ItemStack stack, Level level, BlockPos pos, Player player, InteractionHand hand, SoundEvent sound, float pitch, Block newBlock, boolean bucket) {
        var newState = newBlock.defaultBlockState().setValue(AbstractCompooperBlock.LEVEL, bucket ? MAX_LEVEL : MIN_LEVEL + 1);

        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, pitch);
        level.setBlockAndUpdate(pos, newState);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));

        ItemStack itemStack = ItemUtils.createFilledResult(stack, player, bucket ? Items.BUCKET.getDefaultInstance() : Items.GLASS_BOTTLE.getDefaultInstance());
        player.setItemInHand(hand, itemStack);

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }


    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state.getValue(POOP_LEVEL) == READY - 1) {
            level.scheduleTick(pos, state.getBlock(), 20);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (!state.is(newState.getBlock())) {
            level.invalidateCapabilities(pos);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        int i = state.getValue(POOP_LEVEL);
        if (i == READY) {
            extractProduce(player, state, level, pos);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    public static void extractProduce(Entity entity, BlockState state, Level level, BlockPos pos) {
        if (!level.isClientSide) {
            var vec3 = Vec3.atLowerCornerWithOffset(pos, 0.5, 1.01, 0.5).offsetRandom(level.random, 0.7F);
            var itementity = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), new ItemStack(PSItems.SAPING_POOP_BALL.get()));
            itementity.setDefaultPickUpDelay();
            level.addFreshEntity(itementity);
        }

        empty(entity, state, level, pos);
        level.playSound(null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    protected static BlockState empty(@Nullable Entity entity, BlockState state, LevelAccessor level, BlockPos pos) {
        var blockstate = state.setValue(POOP_LEVEL, MIN_LEVEL);
        level.setBlock(pos, blockstate, 3);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
        return blockstate;
    }

    private static BlockState addItem(@Nullable Entity entity, BlockState state, LevelAccessor level, BlockPos pos, ItemStack stack) {
        int i = state.getValue(POOP_LEVEL);
        var f = getValue(stack);
        if ((i != MIN_LEVEL || !(f > 0.0F)) && !(level.getRandom().nextDouble() < (double) f)) {
            return state;
        } else {
            var j = i + 1;
            var blockstate = state.setValue(POOP_LEVEL, j);
            level.setBlock(pos, blockstate, 3);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
            if (j == READY - 1) {
                level.scheduleTick(pos, state.getBlock(), 20);
            }

            return blockstate;
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(POOP_LEVEL) == READY - 1) {
            level.setBlock(pos, state.cycle(POOP_LEVEL), 3);
            level.playSound(null, pos, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    protected static boolean shouldHandlePrecipitation(Level level, Biome.Precipitation precipitation) {
        if (precipitation == Biome.Precipitation.RAIN) {
            return level.getRandom().nextFloat() < 0.05F;
        } else {
            return precipitation == Biome.Precipitation.SNOW && level.getRandom().nextFloat() < 0.1F;
        }
    }

    @Override
    public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
        if (shouldHandlePrecipitation(level, precipitation) && state.getValue(POOP_LEVEL) == MIN_LEVEL) {
            if (precipitation == Biome.Precipitation.RAIN) {
                level.setBlockAndUpdate(pos, PSBlocks.WATER_COMPOOPER.get().defaultBlockState());
                level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
            } else if (precipitation == Biome.Precipitation.SNOW) {
                level.setBlockAndUpdate(pos, PSBlocks.POWER_SNOW_COMPOOPER.get().defaultBlockState());
                level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
            }
        }
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return blockState.getValue(POOP_LEVEL);
    }

    @Override
    public WorldlyContainer getContainer(BlockState state, LevelAccessor level, BlockPos pos) {
        int i = state.getValue(POOP_LEVEL);
        if (i == READY) {
            return new OutputContainer(state, level, pos, new ItemStack(PSItems.SAPING_POOP_BALL.get()));
        } else {
            return i < READY - 1 ? new InputContainer(state, level, pos) : new EmptyContainer();
        }
    }

    static class EmptyContainer extends SimpleContainer implements WorldlyContainer {
        public EmptyContainer() {
            super(0);
        }

        @Override
        public int[] getSlotsForFace(Direction side) {
            return new int[0];
        }

        /**
         * Returns {@code true} if automation can insert the given item in the given slot from the given side.
         */
        @Override
        public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
            return false;
        }

        /**
         * Returns {@code true} if automation can extract the given item in the given slot from the given side.
         */
        @Override
        public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
            return false;
        }
    }

    static class InputContainer extends SimpleContainer implements WorldlyContainer {
        private final BlockState state;
        private final LevelAccessor level;
        private final BlockPos pos;
        private boolean changed;

        public InputContainer(BlockState state, LevelAccessor level, BlockPos pos) {
            super(1);
            this.state = state;
            this.level = level;
            this.pos = pos;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int[] getSlotsForFace(Direction side) {
            return side == Direction.UP ? new int[]{0} : new int[0];
        }

        /**
         * Returns {@code true} if automation can insert the given item in the given slot from the given side.
         */
        @Override
        public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
            return !this.changed && direction == Direction.UP && getValue(itemStack) > 0f;
        }

        /**
         * Returns {@code true} if automation can extract the given item in the given slot from the given side.
         */
        @Override
        public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
            return false;
        }

        @Override
        public void setChanged() {
            ItemStack itemstack = this.getItem(0);
            if (!itemstack.isEmpty()) {
                this.changed = true;
                BlockState blockstate = CompooperBlock.addItem(null, this.state, this.level, this.pos, itemstack);
                this.level.levelEvent(1500, this.pos, blockstate != this.state ? 1 : 0);
                this.removeItemNoUpdate(0);
            }
        }
    }

    static class OutputContainer extends SimpleContainer implements WorldlyContainer {
        private final BlockState state;
        private final LevelAccessor level;
        private final BlockPos pos;
        private boolean changed;

        public OutputContainer(BlockState state, LevelAccessor level, BlockPos pos, ItemStack stack) {
            super(stack);
            this.state = state;
            this.level = level;
            this.pos = pos;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int[] getSlotsForFace(Direction side) {
            return side == Direction.DOWN ? new int[]{0} : new int[0];
        }

        /**
         * Returns {@code true} if automation can insert the given item in the given slot from the given side.
         */
        @Override
        public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
            return false;
        }

        /**
         * Returns {@code true} if automation can extract the given item in the given slot from the given side.
         */
        @Override
        public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
            return !this.changed && direction == Direction.DOWN && stack.is(PSItems.SAPING_POOP_BALL.get());
        }

        @Override
        public void setChanged() {
            CompooperBlock.empty(null, this.state, this.level, this.pos);
            this.changed = true;
        }
    }

    public static float getValue(ItemStack stack) {
        float value = COMPOSTABLES.getFloat(stack.getItem());
        return value == 0f ? -1f : value;
    }
}
