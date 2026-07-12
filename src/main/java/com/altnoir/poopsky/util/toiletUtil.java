package com.altnoir.poopsky.util;

import com.altnoir.poopsky.content.block.ToiletType;
import com.altnoir.poopsky.content.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.content.block.p.BaseToiletLavaBlock;
import com.altnoir.poopsky.init.PoEffects;
import com.altnoir.poopsky.init.PoParticles;
import com.altnoir.poopsky.init.PoSoundEvents;
import com.altnoir.poopsky.init.PoStats;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.LongConsumer;

public class toiletUtil {
    public static boolean isGoldenToilet(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ToiletBlockEntity be) {
            ToiletType type = be.getToiletType();
            return type != null && type.isGolden();
        }
        return false;
    }

    public static Vec3 getBackwardDirection(LivingEntity entity) {
        float yaw = entity.getYRot() * ((float) Math.PI / 180F);
        return new Vec3(Math.sin(yaw), 0, -Math.cos(yaw));
    }

    public static boolean isEntityCentered(BlockPos blockPos, Entity entity) {
        return new AABB(blockPos).inflate(0.2).contains(entity.position());
    }

    public static void lavaToiletStepOn(Level level, BlockPos pos, BlockState state, Entity entity, boolean isGolden) {
        if (!level.isClientSide && entity instanceof Player player && player.isShiftKeyDown() && isEntityCentered(pos, player) && !state.getValue(BaseToiletLavaBlock.LAVA)) {
            if (player.hasEffect(PoEffects.INTESTINAL_SPASM)) {
                level.setBlock(pos, state.setValue(BaseToiletLavaBlock.LAVA, true), 3);
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.PLAYERS, 1.0F, 1.0F);
                player.removeEffect(PoEffects.INTESTINAL_SPASM);
                player.causeFoodExhaustion(1.0F);
            } else {
                boolean hasIncontinence = player.hasEffect(PoEffects.FECAL_INCONTINENCE);
                boolean isFire = hasIncontinence && !isGolden;
                float pitchOffset = isGolden ? -0.5F : 0.5F;
                var playerData = player.getPersistentData();
                long lastPoopTime = playerData.getLong("poopTime");
                canPoop(level, player, isFire, isGolden, 0.1F, pitchOffset, lastPoopTime,
                        time -> playerData.putLong("poopTime", time));
            }
        }
    }

    public static void canPoop(Level level, LivingEntity entity, boolean isFire, boolean isGolden, float yOffset, float pitchOffset, long lastPoopTime, LongConsumer poopTimeSetter) {
        boolean hasIncontinence = entity.hasEffect(PoEffects.FECAL_INCONTINENCE);

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
            player.awardStat(PoStats.POOP_STATS.get());
        }

        if (shouldPoop) {
            Item poopItem;
            if (isFire) {
                poopItem = PoItems.CHILI_POOP.get();
            } else if (isGolden) {
                poopItem = PoItems.GOLDEN_POOP.get();
            } else {
                poopItem = PoItems.POOP.get();
            }
            var poop = new ItemEntity(level, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), new ItemStack(poopItem));
            poop.setDefaultPickUpDelay();
            if (livingEntity.hasEffect(PoEffects.FECAL_INCONTINENCE)) {
                Vec3 backward = getBackwardDirection(livingEntity);
                poop.setDeltaMovement(backward.x * 0.5F, 0.2F, backward.z * 0.5F);
            }
            level.addFreshEntity(poop);
        }

        var pitch = level.random.nextFloat() + pitchOffset;
        level.playSound(null, livingEntity.getX(), livingEntity.getY() + yOffset, livingEntity.getZ(), PoSoundEvents.FART.get(), SoundSource.PLAYERS, 1.0F, pitch);
        ((ServerLevel) level).sendParticles(
                PoParticles.POOP_PARTICLE.get(),
                livingEntity.getX(),
                livingEntity.getY() + yOffset,
                livingEntity.getZ(),
                8, 0.0, -0.1, 0.0, 3.0
        );
    }
}