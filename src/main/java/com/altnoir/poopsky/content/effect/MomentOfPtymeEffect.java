package com.altnoir.poopsky.content.effect;

import com.altnoir.poopsky.content.item.p.TimeBellItem;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class MomentOfPtymeEffect extends MobEffect {
    public MomentOfPtymeEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide && livingEntity instanceof Player player) {
            var server = livingEntity.level().getServer();
            if (server != null && com.altnoir.poopsky.impl.PoTimeFreeze.serverFrozen()) {
                TimeBellItem.unfreeze(server, player);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration <= 1;
    }
}
