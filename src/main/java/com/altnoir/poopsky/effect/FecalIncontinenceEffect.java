package com.altnoir.poopsky.effect;

import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.particle.PSParticle;
import com.altnoir.poopsky.sound.PSSoundEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class FecalIncontinenceEffect extends StatusEffect {
    public FecalIncontinenceEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        int i = entity.getRandom().nextInt(Math.max(1, 20 - amplifier * 4));
        if (i == 0) {
            if (entity instanceof PlayerEntity playerEntity) {
                if (!playerEntity.isSpectator()) {
                    playerEntity.addExhaustion(0.1F * (amplifier + 2));
                    fecalIncontinence(playerEntity);
                }
            } else {
                fecalIncontinence(entity);
            }
        }

        return true;
    }

    private void fecalIncontinence(LivingEntity entity) {
        float pitch = entity.getRandom().nextFloat() + 0.5F;

        if (!entity.getWorld().isClient) {
            ItemEntity poop = new ItemEntity(
                    entity.getWorld(),
                    entity.getX(),
                    entity.getY() + 0.1,
                    entity.getZ(),
                    new ItemStack(PSItems.POOP)
            );
            poop.setPickupDelay(20);
            entity.getWorld().spawnEntity(poop);

            ((ServerWorld) entity.getWorld()).spawnParticles(PSParticle.POOP_PARTICLE,
                    entity.getX(), entity.getY() + 0.1, entity.getZ(),
                    8, 0.0, -0.1, 0.0, 3.0);

            entity.getWorld().playSound(null,
                    entity.getX(),
                    entity.getY() + 0.1,
                    entity.getZ(),
                    PSSoundEvents.FART,
                    entity.getSoundCategory(),
                    1.0F, pitch
            );
        }
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
