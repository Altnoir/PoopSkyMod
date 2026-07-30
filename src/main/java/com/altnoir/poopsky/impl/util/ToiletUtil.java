package com.altnoir.poopsky.impl.util;

import com.altnoir.poopsky.content.ToiletType;
import com.altnoir.poopsky.content.block.abs.AbstractToiletBlock;
import com.altnoir.poopsky.content.block.entity.FlushToiletBlockEntity;
import com.altnoir.poopsky.content.block.entity.ToiletBlockEntity;
import com.altnoir.poopsky.content.block.p.BaseToiletLavaBlock;
import com.altnoir.poopsky.content.block.p.FlushToiletBlock;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.impl.sound.PoSoundEvents;
import com.altnoir.poopsky.init.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.Set;
import java.util.function.LongConsumer;

public class ToiletUtil {
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


    public static boolean hasLinkedTarget(BlockEntity blockEntity) {
        if (blockEntity instanceof ToiletBlockEntity be) {
            return be.getLinkedPos() != null && be.getLinkedDim() != null && !be.getLinkedDim().isBlank();
        } else if (blockEntity instanceof FlushToiletBlockEntity be) {
            return be.getLinkedPos() != null && be.getLinkedDim() != null && !be.getLinkedDim().isBlank();
        }
        return false;
    }

    public static boolean tryTeleportFromFall(Level level, BlockPos pos, Entity entity, float fallDistance) {
        if (fallDistance < 1.0F || !isEntityInToiletPit(level, pos, entity)) {
            return false;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null && hasLinkedTarget(blockEntity)) {
            teleportEntity(level, entity, blockEntity, fallDistance);
            return true;
        }
        return false;
    }

    private static boolean isEntityInToiletPit(Level level, BlockPos pos, Entity entity) {
        BlockState state = level.getBlockState(pos);
        double offsetX = entity.getX() - (pos.getX() + 0.5);
        double offsetZ = entity.getZ() - (pos.getZ() + 0.5);

        if (state.getBlock() instanceof FlushToiletBlock) {
            if (state.getValue(FlushToiletBlock.CLOSED)) {
                return false;
            }

            Direction facing = state.getValue(FlushToiletBlock.FACING);
            double forward = offsetX * facing.getStepX() + offsetZ * facing.getStepZ();
            double sideways = Math.abs(offsetX * facing.getStepZ() - offsetZ * facing.getStepX());
            return sideways <= 2.0 / 16.0 && forward >= -1.0 / 16.0 && forward <= 4.0 / 16.0;
        }

        if (state.getBlock() instanceof AbstractToiletBlock) {
            Direction facing = state.getValue(AbstractToiletBlock.FACING);
            double forward = offsetX * facing.getStepX() + offsetZ * facing.getStepZ();
            double sideways = Math.abs(offsetX * facing.getStepZ() - offsetZ * facing.getStepX());
            return sideways <= 3.0 / 16.0 && Math.abs(forward) <= 7.0 / 16.0;
        }

        return false;
    }

    public static void teleportEntity(Level level, Entity entity, BlockEntity blockEntity, float fallDistance) {
        // Get linked info from block entity
        String linkedDim = null;
        BlockPos linkedPos = null;
        if (blockEntity instanceof ToiletBlockEntity be) {
            linkedDim = be.getLinkedDim();
            linkedPos = be.getLinkedPos();
        } else if (blockEntity instanceof FlushToiletBlockEntity be) {
            linkedDim = be.getLinkedDim();
            linkedPos = be.getLinkedPos();
        }
        if (linkedDim == null || linkedPos == null) return;
        
        var server = level.getServer();
        if (server == null) return;
        
        var targetDimension = ResourceLocation.tryParse(linkedDim);
        if (targetDimension == null) return;
        
        var targetWorld = server.getLevel(ResourceKey.create(Registries.DIMENSION, targetDimension));
        if (targetWorld == null) return;
        
        targetWorld.getChunk(linkedPos);
        Vec3 destination = getToiletPitDestination(targetWorld, linkedPos);
        
        if (entity.isVehicle() && entity.getControllingPassenger() != null) {
            entity.getControllingPassenger().teleportTo(targetWorld, destination.x, destination.y, destination.z, Set.of(), entity.getYRot(), entity.getXRot());
        }
        entity.teleportTo(targetWorld, destination.x, destination.y, destination.z, Set.of(), entity.getYRot(), entity.getXRot());
        
        var pitch = targetWorld.random.nextFloat() + 0.1F;
        targetWorld.playSound(null, destination.x, destination.y, destination.z, SoundEvents.MUD_BREAK, SoundSource.PLAYERS, 1.0F, pitch);
        
        var bounce = Math.sqrt(2 * 0.08 * fallDistance) * 0.85;
        server.tell(new TickTask(server.getTickCount() + 1, () -> {
            entity.setDeltaMovement(entity.getDeltaMovement().x, bounce, entity.getDeltaMovement().z);
            entity.hurtMarked = true;
            entity.hasImpulse = true;
        }));
    }

    private static Vec3 getToiletPitDestination(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof FlushToiletBlock) {
            Direction facing = state.getValue(FlushToiletBlock.FACING);
            return Vec3.atBottomCenterOf(pos).add(
                    facing.getStepX() * 3.0 / 32.0,
                    0.5,
                    facing.getStepZ() * 3.0 / 32.0
            );
        }
        state.getBlock();
        return Vec3.atBottomCenterOf(pos).add(0.0, 1.0, 0.0);
    }
}
