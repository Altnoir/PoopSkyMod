package com.altnoir.poopsky.common.item.p;

import com.altnoir.poopsky.common.item.IFeedable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SimpleFeedableItem extends Item implements IFeedable {
    public SimpleFeedableItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        InteractionResult feedResult = tryFeedToPlayer(stack, player, target);
        if (feedResult.consumesAction()) return feedResult;
        return super.interactLivingEntity(stack, player, target, hand);
    }
}