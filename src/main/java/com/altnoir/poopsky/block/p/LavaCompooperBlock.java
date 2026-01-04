package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.AbstractCompooperBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class LavaCompooperBlock extends AbstractCompooperBlock {
    public static final MapCodec<LavaCompooperBlock> CODEC = simpleCodec(LavaCompooperBlock::new);

    public LavaCompooperBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(LEVEL, 3)
                        .setValue(POWERED, false));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int i = state.getValue(LEVEL);

        if (i == MAX_LEVEL && stack.getItem() == Items.BUCKET) {
            return BucketUse(stack, level, pos, player, hand, SoundEvents.BUCKET_FILL, Items.LAVA_BUCKET.getDefaultInstance());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
