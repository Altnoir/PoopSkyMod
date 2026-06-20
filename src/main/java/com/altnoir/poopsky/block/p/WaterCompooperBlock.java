package com.altnoir.poopsky.block.p;

import com.altnoir.poopsky.block.abs.AbstractCompooperBlock;
import com.altnoir.poopsky.block.PBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class WaterCompooperBlock extends AbstractCompooperBlock {
    public static final MapCodec<WaterCompooperBlock> CODEC = simpleCodec(WaterCompooperBlock::new);

    public WaterCompooperBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 3));
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
        PotionContents potioncontents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

        if (i >= MAX_LEVEL) {
            if (stack.getItem() == Items.BUCKET) {
                return BucketUse(stack, level, pos, player, hand, SoundEvents.BUCKET_FILL, Items.WATER_BUCKET.getDefaultInstance());
            }
        } else if (potioncontents.is(Potions.WATER)) {
            return liquidBottleUse(stack, state, level, pos, player, hand, SoundEvents.BOTTLE_EMPTY);
        }
        if (i > MIN_LEVEL && stack.getItem() == Items.GLASS_BOTTLE) {
            return glassBottleUse(stack, state, level, pos, player, hand, SoundEvents.BOTTLE_FILL, PotionContents.createItemStack(Items.POTION, Potions.WATER));
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}