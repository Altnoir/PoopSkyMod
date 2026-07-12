package com.altnoir.poopsky.content.block.p;

import com.altnoir.poopsky.content.block.abs.AbstractCompooperBlock;
import com.altnoir.poopsky.content.item.PFlyTypes;
import com.altnoir.poopsky.content.item.p.FlyItem;
import com.altnoir.poopsky.init.PoBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
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
        return new ItemStack(PoBlocks.COMPOOPER.get());
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

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !this.isEntityInsideContent(pos, state, entity)) {
            return;
        }

        if (entity.isOnFire()) {
            entity.clearFire();
            if (entity.mayInteract(level, pos)) {
                lowerFillLevel(state, level, pos);
            }
            return;
        }

        if (entity instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getItem();
            if (FlyItem.isFlyItem(stack) && FlyItem.getFlyType(stack).equals(PFlyTypes.NORMAL.get())) {
                int count = stack.getCount();

                catalyst(itemEntity, state, level, pos, count, FlyItem.withType(PFlyTypes.BLUE.get()), FlyItem.withType(PFlyTypes.NORMAL.get()));
                level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }
}
