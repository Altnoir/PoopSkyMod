package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.item.IFeedable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class FeedableBlockItem extends ItemNameBlockItem implements IFeedable {
    public FeedableBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        InteractionResult feedResult = tryFeedToPlayer(stack, player, target);
        if (feedResult.consumesAction()) return feedResult;
        return super.interactLivingEntity(stack, player, target, hand);
    }
}