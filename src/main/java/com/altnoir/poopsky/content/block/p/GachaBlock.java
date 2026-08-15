package com.altnoir.poopsky.content.block.p;

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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class GachaBlock extends Block {
    public static final MapCodec<GachaBlock> CODEC = simpleCodec(GachaBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
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

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!stack.is(Items.EMERALD)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            if (state.getValue(STEP) != 0) {
                if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
                    level.scheduleTick(pos, this, 2);
                }
                return ItemInteractionResult.sidedSuccess(false);
            }
            stack.consume(1, player);
            level.playSound(null, pos, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.5F, 0.8F);
            level.scheduleTick(pos, this, 2);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int step = state.getValue(STEP);
        if (step == 7) {
            level.setBlock(pos, state.setValue(STEP, 0), 3);
            dispense(level, state, pos, random);
            return;
        }

        level.setBlock(pos, state.setValue(STEP, step + 1), 3);
        level.scheduleTick(pos, this, 2);
    }

    private void dispense(ServerLevel level, BlockState state, BlockPos pos, RandomSource random) {
        Direction facing = state.getValue(FACING);
        var registry = level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
        var mobs = registry.getTag(PoTags.EntityTypes.GASHAPON_MOB).orElse(null);
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
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(STEP, 0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, STEP);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }
}