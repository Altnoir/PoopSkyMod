package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.init.PEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ToiletLavaBlock extends AbstractToiletBlock {
    public static final BooleanProperty LAVA = BooleanProperty.create("lava");

    public ToiletLavaBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH)
                .setValue(LAVA, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, CONNECTION, LAVA);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(LAVA, false);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide && entity instanceof Player player && player.isShiftKeyDown() && isEntityCentered(pos, player) && !state.getValue(LAVA)) {
            if (player.hasEffect(PEffects.INTESTINAL_SPASM)) {
                level.setBlock(pos, state.setValue(LAVA, true), 3);
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.removeEffect(PEffects.INTESTINAL_SPASM);
                player.causeFoodExhaustion(1.0F);
            } else if (player.hasEffect(PEffects.FECAL_INCONTINENCE)) {
                onPoop(level, player, true);
                player.causeFoodExhaustion(0.05F);
            } else if (level.getGameTime() % 20 == 0) {
                onPoop(level, player, false);
                player.causeFoodExhaustion(1.0F);
            }
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(LAVA)) {
            if (stack.is(Items.BUCKET)) {
                level.playSound(null, pos, SoundEvents.BUCKET_FILL_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.setBlock(pos, state.setValue(LAVA, false), 3);

                ItemStack itemStack = ItemUtils.createFilledResult(stack, player, new ItemStack(Items.LAVA_BUCKET));
                player.setItemInHand(hand, itemStack);

                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
