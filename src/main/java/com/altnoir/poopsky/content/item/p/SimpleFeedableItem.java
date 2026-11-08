package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.item.IFeedable;
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
    public ItemStack finishUsingItem(ItemStack stack, net.minecraft.world.level.Level level, LivingEntity entity) {
        boolean bowl = getFoodProperties(stack, entity) == com.altnoir.poopsky.content.item.PFoods.POOP_SOUP;
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (bowl && (!(entity instanceof Player player) || !player.getAbilities().instabuild)) {
            ItemStack container = new ItemStack(net.minecraft.world.item.Items.BOWL);
            if (result.isEmpty()) return container;
            if (entity instanceof Player player) { if (!player.getInventory().add(container)) player.drop(container, false); }
            else entity.spawnAtLocation(container);
        }
        return result;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        InteractionResult feedResult = tryFeedToPlayer(stack, player, target);
        if (feedResult.consumesAction()) return feedResult;
        return super.interactLivingEntity(stack, player, target, hand);
    }
}