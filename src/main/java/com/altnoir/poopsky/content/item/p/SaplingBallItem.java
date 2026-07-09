package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.item.IFeedable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SaplingBallItem extends Item implements IFeedable {
    public SaplingBallItem(Properties properties) {
        super(properties);
    }

    public static final Item[] SAPLINGS = new Item[]{
            Items.OAK_SAPLING,
            Items.SPRUCE_SAPLING,
            Items.BIRCH_SAPLING,
            Items.JUNGLE_SAPLING,
            Items.ACACIA_SAPLING,
            Items.DARK_OAK_SAPLING,
            Items.MANGROVE_PROPAGULE,
            Items.CHERRY_SAPLING
    };

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        super.finishUsingItem(stack, level, livingEntity);
        if (livingEntity instanceof ServerPlayer serverplayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, stack);
            serverplayer.awardStat(Stats.ITEM_USED.get(this));
        }

        if (stack.isEmpty()) {
            return new ItemStack(randomProduce());
        } else {
            if (livingEntity instanceof Player player) {
                player.playSound(SoundEvents.CHICKEN_EGG);
                ItemStack itemstack = new ItemStack(randomProduce());
                player.drop(itemstack, false);
            }
            return stack;
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 80;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        InteractionResult feedResult = tryFeedToPlayer(stack, player, target);
        if (feedResult.consumesAction()) return feedResult;
        return super.interactLivingEntity(stack, player, target, hand);
    }

    private static Item randomProduce() {
        var random = RandomSource.create();
        return SAPLINGS[random.nextInt(SAPLINGS.length)];
    }
}