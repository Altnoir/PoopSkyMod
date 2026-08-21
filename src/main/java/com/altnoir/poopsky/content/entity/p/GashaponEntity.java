package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.PoopSky;
import com.altnoir.poopsky.content.item.p.GashaponItem;
import com.altnoir.poopsky.init.PoEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.HitResult;

public class GashaponEntity extends ThrowableProjectile {
    private static final EntityDataAccessor<Byte> DATA_VARIANT = SynchedEntityData.defineId(GashaponEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<String> DATA_MOB_ID = SynchedEntityData.defineId(GashaponEntity.class, EntityDataSerializers.STRING);

    public GashaponEntity(EntityType<? extends GashaponEntity> entityType, Level level) {
        super(entityType, level);
        if (!level.isClientSide()) {
            this.setVariant((byte) this.random.nextInt(4));
        }
    }

    public static GashaponEntity create(EntityType<? extends GashaponEntity> entityType, Level level) {
        return new GashaponEntity(entityType, level);
    }

    public GashaponEntity(Level level, LivingEntity shooter) {
        super(PoEntityType.GASHAPON.get(), level);
        this.setOwner(shooter);
        this.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
    }

    public GashaponEntity(Level level, double x, double y, double z) {
        super(PoEntityType.GASHAPON.get(), x, y, z, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_VARIANT, (byte) 0);
        builder.define(DATA_MOB_ID, "");
    }

    public byte getVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public void setVariant(byte variant) {
        this.entityData.set(DATA_VARIANT, variant);
    }

    public String getMobId() {
        return this.entityData.get(DATA_MOB_ID);
    }

    public void setMobId(String mobId) {
        this.entityData.set(DATA_MOB_ID, mobId);
    }

    private ParticleOptions getHitParticle() {
        return ParticleTypes.ITEM_SNOWBALL;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleOptions particle = this.getHitParticle();
            for (int i = 0; i < 8; i++) {
                this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    public static byte variantFromColor(String color) {
        return switch (color) {
            case GashaponItem.RED -> 1;
            case GashaponItem.YELLOW -> 2;
            case GashaponItem.BLUE -> 3;
            default -> 0;
        };
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setVariant(input.getByteOr("Variant", (byte) 0));
        this.setMobId(input.getStringOr("MobId", ""));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putByte("Variant", this.getVariant());
        output.putString("MobId", this.getMobId());
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide()) {
            if (this.getMobId().isBlank()) {
                this.level().broadcastEntityEvent(this, (byte) 3);
            } else {
                this.spawnMob();
            }
            this.discard();
        }
    }

    private void spawnMob() {
        String mobId = this.getMobId();
        if (mobId.isBlank()) {
            return;
        }

        Identifier id = PoopSky.tryParse(mobId);
        if (id == null) {
            return;
        }

        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.getValue(id);
        if (entityType == null) return;
        Entity entity = entityType.create(this.level(), EntitySpawnReason.TRIGGERED);
        if (entity != null) {
            entity.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            this.level().addFreshEntity(entity);

            var pos = new BlockPos((int) this.getX(), (int) this.getY(), (int) this.getZ());
            this.level().playSound(entity, pos, SoundEvents.CHICKEN_EGG, SoundSource.PLAYERS, 0.5F, 1.0F);
        }
    }
}
