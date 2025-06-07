package com.altnoir.poopsky.entity;

import com.altnoir.poopsky.item.PSItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PoopBallEntity extends ThrownItemEntity {
    public PoopBallEntity(EntityType<? extends PoopBallEntity> entityType, World world) {
        super(entityType, world);
    }
    public PoopBallEntity(World world, LivingEntity owner) {
        super(EntityType.SNOWBALL, owner, world);
    }
    public PoopBallEntity(World world, double x, double y, double z) {
        super(EntityType.SNOWBALL, x, y, z, world);

    }
    protected Item getDefaultItem() {
        return PSItems.POOP_BALL;
    }
    @Override
    public void setItem(ItemStack stack) {
        super.setItem(stack);

    }
    public void handleStatus(byte status) {
        if (status == 3) {
            ParticleEffect particleEffect = this.getParticleParameters();

            for(int i = 0; i < 8; ++i) {
                this.getWorld().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }
    private ParticleEffect getParticleParameters() {
        ItemStack itemStack = this.getStack();
        return (ParticleEffect)(!itemStack.isEmpty() && !itemStack.isOf(this.getDefaultItem()) ? new ItemStackParticleEffect(ParticleTypes.ITEM, itemStack) : ParticleTypes.ITEM_SNOWBALL);
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        int i = entity instanceof SlimeEntity ? 3 : 0;
        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), (float)i);

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.NAUSEA,
                    100,
                    0
            ), this.getOwner());
        }
    }

    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                BlockPos originPos = blockHitResult.getBlockPos();
                World world = this.getWorld();
                ItemStack stack = this.getStack();

                BlockPos.stream(originPos.add(-3, -1, -3), originPos.add(3, 1, 3))
                        .forEach(targetPos -> {
                            boolean fertilized = BoneMealItem.useOnFertilizable(stack, world, targetPos);
                            if (!fertilized) {
                                BoneMealItem.useOnGround(stack, world, targetPos, null);
                            }
                        });

                world.syncWorldEvent(1505, originPos, 15);
            }

            this.getWorld().sendEntityStatus(this, (byte)3);
            this.discard();
        }
    }
}
