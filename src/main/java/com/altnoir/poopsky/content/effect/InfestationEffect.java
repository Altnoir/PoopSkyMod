package com.altnoir.poopsky.content.effect;

import com.altnoir.poopsky.init.PoEntityType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;

public class InfestationEffect extends MobEffect {
    private static final float SPAWN_CHANCE = 0.1F;

    public InfestationEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onMobHurt(ServerLevel level, LivingEntity entity, int amplifier, DamageSource source, float damage) {
        if (entity.getRandom().nextFloat() >= SPAWN_CHANCE) {
            return;
        }

        int count = 1 + entity.getRandom().nextInt(2);
        for (int i = 0; i < count; i++) {
            spawnBabyFly(entity);
        }
    }

    private static void spawnBabyFly(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel level)) return;
        var fly = PoEntityType.FLY.get().create(level, EntitySpawnReason.TRIGGERED);
        if (fly == null) {
            return;
        }

        fly.setBaby(true);
        fly.snapTo(entity.getX() + entity.getRandom().nextGaussian() * 0.35,
                entity.getY() + entity.getBbHeight() * 0.5,
                entity.getZ() + entity.getRandom().nextGaussian() * 0.35,
                entity.getRandom().nextFloat() * 360.0F, 0.0F);
        level.addFreshEntity(fly);
    }
}
