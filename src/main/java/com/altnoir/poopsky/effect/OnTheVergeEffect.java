package com.altnoir.poopsky.effect;

import com.altnoir.poopsky.init.PEffects;
import com.altnoir.poopsky.util.PoopTntUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class OnTheVergeEffect extends MobEffect {
    public OnTheVergeEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        Level level = livingEntity.level();
        boolean result = false;

        if (!level.isClientSide) {
            int duration = Objects.requireNonNull(livingEntity.getEffect(PEffects.ON_THE_VERGE)).getDuration();
            Vec3 vec3 = livingEntity.getDeltaMovement().add(new Vec3(0, 0.125, 0));
            boolean openTheDoor = false;

            if ((livingEntity instanceof Player player && player.isShiftKeyDown()) || duration <= 2) {
                openTheDoor = true;
                result = true;
            } else if (amplifier >= 1 && duration > 200) {
                openTheDoor = true;
            }

            if (openTheDoor) {
                livingEntity.setDeltaMovement(vec3);
                int radius = Math.min(18, amplifier + 2);
                PoopTntUtil.triggerExplosion(livingEntity, radius);
            }
        }

        if (result) {
            livingEntity.removeEffect(PEffects.ON_THE_VERGE);
        }
        return true;
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}