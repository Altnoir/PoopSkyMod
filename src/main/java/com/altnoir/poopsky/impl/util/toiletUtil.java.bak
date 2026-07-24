package com.altnoir.poopsky.impl.util;

import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.entity.FlushToiletBlockEntity;
import com.altnoir.poopsky.content.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.content.block.p.BaseToiletLavaBlock;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.impl.sound.PoSoundEvents;
import com.altnoir.poopsky.init.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
import net.neoforged.neoforge.items.ItemStackHandler;

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
                level.playSound(null, pos, PoSoundEvents.BLOCK_TOILET_LAVA_EMPTY.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
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
            Item poopItem = getPoopItem(isFire, isGolden);
            var poop = new ItemEntity(level, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), new ItemStack(poopItem));
            poop.setDefaultPickUpDelay();
            if (livingEntity.hasEffect(PoEffects.FECAL_INCONTINENCE)) {
                Vec3 backward = getBackwardDirection(livingEntity);
                poop.setDeltaMovement(backward.x * 0.5F, 0.2F, backward.z * 0.5F);
            }
            level.addFreshEntity(poop);
        }

        playPoopEffects(level, livingEntity.getX(), livingEntity.getY() + yOffset, livingEntity.getZ(), level.random.nextFloat() + pitchOffset);
    }

    public static void containerPoop(Level level, BlockPos pos, LivingEntity entity, boolean hasSpasm, boolean isGolden, long lastPoopTime, LongConsumer poopTimeSetter) {
        boolean hasIncontinence = entity.hasEffect(PoEffects.FECAL_INCONTINENCE);
        long gameTime = level.getGameTime();

        if (!hasIncontinence) {
            if (lastPoopTime != 0 && gameTime - lastPoopTime < 20) return;
        }

        if (entity instanceof Player player) {
            if (player.getFoodData().getFoodLevel() <= 0) {
                player.hurt(level.damageSources().wither(), 1.0F);
                insertOrReplaceContainer(level, pos, Items.REDSTONE.getDefaultInstance());
                poopTimeSetter.accept(gameTime);
                return;
            }
            player.awardStat(PoStats.POOP_STATS.get());
            player.causeFoodExhaustion(hasIncontinence ? 0.2F : 4.0F);
        }

        Item poopItem;
        if (isGolden) {
            poopItem = PoBlocks.GOLDEN_SHIT.get().asItem();
        } else if (hasSpasm) {
            poopItem = PoBlocks.CHILI_SHIT.get().asItem();
        } else {
            poopItem = PoBlocks.SHIT.get().asItem();
        }
        if (!insertOrReplaceContainer(level, pos, new ItemStack(poopItem))) return;

        poopTimeSetter.accept(gameTime);

        float yOffset = entity instanceof Player ? 0.55F : 0.05F;
        playPoopEffects(level, entity.getX(), entity.getY() + yOffset, entity.getZ(), level.random.nextFloat() + 0.5F);
    }

    public static boolean insertOrReplaceContainer(Level level, BlockPos pos, ItemStack stack) {
        if (!(level.getBlockEntity(pos) instanceof FlushToiletBlockEntity be)) return false;
        ItemStackHandler handler = be.getItemHandler();
        ItemStack current = handler.getStackInSlot(0);
        if (current.isEmpty()) {
            handler.setStackInSlot(0, stack.copy());
            return true;
        }
        if (current.is(stack.getItem()) && ItemStack.isSameItemSameComponents(current, stack)) {
            int maxStack = current.getMaxStackSize();
            int space = maxStack - current.getCount();
            if (space >= stack.getCount()) {
                current.grow(stack.getCount());
                handler.setStackInSlot(0, current);
                return true;
            }
        }
        if (!current.is(PoTags.Items.POOPS)) {
            handler.setStackInSlot(0, stack.copy());
            return true;
        }
        return false;
    }

    private static void playPoopEffects(Level level, double x, double y, double z, float pitch) {
        level.playSound(null, x, y, z, PoSoundEvents.FART.get(), SoundSource.PLAYERS, 1.0F, pitch);
        ((ServerLevel) level).sendParticles(
                PoParticles.POOP_PARTICLE.get(),
                x, y, z,
                8, 0.0, -0.1, 0.0, 3.0
        );
    }

    private static Item getPoopItem(boolean isFire, boolean isGolden) {
        if (isFire) return PoItems.CHILI_POOP.get();
        if (isGolden) return PoItems.GOLDEN_POOP.get();
        return PoItems.POOP.get();
    }
}