package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.item.p.GashaponItem;
import com.altnoir.poopsky.impl.PoTags;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class GachaBlock extends Block {
    public static final MapCodec<GachaBlock> CODEC = simpleCodec(GachaBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public GachaBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        Direction facing = state.getValue(FACING);
        var registry = level.registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
        var mobs = registry.getTag(PoTags.EntityTypes.GASHAPON_MOB).orElse(null);
        if (mobs == null) {
            return InteractionResult.PASS;
        }

        var holder = mobs.getRandomElement(level.random).orElse(null);
        if (holder == null) {
            return InteractionResult.PASS;
        }

        EntityType<?> entityType = holder.value();
        String color = GashaponItem.COLORS[level.random.nextInt(GashaponItem.COLORS.length)];
        ItemStack stack = GashaponItem.withColorAndMob(color, registry.getKey(entityType).toString());

        Vec3 spawnPos = Vec3.atBottomCenterOf(pos).add(facing.getStepX() * 0.7, 0.2, facing.getStepZ() * 0.7);
        ItemEntity item = new ItemEntity(level, spawnPos.x(), spawnPos.y(), spawnPos.z(), stack);
        item.setDeltaMovement(facing.getStepX() * 0.5, 0.2, facing.getStepZ() * 0.5);
        item.setDefaultPickUpDelay();
        level.addFreshEntity(item);
        level.playSound(null, pos, SoundEvents.DISPENSER_LAUNCH, SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.CONSUME;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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