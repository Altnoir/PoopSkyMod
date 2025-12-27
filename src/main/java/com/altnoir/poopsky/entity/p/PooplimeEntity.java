package com.altnoir.poopsky.entity.p;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;

public class PooplimeEntity extends Slime {
    public PooplimeEntity(EntityType<PooplimeEntity> entityType, Level level) {
        super(entityType, level);
    }
}