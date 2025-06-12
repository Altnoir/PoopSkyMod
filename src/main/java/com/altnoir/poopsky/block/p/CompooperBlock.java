package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.item.PSItems;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class CompooperBlock extends Block implements WorldlyContainerHolder {
    public static final MapCodec<CompooperBlock> CODEC = simpleCodec(CompooperBlock::new);
    public static final int READY = 4;
    public static final int MIN_LEVEL = 0;
    public static final int MAX_LEVEL = 3;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", MIN_LEVEL, READY);
    public static final BooleanProperty LIQUID = BooleanProperty.create("liquid");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final Object2FloatMap<ItemLike> COMPOSTABLES = new Object2FloatOpenHashMap<>();
    private static final VoxelShape OUTER_SHAPE = Shapes.block();
    private static final VoxelShape[] SHAPES = Util.make(new VoxelShape[9], shapes -> {
        for (int i = 0; i < READY; i++) {
            shapes[i] = Shapes.join(OUTER_SHAPE, Block.box(2.0, Math.max(2, 1 + i * 2), 2.0, 14.0, 16.0, 14.0), BooleanOp.ONLY_FIRST);
        }

        shapes[READY] = shapes[MAX_LEVEL];
    });


    @Override
    public MapCodec<CompooperBlock> codec() {
        return CODEC;
    }

    public static final Item[] SAPLINGS = new Item[] {
            Items.OAK_SAPLING,
            Items.SPRUCE_SAPLING,
            Items.BIRCH_SAPLING,
            Items.JUNGLE_SAPLING,
            Items.ACACIA_SAPLING,
            Items.DARK_OAK_SAPLING,
            Items.MANGROVE_PROPAGULE,
            Items.CHERRY_SAPLING
    };

    public static void bootStrap() {
        COMPOSTABLES.defaultReturnValue(-1.0F);
        float f = 0.3F;
        float f1 = 0.5F;
        float f2 = 0.65F;
        float f3 = 0.85F;
        float f4 = 1.0F;
        add(0.1F, PSItems.POOP);
        add(0.1F, PSItems.POOP_BALL);
        //add(0.2F, PSBlocks.POOP_SAPLING);
        //add(0.2F, PSBlocks.POOP_LEAVES);
        add(0.1F, PSBlocks.POOP_PIECE);
        add(0.3F, PSBlocks.POOP_BLOCK);
        //add(0.3F, PSBlocks.POOP_STAIRS);
        //add(0.15F, PSBlocks.POOP_SLAB);
        //add(0.15F, PSBlocks.POOP_VERTICAL_SLAB);
        //add(0.05F, PSBlocks.POOP_BUTTON);
        //add(0.1F, PSBlocks.POOP_PRESSURE_PLATE);
        //add(0.1F, PSBlocks.POOP_FENCE);
        //add(0.1F, PSBlocks.POOP_FENCE_GATE);
        //add(0.1F, PSBlocks.POOP_WALL);
        //add(0.25F, PSBlocks.POOP_DOOR);
        //add(0.15F, PSBlocks.POOP_TRAPDOOR);
        //add(0.1F, PSBlocks.STOOL);
        //add(0.2F, PSItems.BAKED_MAGGOTS);
        //add(0.5F, PSItems.POOP_BREAD);
        //add(0.1F, PSItems.MAGGOTS_SEEDS);
        //add(1.0F, PSBlocks.POOP_CAKE);
    }

    private static void add(float chance, ItemLike item) {
        COMPOSTABLES.put(item.asItem(), chance);
    }

    public CompooperBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LEVEL, 0)
                .setValue(LIQUID, false)
                .setValue(POWERED, false));
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
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state.getValue(LEVEL) == READY) {
            level.scheduleTick(pos, state.getBlock(), 20);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
    ) {
        int i = state.getValue(LEVEL);
        var hasLiquid = state.getValue(LIQUID);
        if (i < READY) {
            if (i < MAX_LEVEL && !level.isClientSide) {
                if (stack.getItem() == Items.WATER_BUCKET) {
                    return waterBucketUse(level, pos, player, hand);
                }
                if (stack.getItem() == PSItems.URINE_BOTTLE.get() && ( hasLiquid || i == MIN_LEVEL)) {
                    return liquidItemUse(i, state, level, pos, player, hand);
                }
                if (!hasLiquid && getValue(stack) > 0) {
                    return itemUse(player, state, level, pos, stack);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }
    }

    protected ItemInteractionResult waterBucketUse(Level level, BlockPos pos, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (!level.isClientSide) {
            var newState = defaultBlockState()
                    .setValue(LEVEL, READY)
                    .setValue(LIQUID, true);
            level.setBlock(pos, newState, Block.UPDATE_ALL);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
                if (!player.getInventory().contains(new ItemStack(Items.BUCKET))) {
                    player.addItem(new ItemStack(Items.BUCKET));
                }
            }
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    protected ItemInteractionResult liquidItemUse(int levelValue, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);

        if (levelValue < MAX_LEVEL && !level.isClientSide) {
            addLiquidToComposter(state, level, pos, stack);
            level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
                if (!player.getInventory().contains(new ItemStack(Items.GLASS_BOTTLE))) {
                    player.addItem(new ItemStack(Items.GLASS_BOTTLE));
                }
            }
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    protected static void addLiquidToComposter(BlockState state, LevelAccessor level, BlockPos pos, ItemStack stack) {
        int i = state.getValue(LEVEL);
        var f = 1.0F;

        if (i != 0 && !(level.getRandom().nextDouble() < f)) return;

        int j = i + 1;

        var blockState = state.setValue(LEVEL, j).setValue(LIQUID, true);
        level.setBlock(pos, blockState, 3);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
        if (j == MAX_LEVEL) {
            level.scheduleTick(pos, state.getBlock(), 20);
        }
    }

    protected ItemInteractionResult itemUse(Player player, BlockState state, Level level, BlockPos pos, ItemStack stack) {
        var newState = addItem(player, state, level, pos, stack);
        level.levelEvent(1500, pos, !state.equals(newState) ? 1 : 0);
        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        stack.consume(1, player);
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        int i = state.getValue(LEVEL);
        if (i == READY) {
            if (!state.getValue(LIQUID)) {
                extractProduce(player, state, level, pos);
            }
            else {
                var stack = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (stack.getItem() == Items.BUCKET) {
                    if (!player.isCreative()) stack.shrink(1);
                    player.getInventory().add(new ItemStack(Items.WATER_BUCKET));
                    empty(player, state, level, pos);
                    level.playSound(player, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    public static BlockState insertItem(Entity entity, BlockState state, ServerLevel level, ItemStack stack, BlockPos pos) {
        int i = state.getValue(LEVEL);
        if (i < MAX_LEVEL && getValue(stack) > 0) {
            var blockstate = addItem(entity, state, level, pos, stack);
            stack.shrink(1);
            return blockstate;
        } else {
            return state;
        }
    }

    public static BlockState extractProduce(Entity entity, BlockState state, Level level, BlockPos pos) {
        if (!level.isClientSide) {
            var vec3 = Vec3.atLowerCornerWithOffset(pos, 0.5, 1.01, 0.5).offsetRandom(level.random, 0.7F);
            var itementity = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), new ItemStack(randomProduce()));
            itementity.setDefaultPickUpDelay();
            level.addFreshEntity(itementity);
        }

        var blockstate = empty(entity, state, level, pos);
        level.playSound(null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        return blockstate;
    }

    protected static BlockState empty(@Nullable Entity entity, BlockState state, LevelAccessor level, BlockPos pos) {
        var blockstate = state.setValue(LEVEL, Integer.valueOf(MIN_LEVEL)).setValue(LIQUID, false);
        level.setBlock(pos, blockstate, 3);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
        return blockstate;
    }

    private static Item randomProduce() {
        var random = RandomSource.create();
        return SAPLINGS[random.nextInt(SAPLINGS.length)];
    }

    protected static BlockState addItem(@Nullable Entity entity, BlockState state, LevelAccessor level, BlockPos pos, ItemStack stack) {
        int i = state.getValue(LEVEL);
        var f = getValue(stack);
        if ((i != MIN_LEVEL || !(f > 0.0F)) && !(level.getRandom().nextDouble() < (double)f)) {
            return state;
        } else {
            var j = i + 1;
            var blockstate = state.setValue(LEVEL, j).setValue(LIQUID, false);
            level.setBlock(pos, blockstate, 3);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
            if (j == MAX_LEVEL) {
                level.scheduleTick(pos, state.getBlock(), 20);
            }

            return blockstate;
        }
    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(LEVEL) == MAX_LEVEL && (!state.getValue(LIQUID) || hasFire(level, pos))) {
            if (state.getValue(LIQUID)) {
                level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);

                var waterColor = level.getBiome(pos).value().getWaterColor();
                var red = (waterColor >> 16 & 0xFF) / 255.0F;
                var green = (waterColor >> 8 & 0xFF) / 255.0F;
                var blue = (waterColor & 0xFF) / 255.0F;

                var color = new Vector3f(red, green, blue);

                level.sendParticles(
                        new DustColorTransitionOptions(color, color, 1.0F),
                        pos.getX() + 0.5,
                        pos.getY() + 1.2,
                        pos.getZ() + 0.5,
                        15,
                        0.3, 0.1, 0.3,
                        0.02
                );
            } else {
                level.playSound(null, pos, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            level.setBlock(pos, state.cycle(LEVEL), Block.UPDATE_ALL);
        }
        level.scheduleTick(pos, this, 20);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIQUID) && state.getValue(LEVEL) == MIN_LEVEL) {
            var biome = level.getBiome(pos).value();
            var precipitation = biome.getPrecipitationAt(pos);
            var isRaining = level.isRaining() && level.canSeeSky(pos);

            if (!isRaining) return;

            boolean canFill = false;
            if (precipitation == Biome.Precipitation.RAIN) {
                canFill = random.nextFloat() < 0.05F;
            } else if (precipitation == Biome.Precipitation.SNOW) {
                canFill = random.nextFloat() < 0.1F;
            }

            if (canFill) {
                var newState = state.setValue(LIQUID, true).setValue(LEVEL, READY);
                level.setBlock(pos, newState, Block.UPDATE_ALL);
                level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
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

                    if (!level.isOutsideBuildHeight(targetPos) && isReplaceable(targetState)) {
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

        if (fromPos.equals(pos.below()) && state.getValue(LEVEL) == MAX_LEVEL && hasFire((ServerLevel)level, pos)) {
            level.scheduleTick(pos, this, 20);
        }

        super.neighborChanged(state, level, pos, blockIn, fromPos, isMoving);
    }

    private boolean isReplaceable(BlockState state) {
        return state.isAir() ||
                state.is(Blocks.WATER) ||
                state.is(Blocks.LAVA);
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
        builder.add(LIQUID);
        builder.add(POWERED);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    private boolean hasFire(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos.below());
        return state.is(Blocks.FIRE)
                || state.is(Blocks.MAGMA_BLOCK)
                || (state.is(Blocks.CAMPFIRE) && state.getValue(CampfireBlock.LIT))
                || (state.is(Blocks.SOUL_CAMPFIRE) && state.getValue(CampfireBlock.LIT));
    }

    @Override
    public WorldlyContainer getContainer(BlockState state, LevelAccessor level, BlockPos pos) {
        int i = state.getValue(LEVEL);
        if (i == READY) {
            return new CompooperBlock.OutputContainer(state, level, pos, new ItemStack(randomProduce()));
        } else {
            return i < 7 ? new InputContainer(state, level, pos) : new EmptyContainer();
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
            return !this.changed && direction == Direction.DOWN && stack.is(Items.BONE_MEAL);
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
