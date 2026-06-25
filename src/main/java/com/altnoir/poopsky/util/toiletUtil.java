package com.altnoir.poopsky.util;

import com.altnoir.poopsky.init.PEffects;
import com.altnoir.poopsky.init.PParticles;
import com.altnoir.poopsky.init.PSoundEvents;
import com.altnoir.poopsky.init.PStats;
import com.altnoir.poopsky.init.PItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.function.LongConsumer;

public class toiletUtil {
    public static void canPoop(Level level, LivingEntity entity, boolean isFire, boolean isGolden, float yOffset, float pitchOffset, long lastPoopTime, LongConsumer poopTimeSetter) {
        boolean hasIncontinence = entity.hasEffect(PEffects.FECAL_INCONTINENCE);

        if (hasIncontinence) {
            onPoop(level, entity, isFire, isGolden, yOffset, pitchOffset);
            if (entity instanceof Player player) {
                player.causeFoodExhaustion(0.05F);
            }
        } else {
            long gameTime = level.getGameTime();
            if (lastPoopTime == 0 || gameTime - lastPoopTime >= 20) {
                onPoop(level, entity, isFire, isGolden, yOffset, pitchOffset);
                if (entity instanceof Player player) {
                    player.causeFoodExhaustion(1.0F);
                }
                poopTimeSetter.accept(gameTime);
            }
        }
    }

    public static void onPoop(Level level, LivingEntity livingEntity, boolean isFire, boolean isGolden, float yOffset, float pitchOffset) {
        boolean shouldPoop = true;

        if (livingEntity instanceof Player player) {
            if (player.getFoodData().getFoodLevel() <= 0) {
                shouldPoop = false;
                player.hurt(level.damageSources().wither(), 1.0F);
                var redStone = new ItemEntity(level, player.getX(), player.getY() + 0.1, player.getZ(), new ItemStack(Items.REDSTONE));
                redStone.setDefaultPickUpDelay();
                level.addFreshEntity(redStone);
            }
            player.awardStat(PStats.POOP_STATS.get());
        }

        if (shouldPoop) {
            Item poopItem;
            if (isFire) {
                poopItem = PItems.CHILI_POOP.get();
            } else if (isGolden) {
                poopItem = PItems.GOLDEN_POOP.get();
            } else {
                poopItem = PItems.POOP.get();
            }
            var poop = new ItemEntity(level, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), new ItemStack(poopItem));
            poop.setDefaultPickUpDelay();
            level.addFreshEntity(poop);
        }

        var pitch = level.random.nextFloat() + pitchOffset;
        level.playSound(null, livingEntity.getX(), livingEntity.getY() + yOffset, livingEntity.getZ(), PSoundEvents.FART.get(), SoundSource.PLAYERS, 1.0F, pitch);
        ((ServerLevel) level).sendParticles(
                PParticles.POOP_PARTICLE.get(),
                livingEntity.getX(),
                livingEntity.getY() + yOffset,
                livingEntity.getZ(),
                8, 0.0, -0.1, 0.0, 3.0
        );
    }
}