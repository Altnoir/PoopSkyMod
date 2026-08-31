package com.altnoir.poopsky.content.entity.p;

import com.altnoir.poopsky.init.PoEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class SnailEntity extends Animal {
    private static final EntityDataAccessor<Integer> DATA_COLOR =
            SynchedEntityData.defineId(SnailEntity.class, EntityDataSerializers.INT);
    private static final int COLOR_COUNT = Color.values().length;

    public SnailEntity(EntityType<? extends SnailEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.08);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.65));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_COLOR, 0);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, net.minecraft.world.DifficultyInstance difficulty,
                                        MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
        this.setColor(Color.byId(this.random.nextInt(COLOR_COUNT)));
        return super.finalizeSpawn(level, difficulty, reason, spawnData);
    }

    public Color getColor() {
        return Color.byId(this.entityData.get(DATA_COLOR));
    }

    public void setColor(Color color) {
        this.entityData.set(DATA_COLOR, color.ordinal());
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.DANDELION);
    }

    @Nullable
    @Override
    public SnailEntity getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        SnailEntity child = PoEntityType.SNAIL.get().create(level);
        if (child != null) {
            child.setColor(Color.byId(this.random.nextInt(COLOR_COUNT)));
        }
        return child;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Color", this.getColor().ordinal());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setColor(Color.byId(tag.getInt("Color")));
    }

    public enum Color {
        YELLOW,
        RED,
        BROWN,
        BLACK;

        public static Color byId(int id) {
            return values()[Math.floorMod(id, COLOR_COUNT)];
        }
    }
}
