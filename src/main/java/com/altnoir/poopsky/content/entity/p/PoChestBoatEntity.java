package com.altnoir.poopsky.content.entity.p;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

public class PoChestBoatEntity extends ChestBoat {
    public PoChestBoatEntity(EntityType<? extends ChestBoat> entityType, Level level, Supplier<Item> dropItem) {
        super(entityType, level, dropItem);
    }
}
