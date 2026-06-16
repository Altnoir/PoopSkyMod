package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.abs.AbstractCompooperBlock;
import com.altnoir.poopsky.block.PBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

public class PowderSnowCompooperBlock extends AbstractCompooperBlock {
    public static final MapCodec<PowderSnowCompooperBlock> CODEC = simpleCodec(PowderSnowCompooperBlock::new);

    public PowderSnowCompooperBlock(Properties properties) {
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
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return new ItemStack(PBlocks.COMPOOPER.get());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        int i = state.getValue(LEVEL);

        if (i == MAX_LEVEL && stack.getItem() == Items.BUCKET) {
            return BucketUse(stack, level, pos, player, hand, SoundEvents.BUCKET_FILL_POWDER_SNOW, Items.POWDER_SNOW_BUCKET.getDefaultInstance());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (this.isEntityInsideContent(pos, state, entity)) {
            if (entity instanceof ItemEntity itemEntity) {
                ItemStack stack = itemEntity.getItem();
                if (stack.is(Items.STICK)) {
                    int count = stack.getCount();

                    catalyst(itemEntity, state, level, pos, count,Items.BREEZE_ROD);
                    level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.BLOCKS, 1.0F, 1.2F);
                } else if (stack.is(Items.BREEZE_ROD)) {
                    entity.setDeltaMovement(entity.getDeltaMovement().x, entity.getGravity() + 0.1, entity.getDeltaMovement().z);
                }
            }
        }
    }
}
