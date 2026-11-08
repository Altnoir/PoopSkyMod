package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.block.CompooperType;
import com.altnoir.poopsky.content.block.abs.AbstractCompooperBlock;
import com.altnoir.poopsky.init.PoSoundEvents;
import com.altnoir.poopsky.init.PoBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class LavaCompooperBlock extends AbstractCompooperBlock implements com.altnoir.poopsky.content.block.PoBlockInteraction {

    public LavaCompooperBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 3));
    }


    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, net.minecraft.world.level.BlockGetter level, BlockPos pos, Player player) {
        return new ItemStack(PoBlocks.COMPOOPER.get());
    }

    @Override
    public InteractionResult poopsky$useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int i = state.getValue(LEVEL);

        if (i == MAX_LEVEL && stack.getItem() == Items.BUCKET) {
            return BucketUse(stack, level, pos, player, hand, PoSoundEvents.BLOCK_COMPOOPER_BUCKET_FILL_LAVA.get(), Items.LAVA_BUCKET.getDefaultInstance());
        }
        return InteractionResult.PASS;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (isEntityInsideContent(pos, state, entity)) {
            if (entity instanceof ItemEntity itemEntity) {
                ItemStack stack = itemEntity.getItem();

                // Try recipe-based processing first
                if (processRecipe(CompooperType.LAVA, itemEntity, state, level, pos, SoundEvents.FIREWORK_ROCKET_BLAST)) {
                    return;
                }

                // Special behaviors that are not recipe-based
                if (stack.is(Items.BLAZE_ROD)) {
                    entity.setDeltaMovement(entity.getDeltaMovement().x, (entity.isNoGravity() ? 0.0D : 0.04D) + 0.1, entity.getDeltaMovement().z);
                } else {
                    entity.lavaHurt();
                }
            } else {
                entity.lavaHurt();
            }
        }
    }

    @Override
    public net.minecraft.world.InteractionResult use(net.minecraft.world.level.block.state.BlockState state,
            net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
            net.minecraft.world.entity.player.Player player, net.minecraft.world.InteractionHand hand,
            net.minecraft.world.phys.BlockHitResult hit) {
        return poopsky$interact(state, level, pos, player, hand, hit);
    }
}
