package com.altnoir.poopsky.content.item.p;

import com.altnoir.poopsky.content.item.IFeedable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class SimpleFeedableItem extends Item implements IFeedable {
    private final Supplier<Holder<MobEffect>> foodEffect;
    private final int effectDuration;
    private final int effectAmplifier;

    public SimpleFeedableItem(Properties properties) {
        this(properties, null, 0, 0);
    }

    public SimpleFeedableItem(Properties properties, Supplier<Holder<MobEffect>> foodEffect,
                              int effectDuration, int effectAmplifier) {
        super(properties);
        this.foodEffect = foodEffect;
        this.effectDuration = effectDuration;
        this.effectAmplifier = effectAmplifier;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, net.minecraft.world.level.Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide && foodEffect != null) {
            entity.addEffect(new MobEffectInstance(foodEffect.get(), effectDuration, effectAmplifier));
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
