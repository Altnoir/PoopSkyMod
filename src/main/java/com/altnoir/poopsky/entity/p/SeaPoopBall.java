package com.altnoir.poopsky.entity.p;

import com.altnoir.poopsky.item.PSItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class SeaPoopBall extends ThrowableItemProjectile {
    public SeaPoopBall(Level level, LivingEntity shooter) {
        super(EntityType.SNOWBALL, shooter, level);
    }

    public SeaPoopBall(Level level, double x, double y, double z) {
        super(EntityType.SNOWBALL, x, y, z, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return PSItems.SEA_POOP_BALL.get();
    }

    private ParticleOptions getParticle() {
        ItemStack itemstack = this.getItem();
        return !itemstack.isEmpty() && !itemstack.is(this.getDefaultItem()) ? new ItemParticleOption(ParticleTypes.ITEM, itemstack) : ParticleTypes.ITEM_SNOWBALL;
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

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        int i = entity instanceof Slime ? 3 : 0;
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), (float) i);

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0), this.getOwner());
            livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 400, 0), this.getOwner());
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }
}
