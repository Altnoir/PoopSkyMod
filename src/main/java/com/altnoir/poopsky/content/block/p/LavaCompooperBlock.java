package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.block.CompooperType;
import com.altnoir.poopsky.content.block.abs.AbstractCompooperBlock;
import com.altnoir.poopsky.impl.sound.PoSoundEvents;
import com.altnoir.poopsky.init.PoBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
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

public class LavaCompooperBlock extends AbstractCompooperBlock {
    public static final MapCodec<LavaCompooperBlock> CODEC = simpleCodec(LavaCompooperBlock::new);

    public LavaCompooperBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 3));
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

        if (i == MAX_LEVEL && stack.getItem() == Items.BUCKET) {
            return BucketUse(stack, level, pos, player, hand, PoSoundEvents.BLOCK_COMPOOPER_BUCKET_FILL_LAVA.get(), Items.LAVA_BUCKET.getDefaultInstance());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (isEntityInsideContent(pos, state, entity)) {
            if (entity instanceof ItemEntity itemEntity) {
                ItemStack stack = itemEntity.getItem();

                // Try recipe-based processing first
                if (processRecipe(CompooperType.LAVA, itemEntity, state, level, pos, SoundEvents.FIREWORK_ROCKET_BLAST)) {
                    return;
                }

                // Special behaviors that are not recipe-based
                if (stack.is(Items.BLAZE_ROD)) {
                    entity.setDeltaMovement(entity.getDeltaMovement().x, entity.getGravity() + 0.1, entity.getDeltaMovement().z);
                } else {
                    entity.lavaHurt();
                }
            } else {
                entity.lavaHurt();
            }
        }
    }
}

