package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.block.abs.AbstractCompooperBlock;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoItems;
import com.altnoir.poopsky.init.PoParticles;
import com.altnoir.poopsky.impl.sound.PoSoundEvents;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class UrineCompooperBlock extends AbstractCompooperBlock implements WorldlyContainerHolder {
    public static final MapCodec<UrineCompooperBlock> CODEC = simpleCodec(UrineCompooperBlock::new);
    public static final BooleanProperty MAGGOTS = BooleanProperty.create("maggots");

    public UrineCompooperBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(LEVEL, 3)
                        .setValue(MAGGOTS, false));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return new ItemStack(PoBlocks.COMPOOPER.get());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int i = state.getValue(LEVEL);

        Item bottle = PoItems.URINE_BOTTLE.get();
        Item bucket = PoItems.URINE_BUCKET.get();

        if (i >= MAX_LEVEL) {
            if (stack.getItem() == Items.BUCKET) {
                return BucketUse(stack, level, pos, player, hand, SoundEvents.BUCKET_FILL, 0.6F, bucket.getDefaultInstance());
            }
        } else if (stack.getItem() == bottle) {
            return liquidBottleUse(stack, state, level, pos, player, hand, SoundEvents.BOTTLE_EMPTY, 0.6F);
        }
        if (i > MIN_LEVEL && stack.getItem() == Items.GLASS_BOTTLE) {
            return glassBottleUse(stack, state, level, pos, player, hand, SoundEvents.BOTTLE_FILL, 0.6F, bottle.getDefaultInstance());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(MAGGOTS)) {
            extractProduce(player, state, level, pos);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !this.isEntityInsideContent(pos, state, entity)) {
            return;
        }

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 200));
        }
    }

    public static void extractProduce(Entity entity, BlockState state, Level level, BlockPos pos) {
        if (!level.isClientSide) {
            var vec3 = Vec3.atLowerCornerWithOffset(pos, 0.5, 1.01, 0.5).offsetRandom(level.random, 0.7F);
            var itementity = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), new ItemStack(PoItems.MAGGOTS_SEEDS.get()));
            itementity.setDefaultPickUpDelay();
            level.addFreshEntity(itementity);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        PoParticles.POOP_PARTICLE.get(),
                        pos.getX() + 0.5,
                        pos.getY() + 0.9375,
                        pos.getZ() + 0.5,
                        8,
                        0.5,
                        0.2,
                        0.5,
                        0.1
                );
            }
        }

        empty(entity, state, level, pos);
        level.playSound(null, pos, SoundEvents.PLAYER_SPLASH, SoundSource.BLOCKS, 0.5F, 1.0F);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!level.isClientSide && state.getValue(LEVEL) == MAX_LEVEL && isHot((ServerLevel) level, pos)) {
            level.scheduleTick(pos, this, 80);
        }
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!level.isClientSide && neighborPos.equals(pos.below()) && state.getValue(LEVEL) == MAX_LEVEL && isHot((ServerLevel) level, pos)) {
            level.scheduleTick(pos, this, 80);
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(LEVEL) != MAX_LEVEL || !isHot(level, pos)) {
            return;
        }

        level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);

        var waterColor = level.getBiome(pos).value().getWaterColor();
        var red = (waterColor >> 16 & 0xFF) / 255.0F;
        var green = (waterColor >> 8 & 0xFF) / 255.0F;
        var blue = (waterColor & 0xFF) / 255.0F;

        var color = new Vector3f(red, green, blue);

        level.sendParticles(
                new DustColorTransitionOptions(color, color, 1.0F),
                pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5,
                15,
                0.3, 0.1, 0.3,
                0.02
        );

        BlockState compooperBlock = PoBlocks.WATER_COMPOOPER.get().defaultBlockState().setValue(LEVEL, MAX_LEVEL);
        level.setBlockAndUpdate(pos, compooperBlock);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL, MAGGOTS);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (!state.is(newState.getBlock())) {
            level.invalidateCapabilities(pos);
        }
    }

    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        return blockState.getValue(MAGGOTS) ? 1 : 0;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(MAGGOTS) && state.getValue(LEVEL) == MAX_LEVEL) {
            level.setBlockAndUpdate(pos, state.setValue(MAGGOTS, true));
            level.playSound(null, pos, PoSoundEvents.BLOCK_COMPOOPER_MAGGOTS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        super.randomTick(state, level, pos, random);
    }

    @Override
    public WorldlyContainer getContainer(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos) {
        if (blockState.getValue(MAGGOTS)) {
            return new UrineCompooperBlock.OutputContainer(blockState, levelAccessor, blockPos, new ItemStack(PoItems.MAGGOTS_SEEDS.get()));
        } else {
            return new UrineCompooperBlock.EmptyContainer();
        }
    }

    static class EmptyContainer extends SimpleContainer implements WorldlyContainer {
        public EmptyContainer() {
            super(0);
        }

        public int[] getSlotsForFace(Direction side) {
            return new int[0];
        }

        public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
            return false;
        }

        public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
            return false;
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
            return !this.changed && direction == Direction.DOWN && stack.is(PoItems.MAGGOTS_SEEDS.get());
        }

        @Override
        public void setChanged() {
            UrineCompooperBlock.empty(null, this.state, this.level, this.pos);
            this.changed = true;
        }
    }

    protected static void empty(@Nullable Entity entity, BlockState state, LevelAccessor level, BlockPos pos) {
        var blockstate = state.setValue(MAGGOTS, false);
        level.setBlock(pos, blockstate, 3);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
    }
}
