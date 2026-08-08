package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.item.p.JinKeLaItem;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.init.PoBlocks;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class PoopFarmlandBlock extends FarmBlock {
    protected static final VoxelShape COLLISION_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    public enum FarmMode implements StringRepresentable {
        DEFAULT("default"),
        ENRICHED("enriched"),
        LEAK("leak"),
        ENRICHED_LEAK("enriched_leak");

        private final String name;

        FarmMode(String name) {
            this.name = name;
        }

        public boolean isEnriched() {
            return this == ENRICHED || this == ENRICHED_LEAK;
        }

        public boolean isLeak() {
            return this == LEAK || this == ENRICHED_LEAK;
        }

        public FarmMode withEnriched(boolean enriched) {
            if (enriched) {
                return this == LEAK ? ENRICHED_LEAK : ENRICHED;
            } else {
                return this == ENRICHED_LEAK ? LEAK : DEFAULT;
            }
        }

        public FarmMode withLeak(boolean leak) {
            if (leak) {
                return this == ENRICHED ? ENRICHED_LEAK : LEAK;
            } else {
                return this == ENRICHED_LEAK ? ENRICHED : DEFAULT;
            }
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    public static final EnumProperty<FarmMode> MODE = EnumProperty.create("mode", FarmMode.class);

    public PoopFarmlandBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(MOISTURE, MAX_MOISTURE)
                .setValue(MODE, FarmMode.DEFAULT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MODE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
                ? PoBlocks.POOP_BLOCK.get().defaultBlockState()
                : super.getStateForPlacement(context);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            level.setBlockAndUpdate(pos, PoBlocks.POOP_BLOCK.get().defaultBlockState());
            return;
        }
        if (state.getValue(MODE).isLeak()) {
            cropDrop(level, pos);
        }
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (!level.isClientSide() && !level.getBlockTicks().hasScheduledTick(currentPos, this)) {
            level.scheduleTick(currentPos, this, 4);
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(MOISTURE) < MAX_MOISTURE) {
            level.setBlock(pos, state.setValue(MOISTURE, MAX_MOISTURE), 2);
        }
        FarmMode mode = state.getValue(MODE);
        if (mode.isEnriched()) {
            tryEnrichedGrow(level, pos);
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.causeFallDamage(fallDistance, 1.0F, entity.damageSources().fall());
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility == ItemAbilities.SHOVEL_FLATTEN) {
            FarmMode mode = state.getValue(MODE);
            if (mode.isLeak()) {
                return null;
            }
            return state.setValue(MODE, mode.withLeak(true));
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

    private static void tryEnrichedGrow(ServerLevel level, BlockPos farmlandPos) {
        BlockPos cropPos = farmlandPos.above();
        BlockState cropState = level.getBlockState(cropPos);
        Property<?> property = cropState.getBlock().getStateDefinition().getProperty("age");
        if (property instanceof IntegerProperty age) {
            int maxAge = JinKeLaItem.getMaxAge(age);
            if (cropState.getValue(age) < maxAge) {
                JinKeLaItem.triggerRandomTick(level, cropPos, cropState);
                level.levelEvent(1505, cropPos, 15);
                level.playSound(null, cropPos, PoSoundEvents.ITEM_JINKELA_USE.get(), SoundSource.BLOCKS);
            }
        }
    }

    private static void cropDrop(ServerLevel level, BlockPos pos) {
        BlockPos abovePos = pos.above();
        BlockPos belowPos = pos.below();
        BlockState aboveState = level.getBlockState(abovePos);
        BlockState belowState = level.getBlockState(belowPos);

        IntegerProperty ageProp = (IntegerProperty) aboveState.getBlock().getStateDefinition().getProperty("age");
        if (ageProp == null) return;

        int age = aboveState.getValue(ageProp);
        Collection<Integer> possibleValues = ageProp.getPossibleValues();
        if (possibleValues.isEmpty()) return;
        int minAge = possibleValues.iterator().next();
        int maxAge = minAge == 0 ? possibleValues.size() - 1 : possibleValues.size();

        if (!notFarmland(aboveState) && !belowState.isCollisionShapeFullBlock(level, belowPos) && age == maxAge) {
            BlockState topState = level.getBlockState(abovePos.above());
            IntegerProperty topAgeProp = (IntegerProperty) topState.getBlock().getStateDefinition().getProperty("age");
            if (topAgeProp != null && !topState.is(PoTags.Blocks.POOP_FARMLAND_AUTO)) {
                return;
            }

            BlockState newState;
            if (aboveState.getBlock() instanceof SweetBerryBushBlock) {
                newState = aboveState.setValue(ageProp, minAge + 1);
            } else {
                newState = aboveState.setValue(ageProp, minAge);
            }
            level.setBlock(abovePos, newState, 2);

            Block.getDrops(aboveState, level, abovePos, null, null, ItemStack.EMPTY)
                    .forEach(stack -> {
                        ItemEntity itemEntity = new ItemEntity(level, belowPos.getX() + 0.5, belowPos.getY() + 0.5, belowPos.getZ() + 0.5, stack);
                        itemEntity.setDeltaMovement(0, 0, 0);
                        level.addFreshEntity(itemEntity);
                    });

            level.playSound(null, abovePos, PoSoundEvents.ITEM_JINKELA_USE.get(), SoundSource.BLOCKS);
        }
    }

    private static boolean notFarmland(BlockState aboveState) {
        return aboveState.getBlock() instanceof TorchflowerCropBlock
                || aboveState.getBlock() instanceof PitcherCropBlock
                || aboveState.getBlock() instanceof StemBlock;
    }
}