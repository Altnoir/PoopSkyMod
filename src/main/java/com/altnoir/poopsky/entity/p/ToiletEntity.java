package com.altnoir.poopsky.entity.p;

import com.altnoir.poopsky.init.PEffects;
import com.altnoir.poopsky.init.PStats;
import com.altnoir.poopsky.item.PItems;
import com.altnoir.poopsky.init.PParticles;
import com.altnoir.poopsky.init.PSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {

    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide || this.getPassengers().isEmpty()) {
            return;
        }

        if (!(this.getPassengers().getFirst() instanceof Player player)) {
            return;
        }

        if (player.hasEffect(PEffects.FECAL_INCONTINENCE)) {
            onPoop(level(), player, player.hasEffect(PEffects.INTESTINAL_SPASM));
            player.causeFoodExhaustion(0.05F);
        } else if (level().getGameTime() % 20 == 0) {
            onPoop(level(), player, player.hasEffect(PEffects.INTESTINAL_SPASM));
            player.causeFoodExhaustion(1.0F);
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        passenger.setPos(passenger.getX(), passenger.getY() + 1.5, passenger.getZ());
        super.removePassenger(passenger);
        this.kill();
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        super.positionRider(passenger, callback);
        passenger.setPos(this.getX(), this.getY() + 0.5, this.getZ());
    }

    protected void onPoop(Level level, Player player, boolean isFire) {
        if (player.getFoodData().getFoodLevel() <= 0) {
            player.hurt(level.damageSources().wither(), 1.0F);

            var redStone = new ItemEntity(
                    level,
                    player.getX(), player.getY() + 0.1, player.getZ(),
                    new ItemStack(Items.REDSTONE)
            );
            redStone.setDefaultPickUpDelay();
            level.addFreshEntity(redStone);
        } else {
            Item poopItem;
            if (isFire) {
                poopItem = PItems.CHILI_POOP.get();
            } else if (goldenPoop) {
                poopItem = PItems.GOLDEN_POOP.get();
            } else {
                poopItem = PItems.POOP.get();
            }
            var poop = new ItemEntity(level, player.getX(), player.getY() + 0.1, player.getZ(), new ItemStack(poopItem));

            poop.setDefaultPickUpDelay();
            level.addFreshEntity(poop);
        }
        var pitch = level.random.nextFloat() + 0.5F;
        level.playSound(null, player.getX(), player.getY() + 0.1, player.getZ(), PSoundEvents.FART.get(), SoundSource.PLAYERS, 1.0F, pitch);
        ((ServerLevel) level).sendParticles(
                PParticles.POOP_PARTICLE.get(),
                player.getX(),
                player.getY() + 0.1,
                player.getZ(),
                8,
                0.0,
                -0.1,
                0.0,
                3.0
        );
        player.awardStat(PStats.POOP_STATS.get());
    }
}