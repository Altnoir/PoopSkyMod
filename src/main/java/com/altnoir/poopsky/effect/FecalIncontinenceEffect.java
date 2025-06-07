package com.altnoir.poopsky.effect;

import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.sound.PSSoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FecalIncontinenceEffect extends MobEffect {

    public FecalIncontinenceEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        int chance = entity.getRandom().nextInt(Math.max(1, 20 - amplifier * 4));
        if (chance == 0) {
            if (entity instanceof Player player && !player.isSpectator()) {
                player.causeFoodExhaustion(0.1F * (amplifier + 2));
                fecalIncontinence(player);
            } else {
                fecalIncontinence(entity);
            }
        }
        return true;
    }

    private void fecalIncontinence(LivingEntity entity) {
        float pitch = entity.getRandom().nextFloat() + 0.5F;

        if (!entity.level().isClientSide) {
            var world = (ServerLevel) entity.level();

            var poop = new ItemEntity(
                    world,
                    entity.getX(),
                    entity.getY() + 0.1,
                    entity.getZ(),
                    new ItemStack(PSItems.POOP.get())
            );
            poop.setDefaultPickUpDelay();
            world.addFreshEntity(poop);

            /*
            world.sendParticles(
                    PSParticle.POOP_PARTICLE.get(),
                    entity.getX(), entity.getY() + 0.1, entity.getZ(),
                    8, 0.0, -0.1, 0.0, 3.0
            );
             */

            world.playSound(
                    null,
                    entity.getX(), entity.getY() + 0.1, entity.getZ(),
                    PSSoundEvents.FART.get(),
                    entity.getSoundSource(),
                    1.0F,
                    pitch
            );
        }
    }
}
