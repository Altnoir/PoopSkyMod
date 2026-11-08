package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.impl.type.damageType.PoDamageTypes;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class PoopBall extends ThrowableItemProjectile {

    public PoopBall(EntityType<? extends PoopBall> type, Level level) {
        super(type, level);
    }

    public PoopBall(Level level, LivingEntity shooter) {
        super(com.altnoir.poopsky.init.PoEntityType.POOP_BALL.get(), shooter, level);
    }

    public PoopBall(Level level, double x, double y, double z) {
        super(com.altnoir.poopsky.init.PoEntityType.POOP_BALL.get(), x, y, z, level);
    }

    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getAddEntityPacket() {
        return net.minecraftforge.network.NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected Item getDefaultItem() {
        return PoItems.POOP_BALL.get();
    }

    private ParticleOptions getParticle() {
        ItemStack itemstack = this.getItem();
        return new ItemParticleOption(ParticleTypes.ITEM, itemstack);
    }

    /**
     * Handles an entity event received from a {@link net.minecraft.network.protocol.game.ClientboundEntityEventPacket}.
     */
    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleOptions particleoptions = this.getParticle();

            for (int i = 0; i < 8; i++) {
                this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    /**
     * Called when the arrow hits an entity
     */
    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        int i = entity instanceof Slime ? 3 : 1;
        entity.hurt(PoDamageTypes.source(this.level(), PoDamageTypes.POOP_BALL, this, this.getOwner()), (float) i);

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0), this.getOwner());
            livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0), this.getOwner());
        }
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            if (result.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) result;
                BlockPos originPos = blockHitResult.getBlockPos();
                Level level = this.level();
                ItemStack stack = this.getItem();

                BlockPos.betweenClosedStream(originPos.offset(-3, -1, -3), originPos.offset(3, 1, 3))
                        .forEach(targetPos -> {
                            boolean applied = BoneMealItem.applyBonemeal(stack, level, targetPos, null)
                                    || BoneMealItem.growWaterPlant(stack, level, targetPos, null);

                            if (applied) {
                                BoneMealItem.addGrowthParticles(level, targetPos, 15);
                            }
                        });
                level.levelEvent(1505, originPos, 15);
                if (this.getOwner() instanceof ServerPlayer player) {
                    player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                }
            }
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }
}