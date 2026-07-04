package com.altnoir.poopsky.common.item.p;

import com.altnoir.poopsky.common.entity.p.FlyEntity;
import com.altnoir.poopsky.common.item.IFeedable;
import com.altnoir.poopsky.common.item.PFlyTypes;
import com.altnoir.poopsky.init.PEffects;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DragonFruitRItem extends Item implements IFeedable {
    public DragonFruitRItem(Properties properties) {
        super(properties);

    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        InteractionResult feedResult = tryFeedToPlayer(stack, player, target);
        if (feedResult.consumesAction()) return feedResult;
        if (target instanceof FlyEntity fly && fly.isAlive()) {
            if (!player.level().isClientSide) {
                ItemStack dragonFlyItem = FlyItem.withType(PFlyTypes.DRAGON_FRUIT.get());
                fly.spawnAtLocation(dragonFlyItem);
                fly.kill();
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        super.finishUsingItem(stack, level, livingEntity);

        if (livingEntity instanceof ServerPlayer player) {
            CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
            player.awardStat(Stats.ITEM_USED.get(this));
        }

        if (!level.isClientSide) {
            if (!livingEntity.hasEffect(PEffects.ON_THE_VERGE)) {
                livingEntity.addEffect(new MobEffectInstance(PEffects.ON_THE_VERGE, 200));
            } else {
                int amplifier = livingEntity.getEffect(PEffects.ON_THE_VERGE).getAmplifier() + 1;
                livingEntity.addEffect(new MobEffectInstance(PEffects.ON_THE_VERGE, 200, amplifier));
            }
            livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200));
        }
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return ItemUtils.startUsingInstantly(level, player, usedHand);
    }
}