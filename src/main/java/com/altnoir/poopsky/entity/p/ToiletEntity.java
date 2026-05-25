package com.altnoir.poopsky.entity.p;

import com.altnoir.poopsky.compat.PSMods;
import com.altnoir.poopsky.effect.PSEffects;
import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.particle.PSParticles;
import com.altnoir.poopsky.sound.PSSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class ToiletEntity extends Entity {
    public ToiletEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

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
        if (!this.level().isClientSide && !this.getPassengers().isEmpty()) {
            LivingEntity livingEntity = (LivingEntity) this.getPassengers().get(0);

            if (livingEntity instanceof Player player) {
                if (player.hasEffect(PSEffects.FECAL_INCONTINENCE)) {
                    onPoop(level(), player, player.hasEffect(PSEffects.INTESTINAL_SPASM));
                    player.causeFoodExhaustion(0.05F);

                } else if (level().getGameTime() % 20 == 0) {
                    onPoop(level(), (Player) livingEntity, livingEntity.hasEffect(PSEffects.INTESTINAL_SPASM));
                    player.causeFoodExhaustion(1.0F);
                }
            }
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
            var poop = new ItemEntity(level, player.getX(), player.getY() + 0.1, player.getZ(), new ItemStack(PSItems.POOP.get()));
            var chili_poop = new ItemEntity(level, player.getX(), player.getY() + 0.1, player.getZ(), new ItemStack(PSItems.CHILI_POOP.get()));

            poop.setDefaultPickUpDelay();
            chili_poop.setDefaultPickUpDelay();

            level.addFreshEntity(isFire ? chili_poop : poop);
        }
        var pitch = level.random.nextFloat() + 0.5F;
        level.playSound(null, player.getX(), player.getY() + 0.1, player.getZ(), PSSoundEvents.FART.get(), SoundSource.PLAYERS, 1.0F, pitch);
        ((ServerLevel) level).sendParticles(
                PSParticles.POOP_PARTICLE.get(),
                player.getX(),
                player.getY() + 0.1,
                player.getZ(),
                8,
                0.0,
                -0.1,
                0.0,
                3.0
        );
    }
}
