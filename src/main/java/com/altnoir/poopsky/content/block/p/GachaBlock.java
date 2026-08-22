package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.block.abs.AbsHorDirBlock;
import com.altnoir.poopsky.content.item.p.GashaponItem;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.impl.util.DispenseUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GachaBlock extends AbsHorDirBlock {
    public static final MapCodec<GachaBlock> CODEC = simpleCodec(GachaBlock::new);
    public static final IntegerProperty STEP = IntegerProperty.create("step", 0, 7);

    public GachaBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(STEP, 0));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public boolean advanceTokenSpin(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(this)) {
            return false;
        }

        int step = state.getValue(STEP);
        if (step == 7) {
            return true;
        }

        level.setBlock(pos, state.setValue(STEP, step + 1), 3);
        level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.5F, 0.8F);
        return step + 1 == 7;
    }

    public void completeTokenSpin(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(this)) {
            return;
        }

        level.setBlock(pos, state.setValue(STEP, 0), 3);
        dispense(level, state, pos, level.getRandom());
    }

    public void resetTokenSpin(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(this) && state.getValue(STEP) != 0) {
            level.setBlock(pos, state.setValue(STEP, 0), 3);
        }
    }

    private void dispense(ServerLevel level, BlockState state, BlockPos pos, RandomSource random) {
        Direction facing = state.getValue(FACING);
        var registry = level.registryAccess().lookupOrThrow(Registries.ENTITY_TYPE);
        var mobs = registry.get(PoTags.EntityTypes.GASHAPON_MOB).orElse(null);
        if (mobs == null) {
            return;
        }

        var holder = mobs.getRandomElement(random).orElse(null);
        if (holder == null) {
            return;
        }

        EntityType<?> entityType = holder.value();
        String color = GashaponItem.COLORS[random.nextInt(GashaponItem.COLORS.length)];
        ItemStack stack = GashaponItem.withColorAndMob(color, registry.getKey(entityType).toString());

        DispenseUtil.spawnItem(level, stack, 0.1, facing, pos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return Objects.requireNonNull(super.getStateForPlacement(context)).setValue(STEP, 0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, STEP);
    }
}
