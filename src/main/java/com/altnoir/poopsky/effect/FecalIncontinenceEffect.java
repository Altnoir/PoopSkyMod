package com.altnoir.poopsky.effect;

import com.altnoir.poopsky.item.PSItems;
import com.altnoir.poopsky.particle.PSParticles;
import com.altnoir.poopsky.sound.PSSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class FecalIncontinenceEffect extends MobEffect {

    public FecalIncontinenceEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        int chance = entity.getRandom().nextInt(Math.max(1, 20 - amplifier * 5));
        if (amplifier >= 3 && entity instanceof Player player && player.isShiftKeyDown()) {
            player.causeFoodExhaustion(0.1F * (amplifier + 2));
            fecalIncontinence(player, amplifier);
        } else if (chance == 0) {
            if (entity instanceof Player player && !player.isSpectator()) {
                player.causeFoodExhaustion(0.1F * (amplifier + 2));
                fecalIncontinence(player, amplifier);
            } else {
                fecalIncontinence(entity, amplifier);
            }
        }
        return true;
    }

    private void fecalIncontinence(LivingEntity entity, int amplifier) {
        float pitch = entity.getRandom().nextFloat() + 0.5F;

        if (!entity.level().isClientSide) {
            var level = (ServerLevel) entity.level();
            BlockPos entityPos = entity.blockPosition();

            ItemStack stack = new ItemStack(PSItems.POOP.get());
            if (entity.hasEffect(PSEffects.INTESTINAL_SPASM)) {
                stack = new ItemStack(PSItems.CHILI_POOP.get());
            }
            ItemStack finalStack = stack;
            boolean dropPoop = BlockPos.betweenClosedStream(entityPos.offset(0, -1, 0), entityPos.offset(0, 1, 0))
                    .anyMatch(targetPos -> {
                        boolean applied = BoneMealItem.applyBonemeal(finalStack, level, targetPos, null)
                                || BoneMealItem.growWaterPlant(finalStack, level, targetPos, null);

                        if (applied) {
                            BoneMealItem.addGrowthParticles(level, targetPos, 15);
                            level.levelEvent(1505, entityPos, 15);
                        }
                        return applied;
                    });

            if (!dropPoop) {
                var poop = new ItemEntity(level, entity.getX(), entity.getY() + 0.1, entity.getZ(), stack);

                if (amplifier > 1) {
                    entity.setDeltaMovement(entity.getDeltaMovement().add(new Vec3(0, 0.125, 0)));
                    entity.fallDistance = 0;
                    entity.hurtMarked = true;
                }
                poop.setDefaultPickUpDelay();
                level.addFreshEntity(poop);

                level.sendParticles(
                        PSParticles.POOP_PARTICLE.get(),
                        entity.getX(), entity.getY() + 0.1, entity.getZ(),
                        8, 0.0, -0.1, 0.0, 3.0
                );

                level.playSound(null,
                        entity.getX(), entity.getY() + 0.1, entity.getZ(),
                        PSSoundEvents.FART.get(), entity.getSoundSource(),
                        1.0F, pitch
                );
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
