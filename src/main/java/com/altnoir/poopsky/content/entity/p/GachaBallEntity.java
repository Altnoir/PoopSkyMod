package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.init.PoComponents;
import com.altnoir.poopsky.init.PoEntityType;
import com.altnoir.poopsky.init.PoItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class GachaBallEntity extends ThrowableItemProjectile {
    public GachaBallEntity(EntityType<? extends GachaBallEntity> entityType, Level level) {
        super(entityType, level);
    }

    public GachaBallEntity(Level level, LivingEntity owner) {
        super(PoEntityType.GACHA_BALL.get(), owner, level);
    }

    public GachaBallEntity(Level level, double x, double y, double z) {
        super(PoEntityType.GACHA_BALL.get(), x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return PoItems.GACHA_BALL.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level() instanceof ServerLevel level) {
            Vec3 location = result.getLocation();
            if (result instanceof EntityHitResult entityHitResult) {
                location = entityHitResult.getEntity().position();
            } else if (result instanceof BlockHitResult blockHitResult) {
                Direction normal = blockHitResult.getDirection();
                location = location.add(normal.getStepX() * 0.5D,
                        normal.getStepY() * 0.5D,
                        normal.getStepZ() * 0.5D);
            }
            spawnContents(level, location);
            level.broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ItemParticleOption particle = new ItemParticleOption(ParticleTypes.ITEM, this.getItem());
            for (int i = 0; i < 8; i++) {
                this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(),
                        0.0D, 0.0D, 0.0D);
            }
            return;
        }
        super.handleEntityEvent(id);
    }

    private void spawnContents(ServerLevel level, Vec3 location) {
        String id = this.getItem().get(PoComponents.GACHA_ENTITY.get());
        if (id == null) {
            return;
        }
        ResourceLocation resourceLocation = ResourceLocation.tryParse(id);
        if (resourceLocation == null) {
            return;
        }
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(resourceLocation).orElse(null);
        if (entityType == null || !entityType.canSummon()) {
            return;
        }
        Entity entity = entityType.create(level);
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        livingEntity.moveTo(location.x(), location.y(), location.z(), level.random.nextFloat() * 360.0F, 0.0F);
        if (livingEntity instanceof Mob mob) {
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(BlockPos.containing(location)),
                    MobSpawnType.TRIGGERED, null);
        }
        level.addFreshEntity(livingEntity);
    }
}
