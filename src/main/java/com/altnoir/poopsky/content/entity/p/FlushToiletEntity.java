package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.content.block.entity.FlushToiletBlockEntity;
import com.altnoir.poopsky.content.block.p.FlushToiletBlock;
import com.altnoir.poopsky.impl.PoTags;
import com.altnoir.poopsky.impl.sound.PoSoundEvents;
import com.altnoir.poopsky.init.PoEffects;
import com.altnoir.poopsky.init.PoItems;
import com.altnoir.poopsky.init.PoParticles;
import com.altnoir.poopsky.init.PoStats;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.ItemStackHandler;

public class FlushToiletEntity extends Entity {
    private long poopTime;

    public FlushToiletEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.poopTime = compoundTag.getLong("poopTime");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putLong("poopTime", this.poopTime);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        if (!(this.level().getBlockState(this.blockPosition()).getBlock() instanceof FlushToiletBlock)) {
            this.kill();
            return;
        }
        if (this.getPassengers().isEmpty()) {
            this.kill();
            return;
        }

        Entity passenger = this.getPassengers().getFirst();
        if (passenger instanceof LivingEntity livingEntity) {
            this.setYRot(this.level().getBlockState(this.blockPosition()).getValue(FlushToiletBlock.FACING).toYRot());

            boolean hasSpasm = livingEntity.hasEffect(PoEffects.INTESTINAL_SPASM);
            int interval = hasSpasm ? 2 : 20;
            long gameTime = level().getGameTime();
            if (poopTime != 0 && gameTime - poopTime < interval) return;

            if (passenger instanceof Player player) {
                if (player.getFoodData().getFoodLevel() <= 0) {
                    player.hurt(level().damageSources().wither(), 1.0F);
                    insertOrReplace(level(), blockPosition(), Items.REDSTONE.getDefaultInstance());
                    poopTime = gameTime;
                    return;
                }
                player.awardStat(PoStats.POOP_STATS.get());
                player.causeFoodExhaustion(hasSpasm ? 0.05F : 1.0F);
            }

            Item poopItem = PoItems.POOP.get();
            if (hasSpasm) {
                poopItem = PoItems.CHILI_POOP.get();
            }

            if (insertOrReplace(level(), blockPosition(), new ItemStack(poopItem))) {
                poopTime = gameTime;
            }

            float yOffset = passenger instanceof Player ? 0.55F : 0.05F;
            float pitch = level().random.nextFloat() + 0.5F;
            level().playSound(null, passenger.getX(), passenger.getY() + yOffset, passenger.getZ(), PoSoundEvents.FART.get(), SoundSource.PLAYERS, 1.0F, pitch);
            ((ServerLevel) level()).sendParticles(
                    PoParticles.POOP_PARTICLE.get(),
                    passenger.getX(),
                    passenger.getY() + yOffset,
                    passenger.getZ(),
                    8, 0.0, -0.1, 0.0, 3.0
            );
        }
    }

    private static boolean insertOrReplace(Level level, BlockPos pos, ItemStack stack) {
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

    @Override
    public void onPassengerTurned(Entity entityToUpdate) {

    }

    @Override
    protected void removePassenger(Entity passenger) {
        if (passenger instanceof LivingEntity living) {
            living.setDeltaMovement(living.getDeltaMovement().add(0.0, 0.4, 0.0));
            living.hasImpulse = true;
        }
        super.removePassenger(passenger);
        if (!this.isRemoved()) {
            this.kill();
        }
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction callback) {
        super.positionRider(passenger, callback);
        if (passenger instanceof Player) {
            callback.accept(passenger, this.getX(), this.getY() - 0.05, this.getZ());
        } else {
            callback.accept(passenger, this.getX(), this.getY() + 0.4, this.getZ());
        }
    }
}