package com.altnoir.poopsky.content.effect;

import com.altnoir.poopsky.content.block.p.CompooperBlock;
import com.altnoir.poopsky.content.entity.p.FlushToiletEntity;
import com.altnoir.poopsky.impl.util.ToiletUtil;
import com.altnoir.poopsky.init.PoEffects;
import com.altnoir.poopsky.init.PoItems;
import com.altnoir.poopsky.init.PoParticles;
import com.altnoir.poopsky.init.PoSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public class FecalIncontinenceEffect extends MobEffect {

    public FecalIncontinenceEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        int chance = entity.getRandom().nextInt(Math.max(1, 20 - amplifier * 5));
        if (amplifier >= 1 && entity instanceof Player player && player.isShiftKeyDown()) {
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

        if (!entity.level().isClientSide()) {
            var vehicle = entity.getVehicle();
            if (vehicle instanceof FlushToiletEntity) {
                return;
            }

            var level = (ServerLevel) entity.level();
            BlockPos entityPos = entity.blockPosition();

            if (!entity.hasEffect(PoEffects.INTESTINAL_SPASM)) {
                BlockState compooperState = level.getBlockState(entityPos);
                if (compooperState.getBlock() instanceof CompooperBlock) {
                    BlockState state = level.getBlockState(entityPos);
                    int pl = state.getValue(CompooperBlock.POOP_LEVEL);
                    if (pl < 7 && CompooperBlock.isEntityInsideContent(entityPos, entity)) {
                        BlockState newState = state.setValue(CompooperBlock.POOP_LEVEL, pl + 1);
                        level.setBlockAndUpdate(entityPos, newState);
                        level.gameEvent(GameEvent.BLOCK_CHANGE, entityPos, GameEvent.Context.of(entity, newState));
                        level.levelEvent(1500, entityPos, 1);
                        return;
                    }
                }
            }

            Item stack = PoItems.POOP.get();

            boolean dropPoop = false;
            if (!entity.hasEffect(PoEffects.INTESTINAL_SPASM)) {
                dropPoop = BlockPos.betweenClosedStream(entityPos.offset(0, -1, 0), entityPos.offset(0, 1, 0))
                        .anyMatch(targetPos -> {
                            boolean applied = BoneMealItem.applyBonemeal(new ItemStack(PoItems.POOP.get()), level, targetPos, null)
                                    || BoneMealItem.growWaterPlant(new ItemStack(PoItems.POOP.get()), level, targetPos, null);

                            if (applied) {
                                BoneMealItem.addGrowthParticles(level, targetPos, 15);
                                level.levelEvent(1505, entityPos, 15);
                            }
                            return applied;
                        });
            }

            if (entity.hasEffect(PoEffects.INTESTINAL_SPASM)) {
                stack = PoItems.CHILI_POOP.get();
            } else if (ToiletUtil.isGoldenToilet(level, entityPos.below())) {
                stack = PoItems.GOLDEN_POOP.get();
            }

            ItemStack finalStack = new ItemStack(stack);
            if (!dropPoop) {
                var poop = new ItemEntity(level, entity.getX(), entity.getY() + 0.1, entity.getZ(), finalStack);

                if (amplifier >= 1) {
                    if (entity.isFallFlying()) {
                        Vec3 look = entity.getLookAngle();
                        double speed = 0.5 + amplifier * 0.025;
                        entity.push(look.x * speed, look.y * speed, look.z * speed);
                    } else {
                        entity.setDeltaMovement(entity.getDeltaMovement().add(new Vec3(0, 0.125, 0)));
                    }
                    entity.fallDistance = 0;
                    entity.hurtMarked = true;
                }
                poop.setDefaultPickUpDelay();
                level.addFreshEntity(poop);

                level.sendParticles(
                        PoParticles.POOP_PARTICLE.get(),
                        entity.getX(), entity.getY() + 0.1, entity.getZ(),
                        8, 0.0, -0.1, 0.0, 3.0
                );

                level.playSound(null,
                        entity.getX(), entity.getY() + 0.1, entity.getZ(),
                        PoSoundEvents.FART.get(), entity.getSoundSource(),
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