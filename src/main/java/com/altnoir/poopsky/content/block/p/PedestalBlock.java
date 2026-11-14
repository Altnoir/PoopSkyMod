package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.block.abs.AbstractWaterloggedEntityBlock;
import com.altnoir.poopsky.content.block.entity.PedestalBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PedestalBlock extends AbstractWaterloggedEntityBlock {
    private static final MapCodec<PedestalBlock> CODEC = simpleCodec(PedestalBlock::new);
    private static final VoxelShape SHAPE = Shapes.or(
            box(1, 0, 1, 15, 3, 15),
            box(3, 3, 3, 13, 6, 13),
            box(2, 6, 2, 14, 10, 14)
    );

    public PedestalBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PedestalBlockEntity(pos, state);
    }

    private static void giveBack(Player player, ItemStack held, InteractionHand hand, ItemStack previous) {
        if (!previous.isEmpty() && !player.getAbilities().instabuild && held.getCount() <= 1) {
            player.setItemInHand(hand, previous);
            return;
        }
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }
        if (!previous.isEmpty()) {
            player.getInventory().placeItemBackInInventory(previous);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (player.isShiftKeyDown()
                && (stack.is(Items.WATER_BUCKET) || (state.getValue(WATERLOGGED) && stack.is(Items.BUCKET)))) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.getBlockEntity(pos) instanceof PedestalBlockEntity pedestal) {
            if (!level.isClientSide && !ItemStack.isSameItemSameComponents(pedestal.getItem(), stack)) {
                ItemStack previous = pedestal.removeItem();
                if (pedestal.insertOne(stack)) {
                    giveBack(player, stack, hand, previous);
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.5F, 1.0F);
                } else if (!previous.isEmpty()) {
                    pedestal.insertOne(previous);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof PedestalBlockEntity pedestal && !pedestal.getItem().isEmpty()) {
            if (!level.isClientSide) {
                player.getInventory().placeItemBackInInventory(pedestal.removeItem());
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.5F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof PedestalBlockEntity pedestal) {
            pedestal.dropContents();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

}
