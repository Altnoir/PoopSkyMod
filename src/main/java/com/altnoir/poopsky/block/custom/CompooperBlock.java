package com.altnoir.poopsky.block.custom;

import com.altnoir.poopsky.block.PSBlocks;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.tag.PSItemTags;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.*;
import net.minecraft.particle.DustColorTransitionParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class CompooperBlock extends Block implements InventoryProvider {
    public static final MapCodec<CompooperBlock> CODEC = createCodec(CompooperBlock::new);
    public static final IntProperty LEVEL = IntProperty.of("level", 0, 4);
    public static final BooleanProperty LIQUID = BooleanProperty.of("liquid");
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    public static final Object2FloatMap<ItemConvertible> ITEM_TO_LEVEL_INCREASE_CHANCE = new Object2FloatOpenHashMap<>();
    private static final VoxelShape RAYCAST_SHAPE = VoxelShapes.fullCube();
    private static final VoxelShape COLLISION_SHAPE = VoxelShapes.combineAndSimplify(
            RAYCAST_SHAPE,
            Block.createCuboidShape(2.0, 2.0, 2.0, 14.0, 16.0, 14.0),
            BooleanBiFunction.ONLY_FIRST
    );

    @Override
    public MapCodec<CompooperBlock> getCodec() {
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

    public static void registerDefaultCompostableItems() {
        registerCompostableItem(0.1F, PSItems.POOP);
        registerCompostableItem(0.1F, PSItems.POOP_BALL);
        registerCompostableItem(0.2F, PSBlocks.POOP_SAPLING);
        registerCompostableItem(0.2F, PSBlocks.POOP_LEAVES);
        registerCompostableItem(0.1F, PSBlocks.POOP_PIECE);
        registerCompostableItem(0.3F, PSBlocks.POOP_BLOCK);
        registerCompostableItem(0.3F, PSBlocks.POOP_STAIRS);
        registerCompostableItem(0.15F, PSBlocks.POOP_SLAB);
        registerCompostableItem(0.15F, PSBlocks.POOP_VERTICAL_SLAB);
        registerCompostableItem(0.05F, PSBlocks.POOP_BUTTON);
        registerCompostableItem(0.1F, PSBlocks.POOP_PRESSURE_PLATE);
        registerCompostableItem(0.1F, PSBlocks.POOP_FENCE);
        registerCompostableItem(0.1F, PSBlocks.POOP_FENCE_GATE);
        registerCompostableItem(0.1F, PSBlocks.POOP_WALL);
        registerCompostableItem(0.25F, PSBlocks.POOP_DOOR);
        registerCompostableItem(0.15F, PSBlocks.POOP_TRAPDOOR);
        registerCompostableItem(0.1F, PSBlocks.STOOL);
        registerCompostableItem(0.2F, PSItems.BAKED_MAGGOTS);
        registerCompostableItem(0.5F, PSItems.POOP_BREAD);
//        registerCompostableItem(0.1F, PSItems.MAGGOTS_SEEDS);
//        registerCompostableItem(1.0F, PSBlocks.POOP_CAKE); // 不知道什么神必bug，如果加上这两会提示item为null
    }

    static {
        ITEM_TO_LEVEL_INCREASE_CHANCE.defaultReturnValue(-1.0F);
        registerDefaultCompostableItems();
    }

    private static void registerCompostableItem(float chance, ItemConvertible item) {
        ITEM_TO_LEVEL_INCREASE_CHANCE.put(item.asItem(), chance);
    }

    public CompooperBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(LEVEL, 0).with(LIQUID, false).with(POWERED, false));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return RAYCAST_SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if ((Integer)state.get(LEVEL) == 3) {
            world.scheduleBlockTick(pos, state.getBlock(), 20);
        }
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int level = state.get(LEVEL);
        boolean hasLiquid = state.get(LIQUID);

        if (level < 3) {
            if (stack.getItem() == Items.WATER_BUCKET && level == 0) {
                return waterBucket(world, pos, player, stack);
            }
            if (stack.getItem() == PSItems.URINE_BOTTLE && (hasLiquid || level == 0)) {
                return LiquidItemUse(level, state, world, pos, player, stack);
            }
            if (ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(stack.getItem()) && !hasLiquid) {
                return CompostItemUse(level, state, world, pos, player, stack);
            }
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    private ItemActionResult waterBucket(World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
        world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);

        if (!world.isClient) {
            BlockState newState = getDefaultState().with(LEVEL, 4).with(LIQUID, true);
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);
            stack.decrementUnlessCreative(1, player);
            if (!player.isCreative() || !player.getInventory().contains(Items.BUCKET.getDefaultStack())) {
                player.giveItemStack(new ItemStack(Items.BUCKET));
            }
        }
        return ItemActionResult.success(world.isClient);
    }

    private ItemActionResult LiquidItemUse(int level, BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
        if (level < 3 && !world.isClient) {
            addLiquidToComposter(player, state, world, pos, stack);
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            stack.decrementUnlessCreative(1, player);
            if (!player.isCreative() || !player.getInventory().contains(Items.GLASS_BOTTLE.getDefaultStack())) {
                player.giveItemStack(new ItemStack(Items.GLASS_BOTTLE));
            }
        }
        return ItemActionResult.success(world.isClient);
    }

    private ItemActionResult CompostItemUse(int level, BlockState state, World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
        if (level < 3 && !world.isClient) {
            BlockState blockState = addToComposter(player, state, world, pos, stack);
            world.syncWorldEvent(WorldEvents.COMPOSTER_USED, pos, state != blockState ? 1 : 0);
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            stack.decrementUnlessCreative(1, player);
        }
        return ItemActionResult.success(world.isClient);
    }


    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (state.get(LEVEL) == 4) {
            if (!state.get(LIQUID)) {
                emptyFullComposter(player, state, world, pos);
                return ActionResult.success(world.isClient);
            } else {
                ItemStack stackInHand = player.getStackInHand(Hand.MAIN_HAND);
                if (stackInHand.isOf(Items.BUCKET)) {
                    stackInHand.decrementUnlessCreative(1, player);
                    if (!player.isCreative() || !player.getInventory().contains(Items.WATER_BUCKET.getDefaultStack())) {
                        player.giveItemStack(new ItemStack(Items.WATER_BUCKET));
                    }
                    world.setBlockState(pos, getDefaultState(), Block.NOTIFY_ALL);
                    world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    return ActionResult.success(world.isClient);
                }
                return ActionResult.PASS;
            }
        }
        return ActionResult.PASS;
    }

    public static BlockState emptyFullComposter(Entity user, BlockState state, World world, BlockPos pos) {
        if (!world.isClient && !state.get(LIQUID)) {
            Item randomSapling = SAPLINGS[world.random.nextInt(SAPLINGS.length)];
            Vec3d vec3d = Vec3d.add(pos, 0.5, 1.01, 0.5).addRandom(world.random, 0.7F);

            ItemEntity itemEntity = new ItemEntity(world, vec3d.getX(), vec3d.getY(), vec3d.getZ(), new ItemStack(randomSapling));
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
        }
        return emptyComposter(user, state, world, pos);
    }

    static BlockState emptyComposter(@Nullable Entity user, BlockState state, WorldAccess world, BlockPos pos) {
        BlockState blockState = state.with(LEVEL, 0);
        world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(user, blockState));
        return blockState;
    }

    static BlockState addToComposter(@Nullable Entity user, BlockState state, WorldAccess world, BlockPos pos, ItemStack stack) {
        int i = (Integer)state.get(LEVEL);
        float f = ITEM_TO_LEVEL_INCREASE_CHANCE.getFloat(stack.getItem());

        if ((i != 0 || !(f > 0.0F)) && !(world.getRandom().nextDouble() < f)) {
            return state;
        } else {
            int j = i + 1;
            BlockState blockState = state.with(LEVEL, j);
            world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(user, blockState));
            if (j == 3) {
                world.scheduleBlockTick(pos, state.getBlock(), 20);
            }
            return blockState;
        }
    }
    static void addLiquidToComposter(@Nullable Entity user, BlockState state, WorldAccess world, BlockPos pos, ItemStack stack) {
        int i = (Integer)state.get(LEVEL);
        float f = 1.0F;

        if (i != 0 && !(world.getRandom().nextDouble() < f)) return;

        int j = i + 1;
        BlockState blockState = state.with(LEVEL, j).with(LIQUID, true);
        world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(user, blockState));
        if (j == 3) {
            world.scheduleBlockTick(pos, state.getBlock(), 20);
        }
    }

    private boolean hasFire(ServerWorld world, BlockPos pos) {
        BlockState state = world.getBlockState(pos.down());
        return state.isOf(Blocks.FIRE) || state.isOf(Blocks.MAGMA_BLOCK)
                || (state.isOf(Blocks.CAMPFIRE) && state.get(CampfireBlock.LIT))
                || (state.isOf(Blocks.SOUL_CAMPFIRE) && state.get(CampfireBlock.LIT));
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(LEVEL) == 3 && (!state.get(LIQUID) || hasFire(world, pos))) {
            if (state.get(LIQUID)) {
                world.playSound(null, pos, SoundEvents.BLOCK_BREWING_STAND_BREW, SoundCategory.BLOCKS, 1.0F, 1.0F);

                int waterColor = world.getBiome(pos).value().getWaterColor();
                float red = (waterColor >> 16 & 0xFF) / 255.0F;
                float green = (waterColor >> 8 & 0xFF) / 255.0F;
                float blue = (waterColor & 0xFF) / 255.0F;

                world.spawnParticles(
                        new DustColorTransitionParticleEffect(
                                new Vector3f(red, green, blue), // 起始颜色
                                new Vector3f(red, green, blue), // 终止颜色
                                1.0F // 尺寸
                        ),
                        pos.getX() + 0.5,
                        pos.getY() + 1.2,
                        pos.getZ() + 0.5,
                        15, // 粒子数量
                        0.3, 0.1, 0.3, // 散布范围
                        0.02 // 基础速度
                );
            }  else {
                world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_READY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
            world.setBlockState(pos, state.cycle(LEVEL), Block.NOTIFY_ALL);
        }
    }
    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean hasPower = world.isReceivingRedstonePower(pos);
        if (hasPower != state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, hasPower), Block.NOTIFY_ALL);
            
            if (hasPower && !world.isClient) {
                BlockEntity be = world.getBlockEntity(pos.down());
                
                if (!(be instanceof Inventory inventory)) return;

                for (int slot = 0; slot < inventory.size(); slot++) {
                    ItemStack stack = inventory.getStack(slot);
                    if (stack.isEmpty()) continue;

                    Block block = Block.getBlockFromItem(stack.getItem());
                    if (block == null || block.getDefaultState().isAir()) {
                        if (stack.getItem() instanceof BlockItem bi) {
                            block = bi.getBlock();
                        }
                    }
                    
                    if (block == null || block.getDefaultState().isAir()) continue;
                    
                    BlockPos targetPos = pos.up();
                    BlockState targetState = world.getBlockState(targetPos);

                    if (!world.isInBuildLimit(pos.up())) return;
                    if (!isReplaceable(targetState)) {
                        //System.out.println(targetState.getBlock().getName().getString() + " False");
                        break;
                    }

                    if (world.setBlockState(targetPos, block.getDefaultState(), Block.NOTIFY_ALL)) {
                        try {
                            SoundEvent sound = block.getDefaultState().getSoundGroup().getPlaceSound();
                            world.playSound(null, targetPos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        } catch (Exception e) {
                            world.playSound(null, targetPos, SoundEvents.BLOCK_GRASS_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        }
                        inventory.removeStack(slot, 1);
                        break;
                    }
                }
            }
        }
        
        if (sourcePos.equals(pos.down()) && state.get(LEVEL) == 3 && hasFire((ServerWorld)world, pos)) {
            world.scheduleBlockTick(pos, this, 20);
        }
        
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }
    
    // 严格限制可替换类型
    private boolean isReplaceable(BlockState state) {
        return state.isAir() || 
               state.isOf(Blocks.WATER) || 
               state.isOf(Blocks.LAVA);
    }
    
    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        int level = state.get(LEVEL) + 1;
        return level * 3;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LEVEL, LIQUID, POWERED);
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        Item randomSapling = SAPLINGS[world.getRandom().nextInt(SAPLINGS.length)];
        int i = (Integer)state.get(LEVEL);

        if (i == 4 && !state.get(LIQUID) && !state.get(POWERED)) {
            return new CompooperBlock.FullComposterInventory(state, world, pos, new ItemStack(randomSapling));
        } else {
            return (SidedInventory)(i < 3 ? new CompooperBlock.ComposterInventory(state, world, pos) : new CompooperBlock.DummyInventory());
        }
    }
    protected static boolean canFillWithPrecipitation(World world, Biome.Precipitation precipitation) {
        if (precipitation == Biome.Precipitation.RAIN) {
            return world.getRandom().nextFloat() < 0.05F;
        } else {
            return precipitation == Biome.Precipitation.SNOW ? world.getRandom().nextFloat() < 0.1F : false;
        }
    }
    @Override
    public void precipitationTick(BlockState state, World world, BlockPos pos, Biome.Precipitation precipitation) {
        if (canFillWithPrecipitation(world, precipitation) && !state.get(LIQUID)) {
            if (precipitation == Biome.Precipitation.RAIN || precipitation == Biome.Precipitation.SNOW) {
                BlockState newState = state.with(LIQUID, true).with(LEVEL, 4);
                world.setBlockState(pos, newState);
                world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);
            }
        }
    }

    static class ComposterInventory extends SimpleInventory implements SidedInventory {
        private final BlockState state;
        private final WorldAccess world;
        private final BlockPos pos;
        private boolean dirty;

        public ComposterInventory(BlockState state, WorldAccess world, BlockPos pos) {
            super(1);
            this.state = state;
            this.world = world;
            this.pos = pos;
        }

        @Override
        public int getMaxCountPerStack() {
            return 1;
        }

        @Override
        public int[] getAvailableSlots(Direction side) {
            return side == Direction.UP ? new int[]{0} : new int[0];
        }

        @Override
        public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
            return !this.dirty && dir == Direction.UP && CompooperBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.containsKey(stack.getItem());
        }

        @Override
        public boolean canExtract(int slot, ItemStack stack, Direction dir) {
            return false;
        }

        @Override
        public void markDirty() {
            ItemStack itemStack = this.getStack(0);
            if (!itemStack.isEmpty()) {
                this.dirty = true;
                BlockState blockState = CompooperBlock.addToComposter(null, this.state, this.world, this.pos, itemStack);
                this.world.syncWorldEvent(WorldEvents.COMPOSTER_USED, this.pos, blockState != this.state ? 1 : 0);
                this.removeStack(0);
            }
        }
    }

    static class DummyInventory extends SimpleInventory implements SidedInventory {
        public DummyInventory() {
            super(0);
        }

        @Override
        public int[] getAvailableSlots(Direction side) {
            return new int[0];
        }

        @Override
        public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
            return false;
        }

        @Override
        public boolean canExtract(int slot, ItemStack stack, Direction dir) {
            return false;
        }
    }

    static class FullComposterInventory extends SimpleInventory implements SidedInventory {
        private final BlockState state;
        private final WorldAccess world;
        private final BlockPos pos;
        private boolean dirty;

        public FullComposterInventory(BlockState state, WorldAccess world, BlockPos pos, ItemStack outputItem) {
            super(outputItem);
            this.state = state;
            this.world = world;
            this.pos = pos;
        }

        @Override
        public int getMaxCountPerStack() {
            return 1;
        }

        @Override
        public int[] getAvailableSlots(Direction side) {
            return side == Direction.DOWN ? new int[]{0} : new int[0];
        }

        @Override
        public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
            return false;
        }

        @Override
        public boolean canExtract(int slot, ItemStack stack, Direction dir) {
            return !this.dirty && dir == Direction.DOWN && stack.isIn(PSItemTags.COMPOOPER_SAPLINGS);
        }

        @Override
        public void markDirty() {
            CompooperBlock.emptyComposter(null, this.state, this.world, this.pos);
            this.dirty = true;
        }
    }
}
