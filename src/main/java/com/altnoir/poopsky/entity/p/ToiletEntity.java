package com.altnoir.poopsky.entity.p;

import com.altnoir.poopsky.init.PEffects;
import com.altnoir.poopsky.init.PParticles;
import com.altnoir.poopsky.init.PSoundEvents;
import com.altnoir.poopsky.init.PStats;
import com.altnoir.poopsky.item.PItems;
import com.altnoir.poopsky.tag.PBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class ToiletEntity extends Entity {
    private boolean goldenPoop;

    public ToiletEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    public void setGoldenPoop(boolean goldenPoop) {
        this.goldenPoop = goldenPoop;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.goldenPoop = compoundTag.getBoolean("goldenPoop");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putBoolean("goldenPoop", this.goldenPoop);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        if (!this.level().getBlockState(this.blockPosition()).is(PBlockTags.TOILET_BLOCKS)) {
            this.kill();
            return;
        }
        if (this.getPassengers().isEmpty()) {
            this.kill();
            return;
        }
        Entity firstPassenger = this.getPassengers().getFirst();
        if (firstPassenger instanceof Player player) {
            if (player.hasEffect(PEffects.FECAL_INCONTINENCE)) {
                onPoop(level(), player, player.hasEffect(PEffects.INTESTINAL_SPASM));
                player.causeFoodExhaustion(0.05F);
            } else if (level().getGameTime() % 20 == 0) {
                onPoop(level(), player, player.hasEffect(PEffects.INTESTINAL_SPASM));
                player.causeFoodExhaustion(1.0F);
            }
        } else if (firstPassenger instanceof LivingEntity livingEntity) {
            if (livingEntity.hasEffect(PEffects.FECAL_INCONTINENCE)) {
                onPoop(level(), livingEntity, livingEntity.hasEffect(PEffects.INTESTINAL_SPASM));
            } else if (level().getGameTime() % 20 == 0) {
                onPoop(level(), livingEntity, livingEntity.hasEffect(PEffects.INTESTINAL_SPASM));
            }
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        passenger.setPos(this.getX(), this.getY() + 1.05F, this.getZ());
        super.removePassenger(passenger);
        if (!this.isRemoved()) {
            this.kill();
        }
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        super.positionRider(passenger, callback);
        if (passenger instanceof Player player) {
            player.setPos(this.getX(), this.getY() + 0.45, this.getZ());
        } else {
            passenger.setPos(this.getX(), this.getY() + 0.95, this.getZ());
        }
    }

    protected void onPoop(Level level, LivingEntity livingEntity, boolean isFire) {
        boolean ass = true;
        float y = livingEntity instanceof Player ? 0.55F : 0.05F;

        if (livingEntity instanceof Player player) {
            if (player.getFoodData().getFoodLevel() <= 0) {
                ass = false;
                player.hurt(level.damageSources().wither(), 1.0F);

                var redStone = new ItemEntity(
                        level,
                        player.getX(), player.getY() + 0.1, player.getZ(),
                        new ItemStack(Items.REDSTONE)
                );
                redStone.setDefaultPickUpDelay();
                level.addFreshEntity(redStone);
            }
            player.awardStat(PStats.POOP_STATS.get());
        }
        if (ass) {
            Item poopItem;
            if (isFire) {
                poopItem = PItems.CHILI_POOP.get();
            } else if (goldenPoop) {
                poopItem = PItems.GOLDEN_POOP.get();
            } else {
                poopItem = PItems.POOP.get();
            }
            var poop = new ItemEntity(level, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), new ItemStack(poopItem));

            poop.setDefaultPickUpDelay();
            level.addFreshEntity(poop);
        }
        var pitch = level.random.nextFloat() + 0.5F;
        level.playSound(null, livingEntity.getX(), livingEntity.getY() + y, livingEntity.getZ(), PSoundEvents.FART.get(), SoundSource.PLAYERS, 1.0F, pitch);
        ((ServerLevel) level).sendParticles(
                PParticles.POOP_PARTICLE.get(),
                livingEntity.getX(),
                livingEntity.getY() + y,
                livingEntity.getZ(),
                8,
                0.0, -0.1, 0.0,
                3.0
        );
    }
}